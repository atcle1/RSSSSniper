package com.atcle.rsssniper.main;

import java.util.ArrayList;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperActivity;
import com.atcle.rsssniper.RSSSniperApplication;
import com.atcle.rsssniper.rss.FeedItem;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ObserveService extends Service {
	private SharedPreferences prefs;
	private NotificationManager mNM;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = 2323;

	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder {
		ObserveService getService() {
			return ObserveService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		MyLog.w("tag", "onCreate service");
		// Display a notification about us starting.  We put an icon in the status bar.
		//showNotification();

		main=((RSSSniperApplication)getApplication()).getMain();
		scheduler=new UpdateScheduler(main);
		prefs=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		connMgr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		pm = (PowerManager) getSystemService(this.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "lock");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MyLog.w("tag", "onStartCommand Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		if(intent==null){
			loadState();
			if(bObserve){
				startObserve();
				main.doBindService();
				main.bObserve=this.bObserve;
				//				지금 바인드 안하면 액티비티 onCreate에서 하게되고
				//				그럴경우 bind가 blocking 함수가 아니기때문에
				//				bind되기전 서비스접근시 NullPointerException이 날수 있음
			}
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);

		// Tell the user we stopped.
		MyLog.w("tag","destroy service");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients.  See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	private void startForegroundM(){
		Intent intent=new Intent(this, RSSSniperActivity.class);
		Notification notification = new Notification(R.drawable.noti, "RSS 감시를 시작합니다...",
				System.currentTimeMillis());

		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);

		notification.setLatestEventInfo(this, "RSS 잠복중...",
				"RSS Sniper 서비스가 실행 중 입니다.", pendingIntent);
		//NotificationManager notiMgr=(NotificationManager)this.getSystemService(Activity.NOTIFICATION_SERVICE);
		startForeground(3337, notification);
	}
	// 이 위로 볼필요 없음
	private boolean bObserve;
	public long updateLeftMil;
	public boolean isObserve(){
		return bObserve;
	}

	ConnectivityManager connMgr;
	PowerManager.WakeLock wl;
	PowerManager pm;
	public void startObserve(){
		bObserve=true;

		//		handler.removeMessages(MSG_UPDATE_FEED);
		//		handler.removeMessages(MSG_UPDATE_WAIT);
		handler.removeMessages(MSG_UPDATE);

		main.resetObserveReserved();	//updateresered 체크한것을 초기화함
		update();
		//setNextUpdate();
		//updatereserved 초기화할것
		//showNotification();
//		pm = (PowerManager) getSystemService(this.POWER_SERVICE);
//		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "lock");
		wl.acquire();

		if(prefs.getBoolean("bObserveNotification", false)){
			startForegroundM();
		}
		saveState();
	}
	public void stopObserve(){
		bObserve=false;
		handler.removeMessages(MSG_UPDATE);
		stopForeground(true);
		if(wl!=null)
			wl.release();
		saveState();
	}
	public void saveState(){
		Editor edit=prefs.edit();
		edit.putBoolean("bServiceStart", bObserve);
		edit.commit();
	}
	public boolean loadState(){
		bObserve=prefs.getBoolean("bServiceStart", false);
		return bObserve;
	}

	/** 감시할 피드 추가등	 */
	public void reschedule(){
		if(bObserve==false) return;
		handler.removeMessages(MSG_UPDATE);
		update();
	}

	public void update(){
		Message msg=Message.obtain();

		NetworkInfo networkInfo=connMgr.getActiveNetworkInfo();
		//null return 할수도 있음
		if(networkInfo!=null){
			MyLog.i("tag","network state "+networkInfo.getType());
		}else{
			MyLog.i("tag","network state return null");
		}
		if(networkInfo==null || main.bObserveOnlyWifi &&  ConnectivityManager.TYPE_WIFI!=networkInfo.getType()){
			//null이거나, 와이파이온리인데 와이파이모드가 아닌경우
			//wifi only update mode
			//아무것도 안함
			MyLog.i("tag","NOT WIFI UPDATE DELEAYED");
		}else{
			ArrayList<Integer> updateList=scheduler.getUpdateList();

			if(MyLog.bI){
				String updateIdxStr="";
				for(int i=0; i<updateList.size(); i++){
					updateIdxStr+=updateList.get(i)+" ";
				}
				MyLog.i("tag","update order : "+updateIdxStr);
			}
			
			for(int i=0; i<updateList.size(); i++){
				main.updateFeedByIdx(updateList.get(i));
			}

		}

		msg.what=MSG_UPDATE;
		handler.sendMessageDelayed(msg, 60*1000);
	}


	//	public void setNextUpdate(){
	//		Message msg=Message.obtain();
	//
	//		FeedItem feed=scheduler.nextUpdateItem();
	//		if(bObserve==false){
	//			MyLog.e("tag", "bObserve==false stop update");
	//			return;
	//		}
	//		if(feed==null){
	//			MyLog.e("tag", "nextUpdate null wait...");
	//			msg.what=MSG_UPDATE_WAIT;
	//			handler.sendMessageDelayed(msg, 60000);
	//			//지금 모두 다 업데이트중일때 1분뒤에 다시 체크함
	//			return;
	//		}
	//		updateLeftMil=feed.getUpdateLeftMil();
	//		feed.bUpdateReserved=true;	//업데이트 예약 표시
	//
	//		if(updateLeftMil<=0){
	//			updateLeftMil=0;
	//		}
	//		msg.what=MSG_UPDATE_FEED;
	//		msg.arg1=feed.idx;
	//		handler.removeMessages(MSG_UPDATE_FEED);
	//		handler.sendMessageDelayed(msg, updateLeftMil);
	//		MyLog.e("tag","setNextUpdate "+feed.nick+"/"+updateLeftMil);
	//	}


	public void say(){
		MyLog.e("tag","say()");
	}

	RSSSniperMain main;
	UpdateScheduler scheduler;

	//	private static final int MSG_UPDATE_FEED=1;
	//	private static final int MSG_UPDATE_WAIT=2;
	//	public static final int MSG_UPDATE_END=3;

	public static final int MSG_UPDATE=5;
	public Handler handler=new Handler(){
		//		FeedItem feed;
		@Override
		public void handleMessage(Message msg){
			MyLog.w("tag", "service handler receive "+msg.what+" arg1 : "+msg.arg1);
			switch(msg.what){
			case MSG_UPDATE:
				update();
				break;

				//			case MSG_UPDATE_FEED:
				//				feed=main.getFeedByIdx(msg.arg1);
				//				if(feed==null){	//피드가 삭제된 경우
				//					setNextUpdate();
				//					return;
				//				}
				//
				//				NetworkInfo networkInfo=connMgr.getActiveNetworkInfo();
				//				//null return 할수도 있음
				//				if(networkInfo!=null){
				//					MyLog.i("tag","network state "+networkInfo.getType());
				//				}else{
				//					MyLog.i("tag","network state return null");
				//				}
				//				if(networkInfo==null || main.bObserveOnlyWifi &&  ConnectivityManager.TYPE_WIFI!=networkInfo.getType()){
				//					//null이거나, 와이파이온리인데 와이파이모드가 아닌경우
				//					//wifi only update mode
				//					Message smsg=Message.obtain();
				//					smsg.what=MSG_UPDATE_WAIT;
				//					handler.sendMessageDelayed(smsg, 120000);
				//					MyLog.i("tag","NOT WIFI UPDATE DELEAYED");
				//					return;
				//				}else{
				//					main.updateFeed(feed);
				//					ObserveService.this.setNextUpdate();
				//				}
				//				break;
				//			case MSG_UPDATE_WAIT:
				//				ObserveService.this.setNextUpdate();
				//				break;
				//			case MSG_UPDATE_END:
				////				main.updateFeed(feed);
				//				break;
			}
		}
	};
}
