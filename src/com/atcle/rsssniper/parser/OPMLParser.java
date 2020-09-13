package com.atcle.rsssniper.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.rss.RSSItem;

public class OPMLParser {
	private RSSItem rssItem;
	XmlPullParserFactory factory;
	XmlPullParser xpp;

	public OPMLParser() {
		try{
			factory=XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			xpp=factory.newPullParser();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	boolean bHeader, bItem;
	String nowQName;
	String str = "";

	public ArrayList<FeedItem> parse(String uri) throws Exception{
		String qName;
		ArrayList<FeedItem> result=new ArrayList<FeedItem>();

		File file=new File(uri);
		FileInputStream is=new FileInputStream(file);
		xpp.setInput(is,"utf-8");

		String nick;
		String rssUrl;

		int eventType=xpp.getEventType();

		while(eventType!=XmlPullParser.END_DOCUMENT){
			if(eventType == XmlPullParser.START_TAG) { 
				qName=xpp.getName();
				if(qName.equals("outline")){
					try{
						rssUrl=xpp.getAttributeValue(null, "xmlUrl");
						if(rssUrl!=null){
							nick=xpp.getAttributeValue(null, "title");
							FeedItem feed=new FeedItem(rssUrl, nick, 60);
							result.add(feed);
						}
					}catch(Exception ex){}
				}else{
					nowQName=qName;
				}
			} 
			eventType=xpp.next();

		}

		return result;
	}

}
