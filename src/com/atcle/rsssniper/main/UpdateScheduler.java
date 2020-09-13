package com.atcle.rsssniper.main;

import java.util.ArrayList;
import java.util.Date;

import com.atcle.log.MyLog;
import com.atcle.rsssniper.rss.FeedItem;

public class UpdateScheduler {
	private static UpdateScheduler instance;
	RSSSniperMain main;
	public UpdateScheduler(RSSSniperMain amain){
		main=amain;
	}
	
	public ArrayList<Integer> getUpdateList(){
		ArrayList<Integer> result=new ArrayList<Integer>();
		Date now=new Date();
		
		for(int i=0; i<main.getFeedCount(); i++){
			FeedItem feed=main.getFeedByPos(i);
			Date lastUpdate=feed.lastUpdateDate;
			if(feed.status==FeedItem.STATUS_OBSERVE){
				if(lastUpdate==null){
					result.add(feed.idx);
				}else{
					if(lastUpdate.getTime()+feed.pollingInterval*60*1000
							<now.getTime()+30*1000){
						result.add(feed.idx);
					}
				}
			}
		}
		return result;
	}
//
//	public FeedItem nextUpdateItem(){
//		int debugReson=0;
//		int debugResonTmp=-1;
//		main=RSSSniperMain.getInstance();
//		int findItemPos=-1;
//
//		Date now=new Date();
//		FeedItem foundItem=null;
//		long shortestItemdMil=Long.MAX_VALUE;
//		long nowTime=now.getTime();
//
//		for(int i=0; i<main.getFeedCount(); i++){
//			FeedItem feed=main.getFeedByPos(i);
//			Date lastUpdate=feed.lastUpdateDate;
//			if(feed.status==FeedItem.STATUS_OBSERVE){
//				//총 4가지경우
//				/*
//				 * 업데이트된적없음, 예약안됨 => 현재시간
//				 * 업데이트된적없음, 예약됨 -> 다음업데이트 : 현재시간+pi
//				 * 업데이트된적있음, 예약됨 -> 마지막업데이트+pi+pi
//				 * 업데이트된적있음, 예약안됨->마지막업데이트+pi
//				 */
//				long nextUpdateMil=0;
//				if(lastUpdate==null && feed.bUpdateReserved==false){
//					nextUpdateMil=nowTime;
//					debugResonTmp=0;
////					return feed;
//				}else if(lastUpdate==null && feed.bUpdateReserved==true){
//					nextUpdateMil=nowTime+(feed.pollingInterval*60*1000);
//					debugResonTmp=1;
//				}else if(lastUpdate!=null && feed.bUpdateReserved==true){
//					nextUpdateMil=lastUpdate.getTime();
//					while(nextUpdateMil>nowTime){
//						nextUpdateMil+=(feed.pollingInterval*60*1000);
//					}
//					debugResonTmp=2;
//				}else if(lastUpdate!=null && feed.bUpdateReserved==false){
//					nextUpdateMil=lastUpdate.getTime()+(feed.pollingInterval*60*1000);
//					debugResonTmp=3;
//				}
//				
//				if(nextUpdateMil<=shortestItemdMil){
//					findItemPos=i;
//					shortestItemdMil=nextUpdateMil;
//					debugReson=debugResonTmp;
//				}
//			}
//		}
//		if(findItemPos!=-1){
//			MyLog.e("tag","selected reson "+debugReson);
//			foundItem=main.getFeedByPos(findItemPos);
//		}else
//			MyLog.e("tag","selected reson NOT FOUND");
//		return foundItem;
//	}
}
