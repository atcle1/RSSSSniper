package com.atcle.rsssniper.rss;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.DataFormatException;

import com.atcle.log.MyLog;

public class RSSItem {
	public String title="";
	public String link="";
	public String description="내용이 없습니다.";
	public String enclosure;
	public String pubDate;

	//public HashMap<String, String> elements=new HashMap<String, String>(3);
	@Override
	public String toString(){
		return title+"/"+link;
	}

	public Date getPubDate(){
		Date temp=null;
		if(pubDate==null) return null;
		try {
			temp=new Date(Date.parse(pubDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}
	public String getPubDateStr(){
		SimpleDateFormat sdf=new SimpleDateFormat();
		Date date=getPubDate();
		if(date!=null){
			
		}
		return "";
	}
	
	public boolean CheckFilter(FeedFilter filter){
		Iterator<String> itr;
		boolean bContain=false;
		if(filter.includeWords.size()>0){
				itr=filter.includeWords.iterator();
				while(itr.hasNext()){
					String match=itr.next();
					if(title.contains(match)){
						bContain=true;
						break;
					}
				}
				if(bContain==false){
					return false;
				}
		}
		
		if(filter.excludeWords.size()>0){
			itr=filter.excludeWords.iterator();
			while(itr.hasNext()){
				String match=itr.next();
				if(title.contains(match)){
					return false;	//불포함 단어 포함
				}
			}
			return true;	//불포함단어 단어가 없음
		}else{
			return true;	//불포함필터없음
		}
	}
}
