package com.atcle.rsssniper.recommend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.atcle.log.MyLog;



import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class DBHandler {
	private DBHelper helper;
	private SQLiteDatabase db;
	public static final String tableName=DBHelper.tableName;
	private Context mContext;

	public DBHandler(Context ctx){
		mContext=ctx;
		this.helper=new DBHelper(ctx);
		this.db=helper.getWritableDatabase();
	}


	public void close(){
		helper.close();
	}
	public void deleteDB(){
		db.delete(tableName, "", null);
	}
	public long deleteItem(int idx){
		return db.delete(tableName, "IDX=?", new String[]{Integer.toString(idx)});
	}
	public void restoreDBFromAsset(){		
		try{
			MyLog.i("tag","restoreDBFromAsset()");
			String pathStr=helper.getWritableDatabase().getPath();
			helper.close();
			AssetManager am=mContext.getAssets();
			InputStream is=am.open(DBHelper.dbFileName);
			FileOutputStream fos=new FileOutputStream(new File(pathStr));

			byte[] buffer = new byte[1024 * 8];
			while(true) {
				int count = is.read(buffer);
				if(count == -1) {
					break;
				}
				fos.write(buffer, 0, count );
			}

			fos.close();
			is.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		db=helper.getWritableDatabase();
	}


	/**
	 * @return 1: insert 2: update -1: error
	 */
	public int sync(DBItem item){
		long r=0;
		r=insert(item);
		if(r==-1){
			r=update(item);
			if(r!=-1){
				return 2;
			}
			return -1;
		}
		return 1;
	}

	public long update(DBItem item){
		ContentValues values=putItemIntoValues(item);		
		return db.update(tableName, values, "IDX=?", new String[]{Integer.toString(item.IDX)});
	}

	public long insert(DBItem item){
		int r=-1;
		try{
			ContentValues values=putItemIntoValues(item);
			r=(int)db.insert(tableName, null, values);
		}catch(SQLiteConstraintException ex){
		}catch(Exception ex){}
		return r; 
	}

	public DBItem getItem(int idx){
		DBItem item=null;
		String sql="select * from "+DBHandler.tableName+" where IDX="+idx;
		Cursor cursor=db.rawQuery(sql, null);
		if(cursor!=null){
			if(cursor.moveToFirst()){
				item=makeItemFromCursor(cursor);
			}
		}
		cursor.close();
		return item;
	}

	public ArrayList<DBItem> getItemsByPidx(int pidx){
		DBItem item=null;
		ArrayList<DBItem> items=new ArrayList<DBItem>();

		String sql="select * from "+DBHandler.tableName+" where PIDX="+pidx+" order by ORDERS asc";
		Cursor cursor=db.rawQuery(sql, null);
		if(cursor!=null){
			while(cursor.moveToNext()){
				item=makeItemFromCursor(cursor);
				items.add(item);			
			}
		}
		cursor.close();
		return items;
	}




	private DBItem makeItemFromCursor(Cursor cursor){
		DBItem item=new DBItem();
		int index=0;
		index=cursor.getColumnIndex("IDX");
		item.IDX=cursor.getInt(index);
		index=cursor.getColumnIndex("PIDX");
		item.PIDX=cursor.getInt(index);
		index=cursor.getColumnIndex("TYPES");
		item.TYPES=cursor.getInt(index);
		index=cursor.getColumnIndex("ORDERS");
		item.ORDER=cursor.getInt(index);
		index=cursor.getColumnIndex("TITLE");
		item.TITLE=cursor.getString(index);
		index=cursor.getColumnIndex("DESCS");
		item.DESCS=cursor.getString(index);
		index=cursor.getColumnIndex("URL");
		item.URL=cursor.getString(index);
		return item;
	}

	private ContentValues putItemIntoValues(DBItem item){
		ContentValues values=new ContentValues();
		values.put("IDX", item.IDX);
		values.put("PIDX", item.PIDX);
		values.put("TYPES", item.TYPES);
		values.put("ORDERS", item.ORDER);
		values.put("TITLE", item.TITLE);
		values.put("DESCS", item.DESCS);
		values.put("URL", item.URL);
		return values;
	}
}
