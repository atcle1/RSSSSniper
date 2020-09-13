package com.atcle.rsssniper.pref;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.db.RSSListDbHelper;
import com.atcle.rsssniper.main.RSSSniperMain;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;

public class BackupManager {
	Context mContext;
	RSSSniperMain main;
	String backupDirPath;
	String backupFilePath;
	String dbFilePathStr;
	
	public BackupManager(Context amContext, RSSSniperMain aMain){
		mContext=amContext;
		dbFilePathStr=getDbPath();
		
		main=aMain;
	}

	/** db backup listener	 */
	public OnPreferenceClickListener backupDbClickListener=new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			try {
				setupBackupPath();
				File backupFile=new File(backupFilePath);
				if(backupFile.exists()==true){
					new AlertDialog.Builder(mContext).setTitle("덮어쓰기").setMessage(backupFilePath+"\n백업 파일이 이미 존재합니다. 덮어 씌우시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							backupWork();
						}
					}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					}).show();
				}else{
					backupWork();
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			return true;
		}
	};
	/** db 복원리스너	 */
	public OnPreferenceClickListener restoreDbClickListener=new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			setupBackupPath();
			File backupFile=new File(backupFilePath);
			if(backupFile.exists()==false){
				Toast.makeText(mContext, backupFilePath+" 가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
				return true;
			}else{
				new AlertDialog.Builder(mContext).setTitle("복원 확인").setMessage(backupFilePath+"\n백업 파일로 복원하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						restoreWork();
						main.reloadDb();
					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
			}
			return true;
		}
	};

	/** db초기화클릭리스너
	 */
	public OnPreferenceClickListener initDbClickListener=new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			new AlertDialog.Builder(mContext).setTitle("초기화 확인").setMessage("모든 목록을 삭제하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try{
						main.closeDb();
						File f=new File(getDbPath());
						f.delete();
						main.openDb();
						main.reloadDb();
						//TempPref.bDbChanged=true;
					}catch(Exception ex){ex.printStackTrace();}
				}
			}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			}).show();
			return true;
		}
	};
	
	/**외장메모리상태검사 & 저장 폴더 생성, 파일경로 설정	 */
	private void setupBackupPath(){
		String state=Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			backupDirPath=Environment.getExternalStorageDirectory()+"/RSSSniper/";
		}else{	//외장메모리사용불가능시 내장공간 사용
			backupDirPath="/data/data/com.atcle.rsssniper/files/";
		}
		File backupFile=new File(backupDirPath);
		backupFile.mkdirs();
		backupFilePath=backupDirPath+"backupDb.db";
		MyLog.i("tag", "backuppath : "+backupFilePath);
	}
	
	private void backupWork(){
		try {
			FileInputStream fis=new FileInputStream(new File(dbFilePathStr));
			FileOutputStream fos=new FileOutputStream(new File(backupFilePath));
			fos.getChannel().transferFrom(fis.getChannel(), 0, 32 * 1024 * 1024);
			fos.close();
			fis.close();
			Toast.makeText(mContext, backupFilePath+" 에 저장완료!", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void restoreWork(){
		try {
			main.closeDb();
			FileInputStream fis=new FileInputStream(new File(backupFilePath));
			FileOutputStream fos=new FileOutputStream(new File(dbFilePathStr));
			fos.getChannel().transferFrom(fis.getChannel(), 0, 32 * 1024 * 1024);
			fos.close();
			fis.close();
			main.openDb();
			Toast.makeText(mContext, backupFilePath+" 복원완료!", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getDbPath(){
		RSSListDbHelper t=new RSSListDbHelper(mContext.getApplicationContext());
		String pathStr=t.getWritableDatabase().getPath();
		t.close();
		return pathStr;
	}
}
