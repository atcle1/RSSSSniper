package com.atcle.rsssniper.tab;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperActivity;
import com.atcle.rsssniper.RSSSniperApplication;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.tab.ReadTab.headerOnClickListener;
import com.atcle.rsssniper.tab.listadapter.FeedListAdapter;
import com.atcle.rsssniper.tab.listadapter.ReadListAdapter;

public class FeedsTab {
	private RSSSniperActivity mainActitivity;
	private ListView readList;
	private FeedListAdapter feedListAdapter;
	private ToggleButton bObserveToggleButton;
	private RSSSniperMain main;
	public boolean []bOpened;
	
	

	public FeedItem feed;

	public FeedsTab(RSSSniperActivity amainActivity){
		mainActitivity=amainActivity;
		main=((RSSSniperApplication)mainActitivity.getApplication()).getMain();
		
		readList=(ListView)mainActitivity.findViewById(R.id.feed_List_listView);
		feedListAdapter=new FeedListAdapter(main, this, mainActitivity);
		readList.setAdapter(feedListAdapter);
		
		bObserveToggleButton=(ToggleButton)mainActitivity.findViewById(R.id.feed_snipe_toggle);
		bObserveToggleButton.setOnCheckedChangeListener(new obseveCheckBoxChangedListener());

		
		
		readList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				main.setMainReadTabByPos(arg2);
			}
		});


	}
	public void setUpdateMart(int idx){
		int pos=main.getPosByIdx(idx);
		if(pos==-1){
			return;
		}
		pos=pos-readList.getFirstVisiblePosition();	//리스트에서 상대적 위치
		View v=readList.getChildAt(pos);
		if(v!=null){
			TextView tv=(TextView)v.findViewById(R.id.feed_list_row_nick);
			v.setBackgroundColor(Color.parseColor("#005555"));
			tv.append(" Update...");
		}
	}
	public void setObserveCheckBox(boolean bChecked){
		bObserveToggleButton.setOnCheckedChangeListener(null);
		bObserveToggleButton.setChecked(bChecked);
		bObserveToggleButton.setOnCheckedChangeListener(new obseveCheckBoxChangedListener());
	}

	public void update(){
		feedListAdapter.notifyDataSetChanged();
	}
	
	class obseveCheckBoxChangedListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			MyLog.e("tag","onCheckedChagned "+isChecked);
			FeedsTab.this.main.bObserveSet(isChecked);			
		}
		
	}

}
