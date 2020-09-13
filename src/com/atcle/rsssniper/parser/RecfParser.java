package com.atcle.rsssniper.parser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.atcle.rsssniper.recommend.DBItem;
import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.rss.RSSItem;

public class RecfParser {
	private RSSItem rssItem;
	XmlPullParserFactory factory;
	XmlPullParser xpp;
	private static final String dburl="http://atcle.hosting.paran.com/rsssniper/db.php";
	public RecfParser() {
		try{
			factory=XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			xpp=factory.newPullParser();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


	public ArrayList<DBItem> parse(String lastupdate) throws Exception{
		String qName, str;
		ArrayList<DBItem> parseResult=new ArrayList<DBItem>();
		DBItem item=null;

		String urlstr=dburl;
		if(lastupdate!=null)
			urlstr+="?lastupdate="+ lastupdate;

		URL url=new URL(urlstr);
		InputStream is=url.openStream();
		xpp.setInput(is,"utf-8");

		int eventType=xpp.getEventType();	//스트림 바꾼경우 다시 이벤트타입확인함
		while(eventType!=XmlPullParser.END_DOCUMENT){
			if(eventType == XmlPullParser.START_TAG) { 
				qName=xpp.getName();
				str="";
				if(qName.equals("item")){
					item=new DBItem();
				}else if(qName.equals("Idx")){
					str=xpp.nextText();
					item.IDX=Integer.parseInt(str);
				}else if(qName.equals("Pidx")){
					str=xpp.nextText();
					item.PIDX=Integer.parseInt(str);
				}else if(qName.equals("Types")){
					str=xpp.nextText();
					if(str!=null&& !str.equals(""))
						item.TYPES=Integer.parseInt(str);
				}else if(qName.equals("Order")){
					str=xpp.nextText();
					if(str!=null && !str.equals(""))
						item.ORDER=Integer.parseInt(str);
				}else if(qName.equals("Title")){
					str=xpp.nextText();
					item.TITLE=str;
				}else if(qName.equals("Descs")){
					str=xpp.nextText();
					item.DESCS=str;
				}else if(qName.equals("Url")){
					str=xpp.nextText();
					item.URL=str;
				}
			} else if(eventType == XmlPullParser.END_TAG) {
				qName=xpp.getName();
				if(qName.equals("item")){
					parseResult.add(item);
				}
			} 

			eventType=xpp.next();
		}


		return parseResult;
	}

}