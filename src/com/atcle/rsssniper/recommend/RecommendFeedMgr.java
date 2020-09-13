package com.atcle.rsssniper.recommend;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.parser.RecfParser;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class RecommendFeedMgr {
	public static final String LAST_UPDATE_KEY="RecfLastUpdate";
	private SharedPreferences prefs;
	private RecfParser mp;
	private DBHandler db;
	private Context mContext;

	/** db열기 전에 정의해야함 */
	public RecommendFeedMgr(Context aContext) {
		mp = new RecfParser();
		mContext = aContext;
		prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
		db = new DBHandler(aContext);

//				debug_initDb();
		setFirstRun();
				
//				Sync();
//				dbCopyToExt();	//db복사
	}

	public void deleteDB(){
		db.deleteDB();
	}
	public void closeDb(){
		db.close();
	}
	public ArrayList<DBItem> getChildItems(int pidx){
		return db.getItemsByPidx(pidx);
	}

	public DBItem getItem(int idx){
		return db.getItem(idx);
	}

	public int Sync() {
		ArrayList<DBItem> result = null;
		int i = 0, u = 0, r = 0;
		try {
			Date dt = Util.getDate(prefs, LAST_UPDATE_KEY);

			String lastUpdateStr = (String) android.text.format.DateFormat.format(
					"yyyy-MM-dd%20kk:mm:ss", dt);
			result = mp.parse(lastUpdateStr);
			for (DBItem item : result) {
				if(item.TYPES==0){
					db.deleteItem(item.IDX);
					r=0;
				}else{
					r = db.sync(item);
				}
				
				if(r==0){
					MyLog.i("tag","delete "+item.TITLE);
				}else if (r == 1) {
					i++;
					MyLog.i("tag","insert "+item.TITLE);
				} else if (r == 2) {
					u++;
					MyLog.i("tag","update "+item.TITLE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		Date dt = new Date();
		Util.putDate(prefs, LAST_UPDATE_KEY, dt);
		return i+u;
	}
	private void debug_initDb(){
		db.deleteDB();
		Date t=new Date(1);
		Util.putDate(prefs, LAST_UPDATE_KEY, t);
	}

	/** pref, db연후 할것	, 에셋날짜와 db날짜 비교해서 에셋이 최신일경우 복구 */
	public void setFirstRun() {
		MyLog.i("tag","setFirstRun()");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date assetDBDate=new Date();
		Date currentDBDate=new Date();
		//에셋db 날짜 구함
		String assetDBDateStr=mContext.getString(com.atcle.rsssniper.R.string.recf_asset_date);
		try {
			assetDBDate=sdf.parse(assetDBDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//db날짜 구함
		currentDBDate=Util.getDate(prefs, LAST_UPDATE_KEY);

		//에셋db보다 오래된 경우 에셋으로 복구
		if (currentDBDate==null || assetDBDate.after(currentDBDate)) {
			db.restoreDBFromAsset();

			//스트링에 있는 업데이트날짜 기록
			Util.putDate(prefs, LAST_UPDATE_KEY, assetDBDate);
		}
	}

	/** mydb파일을 외장메모리로 복사	 */
	public void dbCopyToExt() {
		try {
			DBHelper mydb = new DBHelper(mContext);
			String pathStr = mydb.getWritableDatabase().getPath();
			FileInputStream fis = new FileInputStream(new File(pathStr));
			FileOutputStream fos = new FileOutputStream(new File(Environment
					.getExternalStorageDirectory().getPath()
					+ "/"
					+ DBHelper.dbFileName));
			fos.getChannel()
			.transferFrom(fis.getChannel(), 0, 32 * 1024 * 1024);
			fos.close();
			fis.close();
			MyLog.i("tag","dbCopyToExt()");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Toast.makeText(mContext, "db copy", Toast.LENGTH_SHORT).show();
	}
}
