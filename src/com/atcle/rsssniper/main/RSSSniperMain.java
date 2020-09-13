package com.atcle.rsssniper.main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.RSSSniperActivity;
import com.atcle.rsssniper.db.RSSListDbHandler;
import com.atcle.rsssniper.parser.RSSParser;
import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.rss.FilterResult;

public class RSSSniperMain{
	//singleton class
	public static boolean bOnCreateFirst=false;
	private SharedPreferences prefs;
	private static RSSSniperMain instance;
	public static RSSSniperActivity mainActivity;
	public RSSSniperMain(Application app){
		prefs=PreferenceManager.getDefaultSharedPreferences(app);	
		setFirstStart();

		feedEvents=new LinkedList<FilterResult>();
		feeds=new ArrayList<FeedItem>();

		dbh=new RSSListDbHandler(app);

		feeds=dbh.getList();	//로드

		observeClient=new ObserveClient(app);	//서비스
		noticeUtil=new NoticeUtil(app);

		loadPrefSettings();
	}
	public void setMainActivity(RSSSniperActivity amainActivity){
		mainActivity=amainActivity;
	}
	//	public static RSSSniperMain getInstance(RSSSniperActivity amainActivity){
	//		mainActivity=amainActivity;
	//		return getInstance();
	//	}
	//	public static RSSSniperMain getInstance(){
	//		if(instance==null && mainActivity!=null)
	//			instance=new RSSSniperMain();
	//		return instance;
	//	}
	public void setFirstStart(){
		int v=prefs.getInt("codeVersion", -1);
		if(v==-1){
			Editor edit=prefs.edit();
			edit.putString("strMaxParseCount", "30");
			edit.putBoolean("bObserveNotification", true);
			edit.putBoolean("bNewFeedNotification", true);
			edit.putBoolean("bAlarmBeep", true);
			edit.putBoolean("bVibrate", true);
			
			edit.putBoolean("bObserveOnlyWifi", false);
			v=1;
			edit.putInt("codeVersion", v);
			edit.commit();
		}
		if(v==1){
			Editor edit=prefs.edit();
			edit.putString("rt_text_size", "15");
			edit.putString("rt_text_space", "1.5f");
			
			edit.putBoolean("rt_read_one", true);
			edit.putBoolean("rt_auto_close", true);
			edit.putInt("codeVersion", 2);
			edit.commit();
		}
	}
	public void openDb(){
		dbh.open();
	}
	public void closeDb(){
		dbh.close();
	}
	public void reloadDb(){
		feeds=dbh.getList();
		feedEvents.clear();
		mainActivity.initAllTab();
	}
	public void init(){
		feedEvents=new LinkedList<FilterResult>();
		feeds=dbh.getList();
	}
	// 이 위로 볼필요 없음
	// singleton constructor end

	private RSSListDbHandler dbh;	//DB
	private NoticeUtil noticeUtil;	//notice

	//rss feeds data
	public LinkedList<FilterResult> feedEvents;
	private ArrayList<FeedItem> feeds;

	public boolean insertFeed(FeedItem feed){
		if(isExistUrl(feed.RSSUrl)==false){
			long aidx=dbh.insert(feed);
			feed.idx=(int)aidx;
			feeds.add(feed);
			return true;
		}
		return false;
	}
	public FeedItem getFeedByPos(int pos){
		return feeds.get(pos);
	}
	public void dbModifyFeed(FeedItem item){
		dbh.modify(item);
	}
	public void setFeed(int idx, FeedItem item){
		feeds.set(idx, item);
	}
	public void dbDeleteFeedByIdx(int idx){
		dbh.delete(idx);
		int pos=-1;
		for(int i=0; i<feeds.size(); i++){
			if(feeds.get(i).idx==idx){
				pos=i;
				break;
			}
		}
		feeds.remove(pos);
	}
	public int getFeedCount(){
		return feeds.size();
	}
	public FeedItem getFeedByIdx(int idx){
		for(int i=0; i<feeds.size(); i++){
			if(feeds.get(i).idx==idx){
				return feeds.get(i);
			}
		}
		return null;
	}
	public int getPosByIdx(int idx){
		for(int i=0; i<feeds.size(); i++){
			if(feeds.get(i).idx==idx){
				return i;
			}
		}
		return -1;
	}
	/** add feed acctivity에서 사용	 */
	public boolean isExistUrl(String url){
		for(int i=0; i<feeds.size(); i++){
			if(feeds.get(i).RSSUrl!=null && feeds.get(i).RSSUrl.equals(url)){
				return true;
			}
		}
		return false;
	}


	public void setMainReadTabByPos(int pos){
		mainActivity.showReadTab(feeds.get(pos));
	}
	public void setMainReadTabByIdx(int idx){
		FeedItem feed=getFeedByIdx(idx);
		if(feed!=null){
			mainActivity.showReadTab(feed);
		}else{
			MyLog.e("tag", "feed=null");
		}
	}
	//
	public int maxParseCount=0;
	public boolean bObserveOnlyWifi=false;
	public void loadPrefSettings(){
		maxParseCount=Integer.parseInt(prefs.getString("strMaxParseCount", "30"));
		bObserveOnlyWifi=prefs.getBoolean("bObserveOnlyWifi", true);
		
	}
	/** 엑티비티에서 호출
	 */
	public void observeReschedule(){
		observeClient.reschecule();
	}
	public void onFeedListViewCheckBoxChanged(int pos,boolean isChecked){
		if(isChecked){
			feeds.get(pos).status=FeedItem.STATUS_OBSERVE;
		}else{
			feeds.get(pos).status=FeedItem.STATUS_NOT_OBSERVE;
		}
		dbModifyFeed(feeds.get(pos));
		if(bObserve){
			observeClient.startObserve();
		}
		MyLog.i("tag","feed on CheckboxChanged"+pos+"/"+isChecked);
	}
	//
	// Update mothods
	//
	public void updateFeedAll(){
		for(int i=0; i<feeds.size(); i++){
			if(feeds.get(i).status==FeedItem.STATUS_OBSERVE){
				updateFeedByIdx(feeds.get(i).idx);
			}
		}
	}
	public void updateFeed(FeedItem feed){
		feed.bUpdateReserved=true;
		updateFeedByIdx(feed.idx);
	}
	public void updateFeedByPos(int pos){
		FeedItem feed=getFeedByIdx(pos);
		if(feed!=null){
			updateFeedByIdx(feed.idx);
		}
	}
	private void sendActivityMessage(Message msg){
		if(mainActivity!=null){
			mainActivity.activityHandler.sendMessage(msg);
		}
	}
	private void sendActivityMessage(int what){
		if(mainActivity!=null){
			mainActivity.activityHandler.sendEmptyMessage(what);
		}
	}
	public AtomicInteger runThreadCount=new AtomicInteger(0);
	/** 실제 업데이트 스레드 시작 */
	public void updateFeedByIdx(int idx){
		Message msg=Message.obtain();
		if(runThreadCount.get()>=3){
			//			MyLog.i("tag", "update delayed "+runThreadCount.get() +" "+idx);
			msg.what=MSG_DELAY_UPDATE;
			msg.arg1=idx;
			handler.sendMessageDelayed(msg, 2000);
			return;
		}
		runThreadCount.incrementAndGet();	//쓰레드카운트증가
		msg.what=RSSSniperActivity.MSG_FEED_UPDATE_START;
		msg.arg1=idx;
		sendActivityMessage(msg);

		updateFeed workThread=new updateFeed(this, idx);
		workThread.start();
	}


	//
	// Service Methods
	//
	public boolean bObserve;
	ObserveClient observeClient;

	/** feed update reserved set false	 */
	/**
	 * 
	 */
	public void resetObserveReserved(){
		for(int i=0; i<feeds.size(); i++){
			feeds.get(i).bUpdateReserved=false;
		}
	}
	//옵저버 시작
	public void bObserveSet(boolean abObserve){
		bObserve=abObserve;
		if(abObserve){
			observeClient.startObserve();
		}else{
			observeClient.stopObserve();
			handler.removeMessages(MSG_DELAY_UPDATE);
		}
		observeClient.say();
	}

	public void doBindService(){
		observeClient.doBindService();
	}
	public void onDestroy(){
		observeClient.doUnbindService();
		if(bObserve==false){
			observeClient.doStopService();
		}
	}

	//events tab 메뉴에서 이벤트 지울떄 호출
	public void clearEvents(){
		feedEvents.clear();
	}
	/** 필터 체크	 */
	public void filterCheck(int idx){
		//첫 업데이트시 알람울리지 않음
		FeedItem feed=getFeedByIdx(idx);
		if(feed==null) return;
		
		//닉 미설정 경우 title로 저장함
		if(feed.nick==null || feed.nick.length()==0){
			feed.nick=feed.title;
			dbModifyFeed(feed);
		}
		
		if(feed.status!=FeedItem.STATUS_OBSERVE){
			return;
		}

		FilterResult fr=feed.doFilter();
		if(fr!=null){
			if(feedEvents.size()>=50){
				feedEvents.poll();
			}
			feedEvents.add(fr);

			if(bObserve){	//감시중일때만 노티스알림
				if(feed.fnewCount!=-1){//첫업데이트가 아닌경우만
					noticeUtil.showNoticeIfSetNoticeable();	//notice
				}else{
					//feed.fnewCount
				}
			}
		}
		sendActivityMessage(RSSSniperActivity.MSG_FEED_EVENT);
	}


	public Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg){
			MyLog.v("tag", "main receive "+msg.what+" arg1 : "+msg.arg1);
			switch(msg.what){
			case MSG_PARSE_COMPLETE:
				RSSSniperMain.this.filterCheck(msg.arg1);//필터체크
				Message msg2=Message.obtain();
				msg2.what=RSSSniperActivity.MSG_FEED_UPDATE_COMPLETE;
				msg2.arg1=msg.arg1;
				sendActivityMessage(msg2);
				//화면 갱신을 위해 메시지 보냄
				break;

			case MSG_PARSE_ERROR:
				Message msg3=Message.obtain();
				msg3.what=RSSSniperActivity.MSG_FEED_UPDATE_FAIL;
				msg3.arg1=msg.arg1;
				sendActivityMessage(msg3);
				break;
			case MSG_DELAY_UPDATE:
				RSSSniperMain.this.updateFeedByIdx(msg.arg1);
				break;
			}
		}
	};
	public final static int MSG_DELAY_UPDATE=3;
	public final static int MSG_PARSE_COMPLETE=0;
	public final static int MSG_PARSE_ERROR=1;


	class updateFeed extends Thread{
		RSSSniperMain main;
		FeedItem oldFeed;
		FeedItem newFeed;
		int idx;
		public updateFeed(RSSSniperMain amain, int aidx){
			main=amain;
			idx=aidx;
			oldFeed=main.getFeedByIdx(idx);
			if(oldFeed!=null)
				newFeed=new FeedItem(oldFeed);
		}
		@Override
		public void run(){
			oldFeed.bUpdateReserved=false;
			MyLog.i("tag","thread run "+main.runThreadCount.get()+" / "+oldFeed.idx);
			RSSParser parser=new RSSParser();
			Message msg=Message.obtain(main.handler);
			try {
				parser.parse(newFeed,main.maxParseCount);

				countNewFeeds(oldFeed, newFeed);
				oldFeed.copyFromParseElement(newFeed);

				msg.what=RSSSniperMain.MSG_PARSE_COMPLETE;
				msg.arg1=idx;
				main.handler.sendMessage(msg);
			} catch (Exception e) {
				oldFeed.setLastUpdateNow();

				msg.what=RSSSniperMain.MSG_PARSE_ERROR;
				msg.arg1=idx;
				main.handler.sendMessage(msg);
				e.printStackTrace();
			}

			main.runThreadCount.decrementAndGet();	//쓰레드카운트감소
		}

		public void countNewFeeds(FeedItem old, FeedItem nw){
			if(old.rssItems.size()==0){
				old.newCount=nw.rssItems.size();
				old.fnewCount=nw.rssItems.size();
				if(old.lastUpdateDate==null)
					old.fnewCount=-1;	//첫업데이트시 fnew카운트 -1해줌
				return;
			}

			String topLink=old.rssItems.get(0).link;
			int nwSize=nw.rssItems.size();
			int c=0;
			for(int i=0; i<nwSize; i++){
				if(topLink.equals(nw.rssItems.get(i).link)){
					c=i;
					break;
				}
			}
			old.fnewCount=c;
			old.newCount=c+old.newCount;
			if(old.newCount>old.rssItems.size()){
				old.newCount=old.rssItems.size();
			}
			MyLog.e("tag", old.idx+" : new"+old.newCount);
		}
	}
}


