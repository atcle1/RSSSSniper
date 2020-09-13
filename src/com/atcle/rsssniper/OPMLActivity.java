package com.atcle.rsssniper;

import java.util.ArrayList;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.RSSSniperActivity.onTabChangedListener;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.parser.OPMLParser;
import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.tab.EventsTab;
import com.atcle.rsssniper.tab.FeedsTab;
import com.atcle.rsssniper.tab.FiltersTab;
import com.atcle.rsssniper.tab.ReadTab;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class OPMLActivity extends Activity {
	EditText path_edit;
	TextView list_text;
	String path;
	ArrayList<FeedItem> feeds;
	RSSSniperMain main;
	OPMLParser parser=new OPMLParser();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_opml);

		path_edit=(EditText)findViewById(R.id.opml_path_edit);
		main=((RSSSniperApplication)getApplication()).getMain();
		list_text=(TextView)findViewById(R.id.opml_rss_list);
	}

	public void onClickFind(View view){
		list_text.setText("");
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("text/*");
		startActivityForResult(intent, 1);
	}
	public void onClickLoad(View view){
		String list="";

		try{
			path=path_edit.getText().toString();
			feeds=parser.parse(path);
			for(int i=0; i<feeds.size(); i++){
				FeedItem item=feeds.get(i);
				list+=item.nick+"\r\n"+item.RSSUrl+"\r\n\r\n";
			}
			MyLog.i("tag",list);
			list_text.setText(list);
		}catch(Exception ex){
			ex.printStackTrace();
			list_text.setText("Parse failed");
		}

	}
	public void onClickImport(View view){
		int count=0;
		int fcount=0;
		try{

			for(int i=0; i<feeds.size(); i++){
				FeedItem item=feeds.get(i);
				if(main.isExistUrl(item.RSSUrl)==false){
					main.insertFeed(item);
					count++;
				}else{
					fcount++;
				}
				MyLog.i("tag",item.nick+" "+item.RSSUrl);

			}
			Toast.makeText(this, count+"개 추가\r\n"+fcount+"개 이미 존재\r\n", Toast.LENGTH_SHORT).show();
		}catch(Exception ex){
			ex.printStackTrace();
		}

		finish();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case 1:
			if(data!=null){
				try{
					String path="";
					Uri selectedUri = data.getData();
					path=selectedUri.getPath();
					path_edit.setText(path);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			break;
		}
	}
}
