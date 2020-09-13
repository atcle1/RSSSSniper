package com.atcle.rsssniper;

import com.atcle.rsssniper.main.RSSSniperMain;

import android.app.Application;

public class RSSSniperApplication extends Application {
	@Override
	public void onCreate(){
		rsssniperMain=new RSSSniperMain(this);
		super.onCreate();
	}
	@Override
	public void onTerminate(){
		super.onTerminate();
	}
	
	public void setMainActivity(RSSSniperActivity activity){
		rsssniperMain.setMainActivity(activity);
	}
	public RSSSniperMain getMain(){
		return rsssniperMain;
	}
	RSSSniperMain rsssniperMain;
}
