package com.atcle.rsssniper.pref;

import java.util.Date;
import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperApplication;
import com.atcle.rsssniper.main.RSSSniperMain;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;

public class PrefActivity extends PreferenceActivity {

	BackupManager backupManager;
	RSSSniperMain main;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main=((RSSSniperApplication)getApplication()).getMain();
		addPreferencesFromResource(R.layout.pref);

		findPreference("help").setOnPreferenceClickListener(helpClickListener);
		findPreference("help").setSummary("RSSSniper version : "+getVersionInfo());
		
		backupManager=new BackupManager(this, main);
		findPreference("initDb").setOnPreferenceClickListener(backupManager.initDbClickListener);
		findPreference("restoreDb").setOnPreferenceClickListener(backupManager.restoreDbClickListener);
		findPreference("backupDb").setOnPreferenceClickListener(backupManager.backupDbClickListener);
		findPreference("mail").setOnPreferenceClickListener(mailClickListener);
	}

	/** 도움말	 */
	OnPreferenceClickListener helpClickListener=new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			startActivity(new Intent(getApplicationContext(),HelpActivity.class));
			return true;
		}
	};
	/** 의견 보내기 */
	OnPreferenceClickListener mailClickListener=new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			Uri emailUri=Uri.parse("mailto:atcle1@gmail.com");
			Intent email=new Intent(Intent.ACTION_SENDTO, emailUri);
			
			startActivity(email);
			return true;
		}
	};
	
	String getVersionInfo(){
		try{
			PackageInfo info=getPackageManager().getPackageInfo(getPackageName(), 0);
			String version=info.versionName+"."+info.versionCode;
			return version;
		}catch(Exception ex){

		}
		return "";
	}
}
