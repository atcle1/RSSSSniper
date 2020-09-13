package com.atcle.rsssniper;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.pref.PrefActivity;
import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.tab.EventsTab;
import com.atcle.rsssniper.tab.FeedsTab;
import com.atcle.rsssniper.tab.FiltersTab;
import com.atcle.rsssniper.tab.ReadTab;
import com.atcle.rsssniper.tab.listadapter.ReadListAdapter;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

public class RSSSniperActivity extends TabActivity{
	public static final int MENU_FDT_ADD_FEED=Menu.FIRST+1;
	public static final int MENU2=Menu.FIRST+2;
	public static final int MENU_FDT_UPDATE_ALL=Menu.FIRST+3;
	public static final int MENU4=Menu.FIRST+4;
	public static final int MENU_RDT_UPDATE=Menu.FIRST+5;
	public static final int MENU_RDT_MODIFY=Menu.FIRST+6;
	public static final int MENU_RDT_FITER=Menu.FIRST+7;
	public static final int MENU_RDT_OPEN_LINK=Menu.FIRST+8;
	public static final int MENU_EVT_REMOVE=Menu.FIRST+9;
	public static final int MENU_RDT_OPEN_MLINK=Menu.FIRST+10;
	public static final int MENU_RDT_BACK=Menu.FIRST+11;
	public static final int MENU_RDT_FORWARD=Menu.FIRST+12;
	public static final int MENU_RDT_REFRESH=Menu.FIRST+13;
	public static final int MENU_RDT_LIST=Menu.FIRST+14;

	public static final int TAB1=0;
	public static final int TAB2=1;
	public static final int TAB3=2;
	public static final int TAB4=3;

	private TabHost tabHost;

	public Handler activityHandler;
	private RSSSniperMain main;
	private SharedPreferences prefs;

	private ReadTab readTab;
	private FeedsTab feedsTab;
	private FiltersTab filtersTab;
	private EventsTab eventsTab;

	public boolean bBackedPressed=false;
	public Toast toast;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		/* �������ð�ü main �� pref set*/
		main=((RSSSniperApplication)getApplication()).getMain();
		((RSSSniperApplication)getApplication()).setMainActivity(this);
		prefs=PreferenceManager.getDefaultSharedPreferences(this);	

		/* service bind */
		main.doBindService();

		/* tab setting */
		tabHost=getTabHost();
		TabSpec tabSpec1=tabHost.newTabSpec("Tab1").setIndicator("Feeds");
		tabSpec1.setContent(R.id.main_tab1);
		tabHost.addTab(tabSpec1);

		TabSpec tabSpec2=tabHost.newTabSpec("Tab2").setIndicator("Read");
		tabSpec2.setContent(R.id.main_tab2);
		tabHost.addTab(tabSpec2);

		TabSpec tabSpec3=tabHost.newTabSpec("Tab3").setIndicator("Events");
		tabSpec3.setContent(R.id.main_tab3);
		tabHost.addTab(tabSpec3);

		/*
		TabSpec tabSpec4=tabHost.newTabSpec("Tab4").setIndicator("Filters");
		tabSpec4.setContent(R.id.main_tab4);
		tabHost.addTab(tabSpec4);
		*/
		
		TabWidget tw = tabHost.getTabWidget();
		for(int a=0; a<tw.getChildCount(); a++) {
			View v = tw.getChildAt(a);
			v.getLayoutParams().height = 65;
		}
		tabHost.setCurrentTab(TAB1);

		// tast set
		toast=Toast.makeText(this, "", Toast.LENGTH_SHORT);
		if(RSSSniperMain.bOnCreateFirst){
			toast.setText("3G������ �����ϴ� ��� ������� ������ �������� �ƴϰų�, �����ѵ��� �ʰ��� ��� ������ ����� �߻��� �� �ֽ��ϴ�.");
			toast.setDuration(Toast.LENGTH_LONG);
			toast.show();
			RSSSniperMain.bOnCreateFirst=false;
		}
		toast.setDuration(Toast.LENGTH_SHORT);

		//handler set
		setHandler();

		//tab ��ü ����
		feedsTab=new FeedsTab(this);
		readTab=new ReadTab(this);
		filtersTab=new FiltersTab(this);
		eventsTab=new EventsTab(this);

		// ���� ����
		// 5���� ��찡 ����
		// ���� ù ���μ�������, ��Ƽ��Ƽ����
		// ���� ��Ƽ��Ƽ����
		// Notification���� ���� -> �̺�Ʈ �� ������
		// ���񽺽��� �� ��������, ���� ������ -> üũ�ڽ� üũ���·�
		// ���񽺽��� �� ��������, ��Ƽ��Ƽ ������ -> üũ�ڽ� üũ���·�, ���񽺽���
		Boolean bSavedStatus=prefs.getBoolean(getString(R.string.p_bServiceStart), false);
		if(bSavedStatus){//���� �������̾��ٸ�
			feedsTab.setObserveCheckBox(true);	//üũ�ڽ� üũ
			if(main.bObserve==false){	//���񽺰��������� �ƴϸ� ����
				main.bObserveSet(true);
			}
		}



		Intent intent=getIntent();


		Boolean bNotificationIntent=intent.getBooleanExtra("notificationIntent", false);
		MyLog.i("tag","onCreate "+bNotificationIntent);

		if(bNotificationIntent){	//Notification���� �����Ų��� event tab����
			tabHost.setCurrentTab(TAB3);
		}

		tabHost.setOnTabChangedListener(new onTabChangedListener());
		showStartingNotification();
	}
	
	private void showStartingNotification(){
		NotificationManager nfm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification nf=new Notification(R.drawable.icon, "RSS Sniper", System.currentTimeMillis());
		Intent intent=new Intent(this,this.getClass());
		PendingIntent pi=PendingIntent.getActivity(this, 0, intent, 0);
		nf.setLatestEventInfo(getApplicationContext(), "", "", pi);
		nf.flags=Notification.FLAG_AUTO_CANCEL;
		nfm.notify(0, nf);
		nfm.cancelAll();
	}

	private String curTab="Tab1";
	private String preTab="Tab1";

	/** db������, �ʱ�ȭ�Ҷ� ���ο��� ȣ���	 */
	public void initAllTab(){
		feedsTab.update();
		readTab.updateNotSelected();
		eventsTab.update();
		filtersTab.update();
	}

	class onTabChangedListener implements OnTabChangeListener{
		@Override
		public void onTabChanged(String tabId) {
			if(tabId.equals("Tab1")){
				feedsTab.update();
			}else if(tabId.equals("Tab2")){
				readTab.showList();	//�����ִ� �������
				//���⼭ ���� ������Ʈ�ϸ�, showReadTab()���� �ѹ� ������Ʈ,���⼭�ѹ��Ǿ�
				//������Ʈ�� �ι� �Ǿ������ new�� �ȵǾ� ����
			}else if(tabId.equals("Tab3")){
				eventsTab.update();
			}
			/*
			else if(tabId.equals("Tab4")){
				filtersTab.update();
			}
			*/
			preTab=curTab;
			curTab=tabId;
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(Menu.NONE, MENU4, 10, "����").setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		int group=tabHost.getCurrentTab()+1;
		menu.removeGroup(Menu.FIRST);
		menu.removeGroup(Menu.FIRST+1);
		menu.removeGroup(Menu.FIRST+2);
		menu.removeGroup(Menu.FIRST+3);
		if(group==Menu.FIRST){
			menu.add(Menu.FIRST, MENU_FDT_ADD_FEED, Menu.NONE, "�ǵ� �߰�").setIcon(android.R.drawable.ic_menu_add);
			menu.add(Menu.FIRST, MENU_FDT_UPDATE_ALL, Menu.NONE, "��� ������Ʈ").setIcon(android.R.drawable.ic_menu_rotate);
		}else if(group==Menu.FIRST+1){
			if(readTab.feed!=null){
				if(readTab.bShowWebView==false){
					menu.add(Menu.FIRST+1, MENU_RDT_UPDATE, Menu.NONE, "������Ʈ").setIcon(android.R.drawable.ic_menu_rotate);
					menu.add(Menu.FIRST+1, MENU_RDT_OPEN_LINK, Menu.NONE, "��ũ ����").setIcon(android.R.drawable.ic_menu_view);
					menu.add(Menu.FIRST+1, MENU_RDT_OPEN_MLINK, Menu.NONE, "����� ��ũ����").setIcon(android.R.drawable.ic_menu_view);
					menu.add(Menu.FIRST+1, MENU_RDT_MODIFY, Menu.NONE, "����").setIcon(android.R.drawable.ic_menu_edit);
					menu.add(Menu.FIRST+1, MENU_RDT_FITER, Menu.NONE, "���� ����").setIcon(android.R.drawable.ic_menu_manage);
				}else{
					menu.add(Menu.FIRST+1, MENU_RDT_BACK, Menu.NONE, "�ڷ�");
					menu.add(Menu.FIRST+1, MENU_RDT_FORWARD, Menu.NONE, "������");
					menu.add(Menu.FIRST+1, MENU_RDT_REFRESH, Menu.NONE, "���ΰ�ħ");
					menu.add(Menu.FIRST+1, MENU_RDT_LIST, Menu.NONE, "����Ʈ");
				}
			}
		}else if(group==Menu.FIRST+2){
			menu.add(Menu.FIRST+2, MENU_EVT_REMOVE, Menu.NONE, "�̺�Ʈ ��� ����").setIcon(android.R.drawable.ic_menu_delete);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case MENU_FDT_ADD_FEED://�ǵ��� �߰�
			//			Intent it1=new Intent(this,AddFeedActivity.class);
			//			startActivityForResult(it1, MENU_FDT_ADD_FEED);

			//			Intent it11=new Intent(this,OPMLActivity.class);
			//			startActivityForResult(it11, MENU_FDT_ADD_FEED);

			Intent it11=new Intent(this,SelectInputOptionActivity.class);
			startActivityForResult(it11, MENU_FDT_ADD_FEED);
			break;
		case MENU2:
			break;
		case MENU_FDT_UPDATE_ALL://�ǵ��� ��� ������Ʈ
			main.updateFeedAll();
			break;
		case MENU4://����
			Intent it4=new Intent(this, PrefActivity.class);
			startActivityForResult(it4,MENU4);
			break;
		case MENU_RDT_UPDATE://������ ������Ʈ
			main.updateFeed(readTab.feed);
			break;
		case MENU_RDT_MODIFY://������ ����
			Intent it6=new Intent(this,ModifyFeedActivity.class);
			it6.putExtra("idx", readTab.feed.idx);
			startActivityForResult(it6, MENU_RDT_MODIFY);
			break;
		case MENU_RDT_FITER://������ ���ͼ���
			Intent it7=new Intent(this,AddFilterActivity.class);
			it7.putExtra("idx", readTab.feed.idx);
			startActivityForResult(it7, MENU_RDT_FITER);
			break;
		case MENU_RDT_OPEN_LINK://������ ��ũ����
			if(readTab.feed.link!=null && !readTab.feed.link.equals("")){
				Uri browserUri=Uri.parse(readTab.feed.link);
				Intent brower=new Intent(Intent.ACTION_VIEW, browserUri);
				startActivity(brower);
			}else{
				showToastMessage("��ũ�� �ùٸ��� �ʽ��ϴ�.");
			}
			break;
		case MENU_RDT_OPEN_MLINK:
			if(readTab.feed.link!=null && !readTab.feed.link.equals("")){
				Uri browserUri=Uri.parse("http://google.com/gwt/x?u="+readTab.feed.link);
				Intent brower=new Intent(Intent.ACTION_VIEW, browserUri);
				startActivity(brower);
			}else{
				showToastMessage("��ũ�� �ùٸ��� �ʽ��ϴ�.");
			}
			break;
		case MENU_EVT_REMOVE://�̺�Ʈ�� ��λ���
			main.clearEvents();
			eventsTab.update();
			break;
			
		case MENU_RDT_BACK:
		case MENU_RDT_FORWARD:
		case MENU_RDT_REFRESH:
		case MENU_RDT_LIST:
			readTab.onOptionsItemSelected(item.getItemId());
			break;
		}
		return true;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		main.onDestroy();
	}

	@Override
	public void onBackPressed(){
		//�ǿ� ���� �ٸ� �ൿ
		// 2,4 -> back : 1 �̵�
		// 3 -> 2 -> back : 3 �̵�
		// 1 -> back -> back : ����
		if(tabHost.getCurrentTab()!=TAB1){
			if(tabHost.getCurrentTab()==TAB2){
				if(readTab.onBackPressed()==false){
					if(preTab.equals("Tab3")){
						tabHost.setCurrentTab(TAB3);
					}else
						tabHost.setCurrentTab(TAB1);
				}
			}else{
				if(preTab.equals("Tab3")){
					tabHost.setCurrentTab(TAB3);
				}else
					tabHost.setCurrentTab(TAB1);
			}
			return;
		}else {
			if(bBackedPressed){
				toast.cancel();
				finish();
			}else{
				showToastMessage("'�ڷ�'��ư�� �ѹ� �� �����ø� �����մϴ�.");
				toast.show();
				bBackedPressed=true;
				activityHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						RSSSniperActivity.this.bBackedPressed=false;
						toast.cancel();
					}
				}, 3000);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case MENU_FDT_ADD_FEED: // �ǵ� �߰�
			feedsTab.update();
			if(main.bObserve){
				main.observeReschedule();	//������ �ٽ���
			}

			break;
		case MENU4:	// ����
			main.loadPrefSettings();
			readTab.loadPrefSettings();
			break;
		case MENU_RDT_MODIFY:	//������-�ǵ� ����
			if(resultCode==Activity.RESULT_FIRST_USER){	//�����ΰ�� �ǵ��������̵�
				tabHost.setCurrentTab(TAB1);
				readTab.updateNotSelected();						//���þ���
			}else if(resultCode==Activity.RESULT_OK){
				//readTab.update();	//���� ���� ������Ʈ�� �ʿ� ����
			}
			break;
		case MENU_RDT_FITER:	//������ ���ͼ���
			filtersTab.update();
			readTab.update();
			break;
		}
	}

	public void showReadTab(FeedItem feed){
		tabHost.setCurrentTab(TAB2);
		readTab.update(feed);
	}

	public boolean bDisplayed=false;

	public void onResume(){
		super.onResume();
		//activityHandler
		MyLog.v("tag","onResume");
		bDisplayed=true;

		Message msg=Message.obtain();
		msg.what=MSG_FEED_LIST_UPDATE;
		//feed list update all auto
		activityHandler.sendMessageDelayed(msg, 0);

		if(curTab.equals("Tab1")){

		}else if(curTab.equals("Tab2")){
			//			readTab.update();	//podcast���� ��ٰ� ������ ������Ʈ �Ǿ������ �ּ�ó����
		}else if(curTab.equals("Tab3")){
			eventsTab.update();
		}else if(curTab.equals("Tab4")){
			filtersTab.update();
		}
	}

	public void onPause(){
		super.onPause();
		MyLog.v("tag","onPause");
		bDisplayed=false;
		activityHandler.removeMessages(MSG_FEED_LIST_UPDATE);
		toast.cancel();
	}

	public void showToastMessage(String message){
		if(bDisplayed){
			toast.cancel();
			toast.setText(message);
			toast.show();
		}
	}

	//MSG
	public static final int MSG_FEED_UPDATE_START=0;
	public static final int MSG_FEED_UPDATE_COMPLETE=1;
	public static final int MSG_FEED_UPDATE_FAIL=2;
	public static final int MSG_FEED_LIST_UPDATE=3;
	public static final int MSG_FEED_EVENT=4;

	//�ǵ�-���� ������Ʈ
	private void setHandler(){
		activityHandler=new Handler(){
			FeedItem feed;
			Message nmsg;
			@Override
			public void handleMessage(Message msg){
				MyLog.v("tag", "mainActivityHandler receive "+msg.what+" arg1 : "+msg.arg1);
				switch(msg.what){
				case MSG_FEED_UPDATE_START:	//������Ʈ ����
					if(bDisplayed ){
						if(tabHost.getCurrentTab()==TAB1){
							feedsTab.setUpdateMart(msg.arg1);
						}
					}
					break;
				case MSG_FEED_UPDATE_COMPLETE:	//������Ʈ����
					if(bDisplayed){
						feed=main.getFeedByIdx(msg.arg1);
						if(feed==null){
							return;
						}
						RSSSniperActivity.this.showToastMessage("������Ʈ ����\n"+feed.nick);
						if( tabHost.getCurrentTab()==TAB1){
							feedsTab.update();
						}else if(tabHost.getCurrentTab()==TAB2){
							if(readTab.feed!=null && readTab.feed.idx==msg.arg1){
								//���� �����ִ°� �϶��� ������ ������Ʈ
								readTab.update();
							}
						}
					}
					break;
				case MSG_FEED_UPDATE_FAIL:	//������Ʈ ����
					if(bDisplayed ){
						feed=main.getFeedByIdx(msg.arg1);
						if(feed==null){
							return;
						}
						RSSSniperActivity.this.showToastMessage("������Ʈ����\n"+feed.nick);
						if(tabHost.getCurrentTab()==TAB1){
							feedsTab.update();
						}
					}
					break;
				case  MSG_FEED_LIST_UPDATE:	//�ǵ帮��Ʈ ������Ʈ�䱸
					//onPause���� msg���ֹǷ� ǥ�õȶ��� �� �޼����� ����
					//1�и��� ������Ʈ��
					if(tabHost.getCurrentTab()==TAB1){
						feedsTab.update();
					}
					nmsg=Message.obtain();
					nmsg.what=MSG_FEED_LIST_UPDATE;
					activityHandler.removeMessages(MSG_FEED_LIST_UPDATE);	//Ȥ�ó� �ߺ� ����
					activityHandler.sendMessageDelayed(nmsg, 60000);
					break;
				case MSG_FEED_EVENT:	//���͸� �Ȱ� ����
					if(bDisplayed && tabHost.getCurrentTab()==TAB3)
						eventsTab.update();
					break;
				}
			}
		};
	}
}

