package com.atcle.rsssniper.recommend;

import java.util.ArrayList;

import com.atcle.rsssniper.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommendFeedListAdapter extends BaseAdapter {

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(items==null) return 0;
		return items.size()+1;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	private ImageView iconIv;
	private TextView titleTv;
	private TextView descTv;
	
	@Override
	public View getView(int arg0, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.recf_row, parent, false);
		}
		if(items==null) return null;
		iconIv=(ImageView)convertView.findViewById(R.id.recf_list_row_icon);
		titleTv=(TextView)convertView.findViewById(R.id.recf_list_row_title);
		descTv=(TextView)convertView.findViewById(R.id.recf_list_row_desc);
		
		if(arg0==0){
			descTv.setVisibility(View.GONE);
			iconIv.setVisibility(View.GONE);
			titleTv.setVisibility(View.VISIBLE);
			titleTv.setText("...");
		}else{
			iconIv.setVisibility(View.VISIBLE);
			titleTv.setVisibility(View.VISIBLE);
			descTv.setVisibility(View.VISIBLE);
			
			DBItem item=items.get(arg0-1);
			titleTv.setText(item.TITLE);

			String desc=item.DESCS;
			if(desc==null || desc.length()==0){
				desc=item.URL;
				descTv.setSingleLine();
			}else{
				descTv.setSingleLine(false);
			}
			if(desc!=null && desc.length()>0){
				descTv.setText(desc);
			}else{
				descTv.setVisibility(View.GONE);
			}
			
			if(item.TYPES==2){
				iconIv.setImageResource(R.drawable.recf_folder);
			}else if(item.TYPES==3){
				iconIv.setImageResource(R.drawable.recf_rss);
				descTv.setEllipsize(TextUtils.TruncateAt.MIDDLE);
				descTv.setSingleLine();
			}else if(item.TYPES==4){
				iconIv.setImageResource(R.drawable.recf_link);
			}else if(item.TYPES==5){
				titleTv.setVisibility(View.GONE);
				iconIv.setVisibility(View.GONE);
				descTv.setSingleLine(false);
			}
		}
		
		return convertView;
	}
	
	private LayoutInflater mInflater;
	private Context context;	
	private RecommendFeedMgr rfm;
	private ArrayList<DBItem> items;
	
	public RecommendFeedListAdapter(Context acontext, RecommendFeedMgr arfm){
		context=acontext;
		rfm=arfm;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public void setData(ArrayList<DBItem> aitems){
		items=aitems;
	}
}
