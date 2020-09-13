package com.atcle.rsssniper.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.atcle.rsssniper.rss.FeedItem;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RSSListDbHandler {
	public static final String TABLE_NAME=RSSListDbHelper.TABLE_NAME;
	private RSSListDbHelper helper;
	private SQLiteDatabase db;
	//private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public RSSListDbHandler(Context ctx){
		helper=new RSSListDbHelper(ctx);
		db=helper.getWritableDatabase();
	}
	@Override
	public void finalize(){
		db.close();
	}
	public void close(){
		db.close();
	}
	public void open(){
		db=helper.getWritableDatabase();
	}
	public long insert(FeedItem feed){
		ContentValues values=new ContentValues();
		values.put("nick", feed.nick);
		values.put("url", feed.RSSUrl);
		values.put("interval", feed.pollingInterval);
		values.put("filter", feed.filter.toString());
		values.put("status", feed.status);
		return db.insert("RSSList", null, values);
	}

	public int modify(FeedItem feed){
		ContentValues values=new ContentValues();
		values.put("nick", feed.nick);
		values.put("url", feed.RSSUrl);
		values.put("interval", feed.pollingInterval);
		values.put("filter", feed.filter.toString());
		values.put("status", feed.status);
		return db.update(TABLE_NAME, values, "idx=?", new String[]{Integer.toString(feed.idx)});
	}
	public int delete(int idx){
		return db.delete(TABLE_NAME, "idx=?", new String[]{Integer.toString(idx)});
	}
	public ArrayList<FeedItem> getList(){
		ArrayList<FeedItem> result=new ArrayList<FeedItem>();
		Cursor cursor;
		cursor=db.rawQuery("select idx, nick, url, interval, filter, status from "+TABLE_NAME, null);
		if(cursor!=null){
			while(cursor.moveToNext()){
				result.add(new FeedItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getInt(5)));
			}
			cursor.close();
		}
		return result;
	}
}
