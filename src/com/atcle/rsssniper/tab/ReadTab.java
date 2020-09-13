package com.atcle.rsssniper.tab;

import java.text.SimpleDateFormat;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperActivity;
import com.atcle.rsssniper.RSSSniperApplication;
import com.atcle.rsssniper.main.UpdateScheduler;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.tab.listadapter.ReadListAdapter;

public class ReadTab {
	private RSSSniperActivity mainActivity;
	private RSSSniperMain main;
	private ListView readList;
	private ReadListAdapter readListAdapter;

	private LinearLayout headerLL;
	private TextView titleTV;
	private TextView updateDateTV;
	private TextView countTV;
	private WebView webView;
	private TextView webViewLoading;

	private SpannableStringBuilder sp;
	private SpannableStringBuilder sp2;

	public boolean bReadOne;
	public boolean bAutoClose;
	public static int fontSize;
	public static float fontSpace;
	private SharedPreferences prefs;

	public boolean []bOpened;	//���뺸���ִ���
	public int newCountCopy=0;	//���������� �����ؼ� ������ ����, �������� 0���� �ٷ���
	public FeedItem feed;		//���� �����ִ� �ǵ�

	public ReadTab(RSSSniperActivity amainActivity){
		mainActivity=amainActivity;
		main=((RSSSniperApplication)mainActivity.getApplication()).getMain();
		headerLL=(LinearLayout)mainActivity.findViewById(R.id.feed_Read_header);

		titleTV=(TextView)mainActivity.findViewById(R.id.feed_Read_title);
		updateDateTV=(TextView)mainActivity.findViewById(R.id.feed_Read_updateDate);
		countTV=(TextView)mainActivity.findViewById(R.id.feed_Read_count);

		readList=(ListView)mainActivity.findViewById(R.id.feed_Read_listView);
		readList.setDivider(new ColorDrawable(Color.parseColor("#555555")));
		readList.setDividerHeight(1);

		readListAdapter=new ReadListAdapter(this, mainActivity);
		readList.setAdapter(readListAdapter);

		headerLL.setOnClickListener(new headerOnClickListener());

		readList.setOnItemClickListener(new readListOnItemClick());

		sp = new SpannableStringBuilder(); 
		sp2 = new SpannableStringBuilder();  

		webView=(WebView)mainActivity.findViewById(R.id.feed_Read_webView);
		webView.setWebViewClient(new SmartWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webViewLoading=(TextView)mainActivity.findViewById(R.id.feed_Read_webView_loading);

		updateNotSelected();

		prefs=PreferenceManager.getDefaultSharedPreferences(mainActivity);
		loadPrefSettings();
	}
	public void loadPrefSettings(){
		bReadOne=prefs.getBoolean("rt_read_one", false);
		bAutoClose=prefs.getBoolean("rt_auto_close", false);
		fontSize=Integer.parseInt(prefs.getString("rt_text_size", "15"));
		fontSpace=Float.parseFloat(prefs.getString("rt_text_space", "1.5f"));
	}

	public void updateNotSelected(){
		feed=null;
		update();
	}

	public void update(){
		if(feed==null){
			MyLog.i("tag","update feed null");
			titleTV.setText("Not selected");
			updateDateTV.setText("");
			countTV.setText("");
			readListAdapter.notifyDataSetChanged();
			return;
		}

		if(feed.lastUpdateDate!=null){
			updateDateTV.setText(feed.lastUpdateDate.toLocaleString());
		}else{
			//������Ʈ �ȵȰ�� ������Ʈ��
			updateDateTV.setText("no updated");
			mainActivity.showToastMessage("������Ʈ���Դϴ�");
			main.updateFeed(ReadTab.this.feed);
		}

		titleTV.setText(feed.title);

		bOpened=new boolean[feed.rssItems.size()];

		readListAdapter.notifyDataSetChanged();

		sp.clear();
		sp.append(String.valueOf(feed.newCount));
		sp.setSpan(new ForegroundColorSpan(Color.GREEN), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.append("/");

		sp2.clear();
		sp.append(String.valueOf(feed.rssItems.size()));
		sp2.setSpan(new ForegroundColorSpan(Color.WHITE), 0, sp2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  

		sp.append(sp2);   
		countTV.setText(sp);

		//�ǵ� ����newcount�� 0���� ������
		//���������� ���纻�� ������ ����Ʈ���� ������.
		//newcount�� �ٷ��ϸ� 0�̵ǹǷ� �̷��� ��.
		newCountCopy=feed.newCount;
		feed.newCount=0;
		
		prevViewPos=0;

	}

	public void update(FeedItem afeed){

		feed=afeed;
		update();
		readList.setSelectionFromTop(0, 0);
	}
	public boolean bShowWebView=false;
	/** back ��������, ������ ó���ϸ� TRUE, ���ϸ� false	 */
	public boolean onBackPressed(){
		if(bShowWebView){
			if(webView.canGoBackOrForward(-2)){
				webView.goBack();
			}else{
				showList();
			}
			return true;
		}
		return false;
	}
	public void onOptionsItemSelected(int id){
		switch(id){
		case RSSSniperActivity.MENU_RDT_BACK:
			webViewGoBackOrForward(-1);
			break;
		case RSSSniperActivity.MENU_RDT_FORWARD:
			webViewGoBackOrForward(1);
			break;
		case RSSSniperActivity.MENU_RDT_REFRESH:
			webView.reload();
			break;
		case RSSSniperActivity.MENU_RDT_LIST:
			showList();
			break;
		}
	}
	public void webViewGoBackOrForward(int steps){
		if(webView.canGoBackOrForward(steps)){
			webView.goBackOrForward(steps);
		}
	}

	public void showList(){
		if(bShowWebView){
			webView.clearView();
			webView.clearHistory();

			readList.setVisibility(View.VISIBLE);
			headerLL.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);

			bShowWebView=false;
		}
	}

	public void showURL(String url){
		if(url==null){
			mainActivity.showToastMessage("link �ּҰ� �ùٸ��� �ʽ��ϴ�.");
			return;
		}
		webView.clearView();
		webView.loadUrl(url);

		readList.setVisibility(View.GONE);
		headerLL.setVisibility(View.GONE);
		webView.setVisibility(View.VISIBLE);
		bShowWebView=true;
	}

	/** ������ �ǵ�Ÿ��Ʋ Ŭ��	 */
	class headerOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mainActivity.openOptionsMenu();
			//			RSSSniperMain.getInstance().updateFeed(ReadTab.this.feed);
		}
	}

	/** ����Ʈ ������ Ŭ��������:���뺸����	 */
	int prevViewPos=0;
	View prevView=null;
	class readListOnItemClick implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int pos=(int)arg3;
			View view=(View)arg1.findViewById(R.id.feed_view_row_description);
			if(view.isShown()){
				view.setVisibility(View.GONE);
				bOpened[pos]=false;
			}else{
				ReadListAdapter.setRSSItemText(feed.rssItems.get(pos), view);
				bOpened[pos]=true;

				if(bReadOne){
					if(prevViewPos!=pos){
						if(prevView!=null)
							prevView.findViewById(R.id.feed_view_row_description).setVisibility(View.GONE);
						bOpened[prevViewPos]=false;
					}
				}
				prevView=view;
				prevViewPos=pos;
			}
		}
	}

	private class SmartWebViewClient extends WebViewClient{
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon){
			super.onPageStarted(view,  url, favicon);
			ReadTab.this.webViewLoading.setVisibility(View.VISIBLE);
			
		}
		@Override
		public void onPageFinished(WebView view, String url){
			super.onPageFinished(view,  url);
			ReadTab.this.webViewLoading.setVisibility(View.GONE);
		}
	}


}
