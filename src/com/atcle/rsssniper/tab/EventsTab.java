package com.atcle.rsssniper.tab;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperActivity;
import com.atcle.rsssniper.RSSSniperApplication;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.tab.listadapter.EventListAdapter;

public class EventsTab {
	ListView listView;
	RSSSniperActivity mainActivity;
	RSSSniperMain main;
	EventListAdapter adapter;
	//ArrayList<>

	public EventsTab(RSSSniperActivity rssSniperActivity){
		mainActivity=rssSniperActivity;
		main=((RSSSniperApplication)mainActivity.getApplication()).getMain();
		
		adapter=new EventListAdapter(main, mainActivity, this);
		listView=(ListView)rssSniperActivity.findViewById(R.id.feed_Event_listView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new eventOnItemClickListener(main));
	}

	public void update(){
		adapter.updateList();
		adapter.notifyDataSetChanged();
	}
	class eventOnItemClickListener implements OnItemClickListener{
		RSSSniperMain main;
		public eventOnItemClickListener(RSSSniperMain aMain){
			main=aMain;
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			//int idx=arg3;
			main.setMainReadTabByIdx((int)arg3);
		}
	}
}
