package com.atcle.rsssniper.main;

import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperActivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NoticeUtil {
	NotificationManager notiMgr;
	Context context;
	SharedPreferences prefs;
	
	public NoticeUtil(Context acontext){
		context=acontext;
		notiMgr=(NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
		prefs=PreferenceManager.getDefaultSharedPreferences(context);
	}
	public void showNoticeIfSetNoticeable(){
		if(prefs.getBoolean("bNewFeedNotification", false)==false){
			return;
		}
		
		Intent intent=new Intent(context,RSSSniperActivity.class);
		
		intent.putExtra("notificationIntent", true);
		
		
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
		
		long when=System.currentTimeMillis();
		
				
		Notification notification=new Notification(R.drawable.newfeed, "새로운 피드", when);
		notification.setLatestEventInfo(context, "새로운 피드","새로운 피드가 있습니다.",pendingIntent);

		
		
		if(prefs.getBoolean("bAlarmBeep",false)){
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if(prefs.getBoolean("bVibrate",false)){
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}		
		
		notification.flags=Notification.FLAG_AUTO_CANCEL;
		
		notiMgr.cancel(12121);
		notiMgr.notify(12121, notification);
	}
}
