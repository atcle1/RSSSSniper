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
					new AlertDialog.Builder(mContext).setTitle("�����").setMessage(backupFilePath+"\n��� ������ �̹� �����մϴ�. ���� ����ðڽ��ϱ�?").setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							backupWork();
						}
					}).setNegativeButton("���", new DialogInterface.OnClickListener() {
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
	/** db ����������	 */
	public OnPreferenceClickListener restoreDbClickListener=new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			setupBackupPath();
			File backupFile=new File(backupFilePath);
			if(backupFile.exists()==false){
				Toast.makeText(mContext, backupFilePath+" �� �������� �ʽ��ϴ�.", Toast.LENGTH_SHORT).show();
				return true;
			}else{
				new AlertDialog.Builder(mContext).setTitle("���� Ȯ��").setMessage(backupFilePath+"\n��� ���Ϸ� �����Ͻðڽ��ϱ�?").setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						restoreWork();
						main.reloadDb();
					}
				}).setNegativeButton("���", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
			}
			return true;
		}
	};

	/** db�ʱ�ȭŬ��������
	 */
	public OnPreferenceClickListener initDbClickListener=new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			new AlertDialog.Builder(mContext).setTitle("�ʱ�ȭ Ȯ��").setMessage("��� ����� �����Ͻðڽ��ϱ�?").setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
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
			}).setNegativeButton("���", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			}).show();
			return true;
		}
	};
	
	/**����޸𸮻��°˻� & ���� ���� ����, ���ϰ�� ����	 */
	private void setupBackupPath(){
		String state=Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			backupDirPath=Environment.getExternalStorageDirectory()+"/RSSSniper/";
		}else{	//����޸𸮻��Ұ��ɽ� ������� ���
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
			Toast.makeText(mContext, backupFilePath+" �� ����Ϸ�!", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(mContext, backupFilePath+" �����Ϸ�!", Toast.LENGTH_SHORT).show();
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
