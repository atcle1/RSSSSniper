package com.atcle.rsssniper.tab.listadapter;

import java.util.Date;
import java.util.LinkedList;

import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperActivity;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.rss.FilterResult;
import com.atcle.rsssniper.tab.EventsTab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EventListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	EventsTab et;
	RSSSniperActivity context;
	RSSSniperMain main;
	LinkedList<FilterResult> frList;
	public EventListAdapter(RSSSniperMain amain, RSSSniperActivity acontext, EventsTab aet){
		et=aet;
		context=acontext;
		main=amain;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		frList=main.feedEvents;
	}
	public void updateList(){
		frList=main.feedEvents;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return frList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		long id=0;
		try{
			id=frList.get(frList.size()-1-position).idx;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return id;
	}

	static TextView tv;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.feed_event_row, parent, false);
		}
		FilterResult item=frList.get(frList.size()-1-position);

		tv=(TextView)convertView.findViewById(R.id.feed_event_row_title);
		tv.setText(item.title);

		tv=(TextView)convertView.findViewById(R.id.feed_event_row_content1);
		tv.setText("*"+item.titls[0]);
		tv=(TextView)convertView.findViewById(R.id.feed_event_row_content2);
		tv.setText("");
		tv=(TextView)convertView.findViewById(R.id.feed_event_row_content3);
		tv.setVisibility(View.GONE);
		tv.setText("");
		
		if(item.titls.length>=2){
			tv=(TextView)convertView.findViewById(R.id.feed_event_row_content2);
			tv.setVisibility(View.VISIBLE);
			tv.setText("*"+item.titls[1]);
		}
		if(item.titls.length==3){
			tv=(TextView)convertView.findViewById(R.id.feed_event_row_content3);
			tv.setVisibility(View.VISIBLE);
			tv.setText("*"+item.titls[2]);
		}else if(item.titls.length>3){
			tv=(TextView)convertView.findViewById(R.id.feed_event_row_content3);
			tv.setVisibility(View.VISIBLE);
			tv.setText("..."+(item.titls.length-2)+" more itmes");
		}

		tv=(TextView)convertView.findViewById(R.id.feed_event_row_date);
		tv.setText(item.d.toLocaleString());
		return convertView;
	}

}
