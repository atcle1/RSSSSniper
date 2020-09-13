package com.atcle.rsssniper.rss;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

import com.atcle.log.MyLog;

public class FeedFilter {
	public static final int INCLUDEWORLD=0;
	public static final int EXCLUDEWORLD=1;
	LinkedHashSet<String> includeWords=new LinkedHashSet<String>(10);
	LinkedHashSet<String> excludeWords=new LinkedHashSet<String>(10);
	String includeStr="";
	String excludeStr="";
	public FeedFilter(){
	}
	public FeedFilter(String aStr){
		fromString(aStr);
	}
	public boolean isFilterEnabled(){
		if(includeWords.size()>0 || excludeWords.size()>0){
			return true;
		}
		return false;
	}
	
	public void setFilter(int type, String filterStr){
		String []split=filterStr.split(" ",0);
		if(split!=null){
			if(type==INCLUDEWORLD){
				includeStr=filterStr;
				includeWords.clear();
				for(String word : split){
					if(word.length()>0)
						includeWords.add(word);
				}
			}else{
				excludeStr=filterStr;
				excludeWords.clear();
				for(String word : split){
					if(word.length()>0)
						excludeWords.add(word);
				}
			}
		}
		MyLog.i("tag","filter : "+this.toString());
	}
	public String getFilterStr(int type){
		if(type==INCLUDEWORLD){
			return includeStr;
		}else{
			return excludeStr;
		}
	}

	public String toString(){
		return includeStr+"|"+excludeStr;
	}
	public void fromString(String str){
		String[] split, inSplit, exSplit;
		if(str==null || str.length()==0){
			return;
		}
		split=str.split("\\|",2);	//|·Î ³ª´®
		includeStr=split[0];
		inSplit=split[0].split("[ ,\n]");//¿£ÅÍ³ª ¶ç¾î¾²±â·Î ³ª´®
		includeWords.clear();
		for(String word:inSplit){
			if(word.length()>0)
				includeWords.add(word);
		}

		if(split.length==2){
			excludeStr=split[1];
			exSplit=split[1].split("[ ,\n]");//¿£ÅÍ³ª ¶ç¾î¾²±â·Î ³ª´®
			excludeWords.clear();
			for(String word:exSplit){
				if(word.length()>0)
					excludeWords.add(word);
			}
		}
	}
}
