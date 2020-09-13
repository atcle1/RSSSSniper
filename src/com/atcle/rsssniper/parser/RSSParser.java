package com.atcle.rsssniper.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.rss.FeedItem;
import com.atcle.rsssniper.rss.RSSItem;


public class RSSParser extends DefaultHandler {
	private FeedItem parseResult;
	private RSSItem rssItem;
	XmlPullParserFactory factory;
	XmlPullParser xpp;
	public RSSParser() {
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
	
	public void parse(FeedItem parseResult,int maxCount) throws Exception{
		parseResult.setLastUpdateNow();	//이걸 해줘야 다시 업데이트시도하는 일이 없음
		String encording;
		String qName;
		
		parseResult.rssItems.clear();
		
		URL url=new URL(parseResult.RSSUrl);
		InputStream is=url.openStream();
		xpp.setInput(is,"utf-8");
		
		
		int count=0;
		
		int eventType=xpp.getEventType();
		//처음 인코딩을 확인함
		while(eventType!=XmlPullParser.END_DOCUMENT){
			if(eventType == XmlPullParser.START_TAG) { 
				qName=xpp.getName();
				if(!qName.equalsIgnoreCase("xml")){
					encording=xpp.getInputEncoding();
					if(!encording.equalsIgnoreCase("utf-8")){
						//인코딩이 utf-8이 아닌경우 다시 스트림을 바꿔서 염
						is.close();
						is=url.openStream();
						xpp.setInput(is,encording);
					}
					break;
				}
			}
			try{
			eventType=xpp.next();
			}catch(XmlPullParserException ex){
				if(!ex.getMessage().contains("PI must not start with xml")){
					throw ex;
				}
			}
		}
		
		eventType=xpp.getEventType();	//스트림 바꾼경우 다시 이벤트타입확인함
		while(eventType!=XmlPullParser.END_DOCUMENT){
			if(eventType == XmlPullParser.START_TAG) { 
				qName=xpp.getName();
				str="";
				if(qName.equals("channel")){
					bHeader=true;
				}else if(qName.equals("item")){
					bHeader=false;
					bItem=true;
					rssItem=new RSSItem();
				}else if(qName.equals("enclosure")){
					try{
					rssItem.enclosure=xpp.getAttributeValue(null, "url");
					}catch(Exception ex){}
				}else{
					nowQName=qName;
				}
		    } else if(eventType == XmlPullParser.END_TAG) {
		    	qName=xpp.getName();
				if(bHeader==true){
					if(qName.equals("title")){
						parseResult.title=str.trim();
					}else if(qName.equals("link")){
						parseResult.link=str;
					}else if(qName.equals("description")){
						parseResult.description=str;
					}else{
//						parseResult.elements.put(qName, str);
					}
				}else if(bItem==true){
					if(qName.equals("item")){
						bItem=false;
						parseResult.rssItems.add(rssItem);
						
						count++;
						if(count>=maxCount) break;
					}else if(qName.equals("title")){
						rssItem.title=HTMLEntityToString(str).trim();
					}else if(qName.equals("link")){
						rssItem.link=str;
					}else if(qName.equals("description")){
						rssItem.description=HTMLEntityToString(str).trim();

					}else if(qName.equals("pubDate")){
						rssItem.pubDate=str.trim();
					}else{
						//not need so 
						//rssItem.elements.put(qName, str);
					}
				}

		    } else if(eventType == XmlPullParser.TEXT) { 
		    	str=xpp.getText();
		    } else if(eventType==XmlPullParser.START_DOCUMENT){
		    	
		    }
			try{
			eventType=xpp.next();
			}catch(Exception ex){
				eventType=XmlPullParser.END_DOCUMENT;
			}
		}

		if(parseResult.rssItems.size()==0){
			throw new Exception("parse fail...");
		}
	}
	
	
	public FeedItem parse(String urlStr, int maxCount)throws Exception {
		parseResult=new FeedItem(urlStr,"",20);
		parseResult.RSSUrl=urlStr;
		parse(parseResult, maxCount);
		return parseResult;
	}
	
	
	private static String HTMLEntityToString(String str){
		String desc="";
		if(str!=null){
			desc=str;

			desc=desc.replaceAll("&amp;", "&");
			desc=desc.replaceAll("&lt;", "<");
			desc=desc.replaceAll("&gt;", ">");
			desc=desc.replaceAll("&lsquo;", "‘");
			desc=desc.replaceAll("&rsquo;", "’");
			desc=desc.replaceAll("&ldquo;", "“");
			desc=desc.replaceAll("&rdquo;", "”");
			desc=desc.replaceAll("&quot;", "”");
			desc=desc.replaceAll("&#39;", "'");
			desc=desc.replaceAll("&nbsp;", " ");
			
			desc=desc.replaceAll("(<[/]?[b,B][r,R][/]?>)", "\r\n");
			desc=desc.replaceAll("(<[/]?[p,P][/]?>)", "\r\n");
			desc=desc.replaceAll("(<[^>]*>)", "");
			
			
			desc=desc.replaceAll("([\r\n]+[\r\n, ]*[\r\n]+)", "\r\n\r\n");	//두번이상 개행을 두번으로 바꿈
		}
		return desc;
	}
}
