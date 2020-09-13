package com.atcle.rsssniper.recommend;

import java.util.ArrayList;
import java.util.Date;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperApplication;
import com.atcle.rsssniper.R.layout;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.parser.RSSParser;
import com.atcle.rsssniper.rss.FeedItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.AdapterView.OnItemSelectedListener;

public class RecommendFeedActivity extends Activity {
	/** Called when the activity is first created. */
	public static final int ROW_DEL=0;
	public static final int ROW_ROOT=1;
	public static final int ROW_CAT=2;
	public static final int ROW_RSS=3;
	public static final int ROW_OUT=4;
	public static final int ROW_TEXT=5;

	RSSSniperMain main;
	RecommendFeedMgr rfm;
	RecommendFeedListAdapter adapter;
	ListView listView;
	
	private SharedPreferences prefs;
	DBItem currentItem;
	ArrayList<DBItem> childItems;

	AlertDialog.Builder addRssDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend_feed);
		main=((RSSSniperApplication)getApplication()).getMain();

		rfm=new RecommendFeedMgr(this);
		
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		
		adapter=new RecommendFeedListAdapter(this, rfm);
		listView=(ListView)findViewById(R.id.recf_list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new onItemClickListener());

		currentItem=rfm.getItem(6);
		if(currentItem!=null)
			setIdx(currentItem.IDX);

		addRssDialog=new AlertDialog.Builder(this);
		addRssDialog.setTitle("다음 피드를 추가하시겠습니까?")
		.setPositiveButton("추가", new AddRssDialogPositiveListener())
		.setNegativeButton("취소", null);
	}

	public void onDestroy(){
		super.onDestroy();
		rfm.closeDb();
	}

	@Override
	public void onBackPressed(){
		if(currentItem!=null && currentItem.TYPES!=ROW_ROOT){
			setIdx(currentItem.PIDX);
		}else{
			finish();
		}
	}

	private void setIdx(int idx){
		DBItem tmp=rfm.getItem(idx);
		if(tmp!=null){
			currentItem=tmp;
			childItems=rfm.getChildItems(currentItem.IDX);

			adapter.setData(childItems);
			adapter.notifyDataSetChanged();
		}
	}

	class onItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(arg2==0){
				if(currentItem.TYPES!=ROW_ROOT){
					setIdx(currentItem.PIDX);
				}
			}else{
				DBItem item=childItems.get(arg2-1);
				MyLog.i("tag",arg2+" "+item.ORDER+": "+item.TITLE+" "+item.TYPES);
				switch(item.TYPES){
				case ROW_CAT:
					setIdx(item.IDX);
					break;
				case ROW_RSS:
					clickedItem=item;
					onClickRss(item);
					break;
				case ROW_OUT:
					onClickOut(item);
					break;
				}
			}
		}
	}

	private DBItem clickedItem;
	class AddRssDialogPositiveListener implements OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			FeedItem feed=new FeedItem(clickedItem.URL,"",20);
			boolean result=RecommendFeedActivity.this.main.insertFeed(feed);
			if(result){
				Toast.makeText(RecommendFeedActivity.this, "추가되었습니다.", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(RecommendFeedActivity.this, "이미 존재하는 피드입니다.", Toast.LENGTH_SHORT).show();
			}
		}

	}

	public void onClickRss(DBItem item){
		addRssDialog.setMessage(item.TITLE);
		addRssDialog.show();
	}
	public void onClickOut(DBItem item){
		try{
			Uri browserUri=Uri.parse(item.URL);
			Intent brower=new Intent(Intent.ACTION_VIEW, browserUri);
			this.startActivity(brower);
		}catch(Exception ex){ex.printStackTrace();}
	}
	
	
	ProgressDialog mProgressDlg;
	int updateResult;
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		Date t=Util.getDate(prefs, RecommendFeedMgr.LAST_UPDATE_KEY);
		String lastUpdate=null;
		try{
			lastUpdate=t.toLocaleString();
		}catch(Exception ex){
			lastUpdate=getApplicationContext().getString(R.string.recf_asset_date);
			Editor edit=prefs.edit();
			edit.putString(RecommendFeedMgr.LAST_UPDATE_KEY, lastUpdate);
			edit.commit();
			 MyLog.i("tag","lastUpdate reset");
			Toast.makeText(getApplicationContext(), "과거 버전입니다! 오류가 계속되면 어플을 지우고 재설치 해주세요. 불편을 끼쳐서 죄송합니다.", Toast.LENGTH_LONG).show();
		}
		new AlertDialog.Builder(this).setMessage("마지막 업데이트:\n  "+lastUpdate+"\n지금 업데이트 하시겠습니까?").setPositiveButton("update", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateResult=-1;
				mProgressDlg=new ProgressDialog(RecommendFeedActivity.this);
				mProgressDlg.setMessage("업데이트 중...");
				mProgressDlg.setCancelable(false);
				mProgressDlg.show();

				updateThread=new UpdateThread();
				updateThread.start();
			}
		}).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
		return super.onPrepareOptionsMenu(menu);
	}
	UpdateThread updateThread;
	class UpdateThread extends Thread{
		public void run() {
			try{
				updateResult=rfm.Sync();
			}catch(Exception ex){ex.printStackTrace();}
			handler.sendEmptyMessage(1);
		}
	}

	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mProgressDlg.dismiss();
				if(updateResult==-1){
					Toast.makeText(RecommendFeedActivity.this, "업데이트 실패", Toast.LENGTH_SHORT).show();
				}else if(updateResult==0){
					Toast.makeText(RecommendFeedActivity.this, "업데이트가 없습니다.", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(RecommendFeedActivity.this, "업데이트 완료", Toast.LENGTH_SHORT).show();
					RecommendFeedActivity.this.setIdx(6);
				}
				break;
			}
		}
	};
}
