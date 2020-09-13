package com.atcle.rsssniper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class RSSItemDbHandler {
	public static final String TABLE_NAME=RSSItemDbHelper.TABLE_NAME;
	static private RSSItemDbHelper helper=null;
	static private SQLiteDatabase db=null;
	public int fIdx;
	//private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void create(Context ctx){
		if(helper==null){
			helper=new RSSItemDbHelper(ctx);
			db=helper.getWritableDatabase();
		}
	}
	public static void close(){
		db.close();
	}
	public static void open(){
		db=helper.getWritableDatabase();
	}
	
	public RSSItemDbHandler(int afIdx){
		fIdx=afIdx;
	}
	
	
}
