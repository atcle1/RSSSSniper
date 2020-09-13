package com.atcle.rsssniper.tab.listadapter;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperActivity;
import com.atcle.rsssniper.main.RSSSniperMain;
import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.tab.FeedsTab;
import com.atcle.rsssniper.tab.FiltersTab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FilterListAdapter extends BaseAdapter {
	FiltersTab filtersTab;
	RSSSniperActivity context;
	private LayoutInflater mInflater;
	RSSSniperMain main;
	public FilterListAdapter(RSSSniperMain aMain, FiltersTab aFiltersTab, RSSSniperActivity acontext){
		filtersTab=aFiltersTab;
		context=acontext;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		main=aMain;
	}

	@Override
	public int getCount() {
		int count=0;
		for(int i=0; i<main.getFeedCount(); i++){
			if(main.getFeedByPos(i).filter.isFilterEnabled()){
				count++;
			}
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		FeedItem feed=null;
		int count=0;
		for(int i=0; i<main.getFeedCount(); i++){
			feed=main.getFeedByPos(i);
			if(feed.filter.isFilterEnabled()){
				if(count==position){
					break;
				}
				count++;
			}
		}
		if(feed==null)
			return position;
		return feed.idx;
	}

	
	TextView tv;
	ImageView iv;
	FeedItem feed;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.feed_filter_row, parent, false);
		}
		feed=null;
		int count=0;
		for(int i=0; i<main.getFeedCount(); i++){
			feed=main.getFeedByPos(i);
			if(feed.filter.isFilterEnabled()){
				if(count==position){
					break;
				}
				count++;
			}
		}
		String tmp="";
		tv=(TextView)convertView.findViewById(R.id.filter_list_row_nick);
		iv=(ImageView)convertView.findViewById(R.id.filter_list_row_include_icon);
		tv.setText(feed.nick);
		tv=(TextView)convertView.findViewById(R.id.filter_list_row_include);
		tmp=feed.filter.getFilterStr(0).replaceAll("\n", " ");
		if(tmp.length()!=0){
			tv.setText(feed.filter.getFilterStr(0).replaceAll("\n", " "));
			tv.setVisibility(View.VISIBLE);
			iv.setVisibility(View.VISIBLE);
		}else{
			tv.setVisibility(View.GONE);
			iv.setVisibility(View.GONE);
		}
		
		
		tv=(TextView)convertView.findViewById(R.id.filter_list_row_exclude);
		ImageView iv=(ImageView)convertView.findViewById(R.id.filter_list_row_exclude_icon);
		tmp=feed.filter.getFilterStr(1).replaceAll("\n", " ");
		if(tmp.length()!=0){
			tv.setText(feed.filter.getFilterStr(1).replaceAll("\n", " "));
			tv.setVisibility(View.VISIBLE);
			iv.setVisibility(View.VISIBLE);
		}else{
			tv.setVisibility(View.GONE);
			iv.setVisibility(View.GONE);
		}
		convertView.setTag(feed.idx);
		
		return convertView;
	}

}
