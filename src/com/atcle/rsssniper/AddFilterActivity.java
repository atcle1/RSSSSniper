package com.atcle.rsssniper;

import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.rss.FeedFilter;
import com.atcle.rsssniper.rss.FeedItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddFilterActivity extends Activity{
	Intent resultIntent = new Intent();
	RSSSniperMain main;
	FeedItem feed;
	EditText includeET;
	EditText excludeET;
	int idx=-1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_filter_dlg);
		
		main=((RSSSniperApplication)getApplication()).getMain();

		TextView tv=(TextView)findViewById(R.id.add_filter_title);
		includeET=(EditText)findViewById(R.id.add_filter_include);
		excludeET=(EditText)findViewById(R.id.add_filter_exclude);

		idx=getIntent().getIntExtra("idx", -1);
		feed=main.getFeedByIdx(idx);
		if(feed==null){
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			});
		}else{
			tv.setText("*"+feed.nick);
			tv.setFocusable(true);
			includeET.setText(feed.filter.getFilterStr(FeedFilter.INCLUDEWORLD));
			excludeET.setText(feed.filter.getFilterStr(FeedFilter.EXCLUDEWORLD));
		}
	}
	public void onClickOK(View view){
		String inCludeText=includeET.getText().toString().trim();
		String exCludeText=excludeET.getText().toString().trim();
		feed.filter.setFilter(FeedFilter.INCLUDEWORLD, inCludeText);
		feed.filter.setFilter(FeedFilter.EXCLUDEWORLD, exCludeText);
		
		main.dbModifyFeed(feed);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
	public void onClickCancel(View view){
		setResult(Activity.RESULT_CANCELED, resultIntent);
		finish();
	}
}
