package com.atcle.rsssniper.recommend;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	public static final String dbFileName="recf.db";
	public static final String tableName="recfTable";
	private Context mContext;
	
	public DBHelper(Context context) {
		super(context, dbFileName, null, 1);
		mContext=context;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		createDb(arg0);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		arg0.execSQL("DROP TABLE IF EXISTS myTimetable");
		createDb(arg0);
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(mContext);
		Editor edit=prefs.edit();
		edit.putString(RecommendFeedMgr.LAST_UPDATE_KEY, "");
		edit.commit();

	}
	private void createDb(SQLiteDatabase arg0){
		String table=
				"CREATE TABLE "+tableName+"("+
						"IDX INTEGER PRIMARY KEY, "+
						"PIDX INTEGER, "+
						"TYPES INTEGER, "+
						"ORDERS INTEGER, "+
						"TITLE TEXT, "+
						"DESCS TEXT, "+
						"URL TEXT)";
		arg0.execSQL(table);
		Log.i("tag","create db");
	}
}
