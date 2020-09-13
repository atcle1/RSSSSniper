package com.atcle.rsssniper;

import com.atcle.rsssniper.recommend.RecommendFeedActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SelectInputOptionActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_input_option_dlg);
	}
	
	public void onClickManualURL(View view){
		Intent it1=new Intent(this,AddFeedActivity.class);
		startActivityForResult(it1, 1);
		

	}
	public void onClickOPML(View view){
		Intent it11=new Intent(this,OPMLActivity.class);
		startActivityForResult(it11, 2);
	}
	
	public void onClickRecommend(View view){
		Intent it11=new Intent(this,RecommendFeedActivity.class);
		startActivityForResult(it11, 3);
	}
}
