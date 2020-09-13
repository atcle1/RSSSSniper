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
					Toast.makeText(AddFeedActivity.this, "업데이트 실패!", Toast.LENGTH_LONG).show();
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
			Toast.makeText(this, "이미 존재하는 피드 URL 입니다.", Toast.LENGTH_SHORT).show();
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
		Toast.makeText(this, "피드 삽입", Toast.LENGTH_SHORT).show();
		finish();
	}
	public void onClickCancel(View view){
		finish();
	}

	private static final String [][]sugestList={

		
		{"구글뉴스/인기뉴스","http://news.google.co.kr/news?pz=1&ned=kr&hl=ko&topic=po&output=rss"}
		,{"미디어다음/TOP","http://www.daum.net/rss.xml"}
		,{"파란뉴스","http://media.paran.com/rss/rss.kth?view=10"}
		,{"한겨례뉴스/전체","http://www.hani.co.kr/rss/"}
	
		,{"매일경제/헤드라인","http://news.mk.co.kr/rss/headline.xml"}
		,{"이데일리/전체","http://rss.edaily.co.kr/edaily_news.xml"}
		,{"쿠키뉴스/전체","http://www.kukinews.com/rss/kukiRssAll.xml"}
		,{"머니투데이/최신","http://rss.mt.co.kr/mt_news.xml"}
		,{"ZDNet Korea/전체","http://www.zdnet.co.kr/Include2/ZDNetKorea_News.xml"}
		,{"파이낸셜뉴스/전체","http://www.fnnews.com/rss/fn_realnews_all.xml"}
		,{"경향신문/IT","http://www.khan.co.kr/rss/rssdata/itnews.xml"}
		,{"딴지일보","http://www.ddanzi.com/rss/s/news"}
		,{"스포츠서울/전체","http://www.sportsseoul.com/rss/rss.asp?cp_flag=1"}
		,{"다나와/컴퓨터","http://bbs.danawa.com/RSS/rss2.0.php?nSiteC=1"}
		
		,{"KBS/이근철의굿모닝팝스","http://tune.kbs.co.kr/rss/1.xml"}
		,{"KBS/유희열의라디오천국","http://tune.kbs.co.kr/rss/7.xml"}
		,{"MBC/FM음악도시성시경입니다","http://minicast.imbc.com/PodCast/pod.aspx?code=1002600100000100000"}
		,{"MBC/손석희의시선집중","http://minicast.imbc.com/PodCast/pod.aspx?code=1000674100000100000"}
		,{"SBS/두시탈출컬투쇼","http://wizard2.sbs.co.kr/w3/podcast/V0000328482.xml"}
		,{"딴지라디오/나는꼼수다","http://old.ddanzi.com/appstream/ddradio.xml"}
		
		,{"뽐뿌/뽐뿌게시판","http://www.ppomppu.co.kr/rss.php?id=ppomppu"}
		,{"뽐뿌/휴대폰뽐뿌","http://www.ppomppu.co.kr/rss.php?id=ppomppu2"}
		,{"뽐뿌/휴대폰업체","http://www.ppomppu.co.kr/rss.php?id=pmarket2"}
		,{"뽐뿌/자유게시판","http://www.ppomppu.co.kr/rss.php?id=freeboard"}
		,{"뽐뿌/휴대폰포럼","http://www.ppomppu.co.kr/rss.php?id=phone"}
		,{"파코즈/최신글","http://rss.parkoz.com/?id=home"}
		,{"파코즈/자유게시판2","http://rss.parkoz.com/?id=express_freeboard2"}
		
		,{"루리웹/유저뉴스","http://web2.ruliweb.daum.net/daum/rss.htm?bbs=2&id=519&bbsId=G003&itemGroupId=30&c1=6&c2=1"}
		,{"루리웹/XBOX 잡담게시판","http://web2.ruliweb.daum.net/daum/rss.htm?bbs=2&id=145&bbsId=G005&itemId=46&c1=1&c2=2&menu=1"}
		,{"디스이즈게임/자유게시판","http://feeds.feedburner.com/TIG_freeboard"}
		,{"오유/베스트오브베스트","http://feed43.com/5218652450154147.xml"}
		,{"안드로이드사이드/전체","http://www.androidside.com/plugin/rss/rss.php"}
		
		,{"네이버카페/중고나라","http://cafe.rss.naver.com/joonggonara"}
		
		,{"네이버/붐","http://boom.rss.naver.com/boom.xml"}
		,{"안랩/ASEC 보안권고문","http://www.ahnlab.com/kr/site/rss/ahnlab_asecinfo.xml"}
		,{"안랩/보안뉴스","http://www.ahnlab.com/kr/site/rss/ahnlab_securitynews.xml"}
		,{"트위터/박원순","http://twitter.com/statuses/user_timeline/wonsoonpark.rss"}
		,{"트위터/김태호PD","http://twitter.com/statuses/user_timeline/teoinmbc.rss"}
		,{"이글루스/인기글","http://valley.egloos.com/rss/theme.php"}
		,{"올블로그/인기글","http://rss.allblog.net/TodayBestPosts.xml"}
		,{"티스토리/인기글","http://tistory.com/category/all/rss"}
		,{"믹시/실시간","http://mixsh.com/rss/category/all/all/all/popular"}
		,{"다음View/베스트","http://v.daum.net/best/rss"}
		
		
		,{"개인블로그/잠밤기","http://thering.co.kr/rss"}
		
		,{"CBS/TOP","http://feeds.cbsnews.com/CBSNewsMain?format=xml"}
		,{"CNN/TOP","http://rss.cnn.com/rss/edition.rss"}
		,{"USA TODAY/HOME","http://rssfeeds.usatoday.com/usatoday-NewsTopStories"}
	//	,{"New Yourk Times/Home","http://feeds.nytimes.com/nyt/rss/HomePage"}
		,{"중국구글/인기","http://news.google.com.hk/news?ned=cn&hl=zh-CN&topic=po&output=rss&r=cn&vanilla=0"}
		,{"중국People/국내","http://www.people.com.cn/rss/politics.xml"}
		,{"Yahoo JP/국내","http://headlines.yahoo.co.jp/rss/rps_dom.xml"}
		,{"NHK온라인/주요","http://www3.nhk.or.jp/rss/news/cat0.xml"}
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