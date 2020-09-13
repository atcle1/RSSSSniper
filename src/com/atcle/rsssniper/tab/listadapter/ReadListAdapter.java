package com.atcle.rsssniper.tab.listadapter;

import java.util.Date;
import java.util.Vector;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.R;
import com.atcle.rsssniper.RSSSniperActivity;
import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.rss.RSSItem;
import com.atcle.rsssniper.tab.ReadTab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReadListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	//	private itemOnClickListener itemOnClickListener;
	private static textOnClickListener textOnClickListener;
	private static enclosureOnClickListener enclosureOnClickListener;
	private ReadTab readTab;
	private Activity context;
	
	private SharedPreferences prefs;
	
	
	public FeedItem getFeed(){
		return readTab.feed;
	}

	public ReadListAdapter(ReadTab areadTab, Activity acontext){
		readTab=areadTab;
		context=acontext;
		mInflater = (LayoutInflater)acontext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		textOnClickListener=new textOnClickListener();
		//		itemOnClickListener=new itemOnClickListener();
		enclosureOnClickListener=new enclosureOnClickListener();

	}
	@Override
	public int getCount() {
		if(readTab.feed!=null){
			return readTab.feed.rssItems.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(readTab.feed!=null){
			return readTab.feed.rssItems.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	//rssItems.get(position).CheckFilter(readTab.feed.filter);
	RSSItem item;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.feed_view_row, parent, false);
		}
		item=readTab.feed.rssItems.get(position);
		//title
		TextView tv=(TextView)convertView.findViewById(R.id.feed_view_row_title);
		//tv.setText(HTMLEntityToString(item.title));
		tv.setText(item.title);
		tv.setTextColor(Color.WHITE);
		if(readTab.feed.filter.isFilterEnabled()){
			if(item.CheckFilter(readTab.feed.filter)){
				tv.setTextColor(Color.YELLOW);
			}
		}

		LinearLayout bar=(LinearLayout)convertView.findViewById(R.id.feed_view_row_newbar);
		if(position<readTab.newCountCopy){
			bar.setBackgroundColor(Color.parseColor("#58d858"));
		}else{
			bar.setBackgroundColor(Color.BLACK);
		}

		//desc
		View desc=convertView.findViewById(R.id.feed_view_row_description);
		if(readTab.bAutoClose==false && readTab.bOpened[position]==true){
			setRSSItemText(item,desc);
		}else{
			desc.setVisibility(View.GONE);
		}
		convertView.setTag(position);	//set tag


		return convertView;
	}

	static SpannableStringBuilder sp= new SpannableStringBuilder();

	public static void setRSSItemText(RSSItem item, View view){
		TextView tv;
		//description 설정
		sp.clear();
		Date d=item.getPubDate();
		if(d!=null){
			sp.append("*PUB : "+d.toLocaleString()); 
			sp.setSpan(new ForegroundColorSpan(Color.WHITE), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			sp.append("\n");
		}else if(item.pubDate!=null){
			sp.append("*PUB : "+item.pubDate);  
			sp.setSpan(new ForegroundColorSpan(Color.WHITE), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			sp.append("\n");
		}

		
		String desc=item.description;
		if(desc==null || desc.length()==0){
			desc="내용이 없습니다.";
		}
		
		//desc=HTMLEntityToString(desc).trim();

		final SpannableStringBuilder sp2 = new SpannableStringBuilder("  "+desc);  
		sp2.setSpan(new ForegroundColorSpan(Color.parseColor("#dddddd")), 0, sp2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
		sp.append(sp2);   

		tv=(TextView)view.findViewById(R.id.feed_view_row_description_text);
		tv.setText(sp);
		
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ReadTab.fontSize);
		tv.setLineSpacing(1f, ReadTab.fontSpace);

		tv.setOnClickListener(textOnClickListener);
		tv.setOnLongClickListener(textOnClickListener);
		//description end

		Button enclosureButton=(Button)view.findViewById(R.id.feed_view_row_description_enclosure);

		if(item.enclosure!=null && item.enclosure.length()>3){
			String fileType=item.enclosure.substring(item.enclosure.length()-3, item.enclosure.length());
			if((fileType.equalsIgnoreCase("mp3")||fileType.equalsIgnoreCase("wma"))){
				enclosureButton.setText(item.enclosure);
				enclosureButton.setVisibility(View.VISIBLE);
			}else{
				enclosureButton.setVisibility(View.GONE);
			}
		}else{
			enclosureButton.setVisibility(View.GONE);
		}
		enclosureButton.setOnClickListener(enclosureOnClickListener);
		view.setVisibility(View.VISIBLE);
	}


	//	class itemOnClickListener implements OnClickListener{
	//		@Override
	//		public void onClick(View v) {
	//			int idx=(Integer)v.getTag();
	//			TextView tv=(TextView)v.findViewById(R.id.feed_view_row_description);
	//			if(tv.isShown()){
	//				tv.setVisibility(View.GONE);
	//				readTab.bOpened[idx]=false;
	//			}else{
	//				ReadListAdapter.setRSSItemText(ReadListAdapter.this.getFeed().rssItems.get(idx),tv);
	//				readTab.bOpened[idx]=true;
	//			}
	//		}
	//	}

	class enclosureOnClickListener implements OnClickListener{
		String str;
		@Override
		public void onClick(View v) {
			try{
				int idx=(Integer)(((View) v.getParent().getParent()).getTag());

				str=ReadListAdapter.this.getFeed().rssItems.get(idx).enclosure;
				Uri browserUri=Uri.parse(str);				
				Intent intent=new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(browserUri, "audio/*");
				context.startActivity(intent);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}

	}

	class textOnClickListener implements OnClickListener, OnLongClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String str=null;
			try{
				int idx=(Integer)(((View) v.getParent().getParent()).getTag());
				str=ReadListAdapter.this.getFeed().rssItems.get(idx).link;
				Uri browserUri=Uri.parse(str);
				//Intent brower=new Intent(Intent.ACTION_VIEW, browserUri);
				//context.startActivity(brower);
				if(str==null || str.length()==0){
					readTab.showURL(null);
				}else
					readTab.showURL(browserUri.toString());
			}catch(Exception ex){
				MyLog.e("tag","url : "+str);
				ex.printStackTrace();
			}
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			String str=null;
			try{
				int idx=(Integer)(((View) v.getParent().getParent()).getTag());
				str=ReadListAdapter.this.getFeed().rssItems.get(idx).link;
				Uri browserUri=Uri.parse("http://google.com/gwt/x?u="+Uri.encode(str));
				//Intent brower=new Intent(Intent.ACTION_VIEW, browserUri);
				//context.startActivity(brower);
				if(str==null || str.length()==0){
					readTab.showURL(null);
				}else
					readTab.showURL(browserUri.toString());
			}catch(Exception ex){
				MyLog.e("tag","url : "+str);
				ex.printStackTrace();
			}
			return true;		//consume
		}
	}
}
