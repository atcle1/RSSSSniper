package com.atcle.rsssniper;


import java.util.ArrayList;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddFeedActivity extends Activity {
	Intent resultIntent = new Intent();

	EditText urlEditText;
	EditText nickEditText;
	EditText PIEditText;
	ProgressDialog pd;
	Handler handler;
	FeedItem parseResult;
	RSSSniperMain main;

	Spinner sp;
	ArrayList<String> suggestList;
	ArrayAdapter<String> adapter;

	public void setSuggestSpinner(Spinner sp){
		suggestList=new ArrayList<String>();
		for(int i=0; i<sugestList.length; i++){
			suggestList.add(sugestList[i][0]);
		}
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,suggestList);
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				urlEditText.setText(sugestList[arg2][1]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		sp.setAdapter(adapter);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_feed_dlg);
		setResult(Activity.RESULT_CANCELED, resultIntent);

		parseResult=new FeedItem("", "", 20);

		urlEditText=(EditText)findViewById(R.id.add_feed_url);
		nickEditText=(EditText)findViewById(R.id.add_feed_nick);
		PIEditText=(EditText)findViewById(R.id.add_feed_interval);
		sp=(Spinner)findViewById(R.id.add_feed_suggest);
		setSuggestSpinner(sp);

		main=((RSSSniperApplication)getApplication()).getMain();

		pd=new ProgressDialog(this);
		//pd.setCancelable(false);
		pd.setMessage("Connecting...");

		handler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(msg.what==1){
					nickEditText.setText(parseResult.title);
					pd.cancel();
				}else{
					pd.cancel();
					Toast.makeText(AddFeedActivity.this, "������Ʈ ����!", Toast.LENGTH_LONG).show();
				}
			}
		};
	}

	public void onClickConnect(View view){
		pd.show();
		String urlStr=urlEditText.getText().toString();

		if(urlStr.length()<7 || urlStr.substring(0, 7).equals("http://")==false){

			urlStr="http://"+urlStr;
			urlEditText.setText(urlStr);
		}
		parseResult.RSSUrl=urlStr;
		nickEditText.setText("");
		receiveFeedWork rf=new receiveFeedWork(this, main);
		rf.start();
	}
	
	public void onClickOK(View view){
		parseResult.RSSUrl=urlEditText.getText().toString();
		parseResult.nick=nickEditText.getText().toString();
		if(main.isExistUrl(parseResult.RSSUrl)){
			Toast.makeText(this, "�̹� �����ϴ� �ǵ� URL �Դϴ�.", Toast.LENGTH_SHORT).show();
			return;
		}

		try{
			parseResult.pollingInterval=Integer.parseInt(PIEditText.getText().toString());
		}catch(Exception ex){
			parseResult.pollingInterval=20;
		}
		parseResult.fnewCount=parseResult.rssItems.size();
		parseResult.newCount=parseResult.fnewCount;
		parseResult.status=FeedItem.STATUS_OBSERVE;
		main.insertFeed(parseResult);
		Toast.makeText(this, "�ǵ� ����", Toast.LENGTH_SHORT).show();
		finish();
	}
	public void onClickCancel(View view){
		finish();
	}

	private static final String [][]sugestList={

		
		{"���۴���/�αⴺ��","http://news.google.co.kr/news?pz=1&ned=kr&hl=ko&topic=po&output=rss"}
		,{"�̵�����/TOP","http://www.daum.net/rss.xml"}
		,{"�Ķ�����","http://media.paran.com/rss/rss.kth?view=10"}
		,{"�Ѱܷʴ���/��ü","http://www.hani.co.kr/rss/"}
	
		,{"���ϰ���/������","http://news.mk.co.kr/rss/headline.xml"}
		,{"�̵��ϸ�/��ü","http://rss.edaily.co.kr/edaily_news.xml"}
		,{"��Ű����/��ü","http://www.kukinews.com/rss/kukiRssAll.xml"}
		,{"�Ӵ�������/�ֽ�","http://rss.mt.co.kr/mt_news.xml"}
		,{"ZDNet Korea/��ü","http://www.zdnet.co.kr/Include2/ZDNetKorea_News.xml"}
		,{"���̳��ȴ���/��ü","http://www.fnnews.com/rss/fn_realnews_all.xml"}
		,{"����Ź�/IT","http://www.khan.co.kr/rss/rssdata/itnews.xml"}
		,{"�����Ϻ�","http://www.ddanzi.com/rss/s/news"}
		,{"����������/��ü","http://www.sportsseoul.com/rss/rss.asp?cp_flag=1"}
		,{"�ٳ���/��ǻ��","http://bbs.danawa.com/RSS/rss2.0.php?nSiteC=1"}
		
		,{"KBS/�̱�ö�Ǳ¸���˽�","http://tune.kbs.co.kr/rss/1.xml"}
		,{"KBS/�����Ƕ���õ��","http://tune.kbs.co.kr/rss/7.xml"}
		,{"MBC/FM���ǵ��ü��ð��Դϴ�","http://minicast.imbc.com/PodCast/pod.aspx?code=1002600100000100000"}
		,{"MBC/�ռ����ǽü�����","http://minicast.imbc.com/PodCast/pod.aspx?code=1000674100000100000"}
		,{"SBS/�ν�Ż��������","http://wizard2.sbs.co.kr/w3/podcast/V0000328482.xml"}
		,{"��������/���²ļ���","http://old.ddanzi.com/appstream/ddradio.xml"}
		
		,{"�˻�/�˻ѰԽ���","http://www.ppomppu.co.kr/rss.php?id=ppomppu"}
		,{"�˻�/�޴����˻�","http://www.ppomppu.co.kr/rss.php?id=ppomppu2"}
		,{"�˻�/�޴�����ü","http://www.ppomppu.co.kr/rss.php?id=pmarket2"}
		,{"�˻�/�����Խ���","http://www.ppomppu.co.kr/rss.php?id=freeboard"}
		,{"�˻�/�޴�������","http://www.ppomppu.co.kr/rss.php?id=phone"}
		,{"������/�ֽű�","http://rss.parkoz.com/?id=home"}
		,{"������/�����Խ���2","http://rss.parkoz.com/?id=express_freeboard2"}
		
		,{"�縮��/��������","http://web2.ruliweb.daum.net/daum/rss.htm?bbs=2&id=519&bbsId=G003&itemGroupId=30&c1=6&c2=1"}
		,{"�縮��/XBOX ���Խ���","http://web2.ruliweb.daum.net/daum/rss.htm?bbs=2&id=145&bbsId=G005&itemId=46&c1=1&c2=2&menu=1"}
		,{"���������/�����Խ���","http://feeds.feedburner.com/TIG_freeboard"}
		,{"����/����Ʈ���꺣��Ʈ","http://feed43.com/5218652450154147.xml"}
		,{"�ȵ���̵���̵�/��ü","http://www.androidside.com/plugin/rss/rss.php"}
		
		,{"���̹�ī��/�߰���","http://cafe.rss.naver.com/joonggonara"}
		
		,{"���̹�/��","http://boom.rss.naver.com/boom.xml"}
		,{"�ȷ�/ASEC ���ȱǰ�","http://www.ahnlab.com/kr/site/rss/ahnlab_asecinfo.xml"}
		,{"�ȷ�/���ȴ���","http://www.ahnlab.com/kr/site/rss/ahnlab_securitynews.xml"}
		,{"Ʈ����/�ڿ���","http://twitter.com/statuses/user_timeline/wonsoonpark.rss"}
		,{"Ʈ����/����ȣPD","http://twitter.com/statuses/user_timeline/teoinmbc.rss"}
		,{"�̱۷罺/�α��","http://valley.egloos.com/rss/theme.php"}
		,{"�ú�α�/�α��","http://rss.allblog.net/TodayBestPosts.xml"}
		,{"Ƽ���丮/�α��","http://tistory.com/category/all/rss"}
		,{"�ͽ�/�ǽð�","http://mixsh.com/rss/category/all/all/all/popular"}
		,{"����View/����Ʈ","http://v.daum.net/best/rss"}
		
		
		,{"���κ�α�/����","http://thering.co.kr/rss"}
		
		,{"CBS/TOP","http://feeds.cbsnews.com/CBSNewsMain?format=xml"}
		,{"CNN/TOP","http://rss.cnn.com/rss/edition.rss"}
		,{"USA TODAY/HOME","http://rssfeeds.usatoday.com/usatoday-NewsTopStories"}
	//	,{"New Yourk Times/Home","http://feeds.nytimes.com/nyt/rss/HomePage"}
		,{"�߱�����/�α�","http://news.google.com.hk/news?ned=cn&hl=zh-CN&topic=po&output=rss&r=cn&vanilla=0"}
		,{"�߱�People/����","http://www.people.com.cn/rss/politics.xml"}
		,{"Yahoo JP/����","http://headlines.yahoo.co.jp/rss/rps_dom.xml"}
		,{"NHK�¶���/�ֿ�","http://www3.nhk.or.jp/rss/news/cat0.xml"}
	};

}

class receiveFeedWork extends Thread{
	AddFeedActivity mAfActivity;
	RSSParser parser;
	RSSSniperMain main;
	public receiveFeedWork(AddFeedActivity afActivity, RSSSniperMain amain){
		mAfActivity=afActivity;
		parser=new RSSParser();
		main=amain;
	}
	@Override
	public void run(){
		try {
			parser.parse(mAfActivity.parseResult, main.maxParseCount);
			MyLog.i("tag","update()+"+mAfActivity.parseResult.rssItems.size());
			mAfActivity.handler.sendEmptyMessage(1);
		} catch (Exception e) {
			mAfActivity.handler.sendEmptyMessage(2);
			MyLog.e("tag","update()+"+mAfActivity.parseResult.rssItems.size());
			e.printStackTrace();
		}
	}
}