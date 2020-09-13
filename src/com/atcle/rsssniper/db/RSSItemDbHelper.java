package com.atcle.rsssniper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RSSItemDbHelper extends SQLiteOpenHelper {
	public static final String TABLE_NAME="RSSItem";
	
	public RSSItemDbHelper(Context context) {
		super(context, "Item.db", null, 1);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String table=
				"CREATE TABLE "+TABLE_NAME+" ("
						+"idx INTEGER PRIMARY KEY AUTOINCREMENT, "+
						"fidx INTEGER, "+
						"title TEXT, "+
						"link TEXT, "+
						"desc TEXT, "+
						"enclosure TEXT, "+
						"pubdate TEXT) ";
		db.execSQL(table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);
	}

}
