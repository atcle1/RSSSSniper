package com.atcle.rsssniper;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.parser.RSSParser;
import com.atcle.rsssniper.rss.FeedItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyFeedActivity extends Activity {
	Intent resultIntent = new Intent();

	EditText urlEditText;
	EditText nickEditText;
	EditText PIEditText;
	ProgressDialog pd;
	Handler handler;
	FeedItem parseResult;
	RSSSniperMain main;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.modify_feed_dlg);
		setResult(Activity.RESULT_CANCELED, resultIntent);
		main=((RSSSniperApplication)getApplication()).getMain();
		
		parseResult=new FeedItem("", "", 20);

		urlEditText=(EditText)findViewById(R.id.modify_feed_url);
		nickEditText=(EditText)findViewById(R.id.modify_feed_nick);
		PIEditText=(EditText)findViewById(R.id.modify_feed_interval);
		
		//savedInstanceState.getInt("idx");
		int idx=getIntent().getIntExtra("idx", -1);
		parseResult=main.getFeedByIdx(idx);
		urlEditText.setText(parseResult.RSSUrl);
		nickEditText.setText(parseResult.nick);
		PIEditText.setText(String.valueOf(parseResult.pollingInterval));
		
		pd=new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setMessage("Connecting...");
		
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(msg.what==1){
					nickEditText.setText(parseResult.title);
					pd.cancel();
				}else{
					pd.cancel();
					Toast.makeText(ModifyFeedActivity.this, "업데이트 실패!", Toast.LENGTH_LONG).show();
				}
			}
		};
	}

	public void onClickConnect(View view){
		pd.show();
		parseResult.RSSUrl=urlEditText.getText().toString();
		nickEditText.setText("");
		receiveFeedWork2 rf=new receiveFeedWork2(this, main);
		rf.start();
	}
	public void onClickOK(View view){
		parseResult.RSSUrl=urlEditText.getText().toString();
		parseResult.nick=nickEditText.getText().toString();
		
		try{
			parseResult.pollingInterval=Integer.parseInt(PIEditText.getText().toString());
		}catch(Exception ex){
			parseResult.pollingInterval=20;
		}
		main.dbModifyFeed(parseResult);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
	public void onClickDelete(View view){
		main.dbDeleteFeedByIdx(parseResult.idx);
		setResult(Activity.RESULT_FIRST_USER, resultIntent);
		//삭제일경우 메인엑티비티에서 리드탭이 아닌 피드탭으로 보여줌
		finish();
	}
	public void onClickCancel(View view){
		setResult(Activity.RESULT_CANCELED, resultIntent);
		finish();
	}
}

class receiveFeedWork2 extends Thread{
	ModifyFeedActivity mAfActivity;
	RSSParser parser;
	RSSSniperMain main;
	public receiveFeedWork2(ModifyFeedActivity afActivity, RSSSniperMain amain){
		mAfActivity=afActivity;
		parser=new RSSParser();
		main=amain;
	}
	@Override
	public void run(){
		try {
			parser.parse(mAfActivity.parseResult,main.maxParseCount);
			MyLog.i("tag","update()+"+mAfActivity.parseResult.rssItems.size());
			mAfActivity.handler.sendEmptyMessage(1);
		} catch (Exception e) {
			mAfActivity.handler.sendEmptyMessage(2);
			MyLog.e("tag","update()+"+mAfActivity.parseResult.rssItems.size());
			e.printStackTrace();
		}
		
	}
}
