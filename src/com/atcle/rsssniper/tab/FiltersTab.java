
package com.atcle.rsssniper.tab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.AddFilterActivity;
import com.atcle.rsssniper.ModifyFeedActivity;
import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperActivity;
import com.atcle.rsssniper.RSSSniperApplication;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.tab.ReadTab.headerOnClickListener;
import com.atcle.rsssniper.tab.listadapter.FilterListAdapter;
import com.atcle.rsssniper.tab.listadapter.ReadListAdapter;

public class FiltersTab extends Activity{
	RSSSniperActivity mainActivity;
	RSSSniperMain main;
	ListView filterList;
	FilterListAdapter adapter;
	
	public FiltersTab(RSSSniperActivity amainActivity){
		mainActivity=amainActivity;
		main=((RSSSniperApplication)mainActivity.getApplication()).getMain();
		filterList=(ListView)mainActivity.findViewById(R.id.feed_Filter_listView);
		filterList.setDivider(new ColorDrawable(Color.parseColor("#555555")));
		filterList.setDividerHeight(1);
		
		adapter=new FilterListAdapter(main, this, mainActivity);
		filterList.setAdapter(adapter);
		
		filterList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MyLog.i("tag","onItemClick "+arg3);
				Intent it7=new Intent(mainActivity,AddFilterActivity.class);
				it7.putExtra("idx", (int)arg3);
				mainActivity.startActivityForResult(it7, RSSSniperActivity.MENU_RDT_FITER);
			}
		});
	}
	
	public void update(){
		adapter.notifyDataSetChanged();
	}

}
