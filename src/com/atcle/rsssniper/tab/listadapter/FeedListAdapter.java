package com.atcle.rsssniper.tab.listadapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperActivity;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.tab.FeedsTab;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class FeedListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private RSSSniperActivity context;
	private RSSSniperMain main;
	private FeedsTab feedsTab;
	private CheckBoxChangedListener checkBoxChangedListener;
	public FeedListAdapter(RSSSniperMain amain, FeedsTab afeedsTab, RSSSniperActivity acontext){
		feedsTab=afeedsTab;
		context=acontext;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		main=amain;
		checkBoxChangedListener=new CheckBoxChangedListener();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return main.getFeedCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return main.getFeedByPos(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.feed_list_row, parent, false);
		}
		convertView.setBackgroundColor(Color.TRANSPARENT);
		FeedItem feed=main.getFeedByPos(position);
		//nick
		TextView tv=(TextView)convertView.findViewById(R.id.feed_list_row_nick);
		tv.setText(feed.nick);
		
		//update date
		tv=(TextView)convertView.findViewById(R.id.feed_list_row_updateDate);
		if(feed.lastUpdateDate!=null){
			long leftedMin=((new Date()).getTime()-feed.lastUpdateDate.getTime())/60000;
			tv.setText(df.format(feed.lastUpdateDate)+" / "+leftedMin+" min ago");
		}else{
			tv.setText("no updated");
		}
			
		//interval
		tv=(TextView)convertView.findViewById(R.id.feed_list_row_interval);
		tv.setText(String.valueOf(feed.pollingInterval));
		//feedcount
		tv=(TextView)convertView.findViewById(R.id.feed_list_row_feedCount);
		tv.setText(feed.newCount+"/"+feed.rssItems.size());
		//checkbox
		CheckBox cb=(CheckBox)convertView.findViewById(R.id.feed_list_row_check);
		cb.setOnCheckedChangeListener(null);
		cb.setChecked(feed.status==FeedItem.STATUS_OBSERVE);
		//set listener
		cb.setOnCheckedChangeListener(checkBoxChangedListener);
		
		convertView.setTag(position);	//set tag
		return convertView;
	}
	
	class CheckBoxChangedListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			int pos=(Integer) ((View)buttonView.getParent()).getTag();
			main.onFeedListViewCheckBoxChanged(pos, isChecked);
		}
	}	
}
