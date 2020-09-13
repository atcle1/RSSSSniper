package com.atcle.rsssniper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RSSListDbHelper extends SQLiteOpenHelper{
	public static final String TABLE_NAME="RSSList";

	public RSSListDbHelper(Context context) {
		super(context, "RSS.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String table=
				"CREATE TABLE "+TABLE_NAME+" ("
						+"idx INTEGER PRIMARY KEY AUTOINCREMENT, "+
						"nick TEXT, "+
						"url TEXT, "+
						"interval INTEGER, "+
						"filter TEXT, "+
						"status INTEGER) ";	//0:감시중지 1:감시
		db.execSQL(table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);
	}

}
