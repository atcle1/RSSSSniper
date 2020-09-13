package com.atcle.rsssniper.recommend;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Util {
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static Date getDate(SharedPreferences prefs, String key){
		String dateStr=prefs.getString(key, "");
		Date dt=null;
		try{
			dt=df.parse(dateStr);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dt;
	}
	public static void putDate(SharedPreferences prefs, String key, Date dt){
		String dateStr=df.format(dt);
		Editor et=prefs.edit();
		et.putString(key, dateStr);
		et.commit();
	}
}
