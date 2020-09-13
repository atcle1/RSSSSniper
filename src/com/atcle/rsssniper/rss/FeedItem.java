package com.atcle.rsssniper.rss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.atcle.log.MyLog;

public class FeedItem {
	public static final int STATUS_NOT_OBSERVE=0;
	public static final int STATUS_OBSERVE=1;
	// essential value
	public String RSSUrl;
	public String nick;
	public int pollingInterval;

	/** constructor
	 */
	public FeedItem(String aRSSUrl, String anick, int apollingInterval){
		RSSUrl=aRSSUrl;
		nick=anick;
		pollingInterval=apollingInterval;
		filter=new FeedFilter();
		status=STATUS_NOT_OBSERVE;
	}
	public FeedItem(int aidx, String anick, String aRSSUrl, int aInterval, String afilter, int astatus){
		idx=aidx;
		nick=anick;
		RSSUrl=aRSSUrl;
		pollingInterval=aInterval;
		filter=new FeedFilter(afilter);
		status=astatus;
	}

	public FeedItem(FeedItem oldFeed) {
		RSSUrl=oldFeed.RSSUrl;
		nick=oldFeed.nick;
		pollingInterval=oldFeed.pollingInterval;
		filter=oldFeed.filter;
		idx=oldFeed.idx;
		status=oldFeed.status;
	}

	/**
	 * 다른 객체에서 파싱한 결과만 복사해옴
	 */
	public void copyFromParseElement(FeedItem parseResult){
		title=parseResult.title;
		description=parseResult.description;
		rssItems=parseResult.rssItems;
		link=parseResult.link;
//		elements=parseResult.elements;
		lastUpdateDate=parseResult.lastUpdateDate;
	}
	//RSS elements
	public String link;
	public String title="No updated";
	public String description="No updated";
//	public HashMap<String, String> elements=new HashMap<String, String>(3);
	
	public Vector<RSSItem> rssItems=new Vector<RSSItem>(20);

	//addtional info
	public static final int DISABLED=0;
	public static final int ENABLED=1;
	public int idx;
	public int status=STATUS_NOT_OBSERVE;
	public int fnewCount;	//마지막 업데이트 이후 새로운 아이템 카운트
	public int newCount;	//안읽은 전체 새 로운 아이템 카운트
	public Date lastUpdateDate;
	public FeedFilter filter;
	public boolean bUpdateReserved;

	/** 업데이트 날짜 기록
	 */
	public void setLastUpdateNow(){
		lastUpdateDate=new Date();
	}
	/**
	 * @return 다음 업데이트까지 남은 mil
	 */
//	public long getUpdateLeftMil(){
//		Date now=new Date();
//		long lefted=0;
//		if(lastUpdateDate==null && bUpdateReserved==false){
//			lefted=0;
//		}else if(lastUpdateDate==null && bUpdateReserved==true){
//			lefted=(pollingInterval*60*1000);
//		}else if(lastUpdateDate!=null && bUpdateReserved==true){
//			lefted=lastUpdateDate.getTime()+(pollingInterval*60*1000*2)-now.getTime();
//		}else if(lastUpdateDate!=null && bUpdateReserved==false){
//			lefted=lastUpdateDate.getTime()+(pollingInterval*60*1000)-now.getTime();
//		}
//		return lefted;
//	}

	public FilterResult doFilter(){
		FilterResult fr=new FilterResult();
		fr.title=title;
		fr.idx=idx;
		fr.d=new Date();
		ArrayList<RSSItem> result=new ArrayList<RSSItem>();

		int checkcount=fnewCount;	//첫 파싱된경우 fnew가 -1임
		if(checkcount==-1){
			checkcount=newCount;
		}
		for(int i=0; i<rssItems.size() && i<checkcount; i++){
			if(rssItems.get(i).CheckFilter(filter)){
				result.add(rssItems.get(i));
			}
		}
		if(result.size()>0){
			fr.titls=new String[result.size()];
			for(int i=0; i<result.size(); i++){
				fr.titls[i]=result.get(i).title;
			}
			return fr;
		}else
			return null;
	}

	@Override
	public String toString(){
		String str=link+" / "+title;
		for(int i=0; i<rssItems.size(); i++){
			str+="\n"+i+":"+rssItems.get(i).toString();
		}
		return str;
	}
}
