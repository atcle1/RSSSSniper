package com.atcle.rsssniper.pref;



import com.atcle.rsssniper.R;

import android.R.color;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HelpActivity extends Activity{
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		webView=(WebView)findViewById(R.id.help_webview);
		webView.loadUrl("file:///android_asset/help.html");
		webView.setBackgroundColor(color.black);
	}
}
