package com.atcle.log;

import android.util.Log;
/**
 * @author atcle
 * simple logger for android
 * 2011.11.3
 */
public class MyLog {
	public static final int NO_LOG=Log.ERROR+1;
	public static final int ALL_LOG=Log.VERBOSE;
	
	/**
	 * 로그레벨이 높을수록 중요한 로그만 출력
	 */
//	public static int logLevel=ALL_LOG;
	public static int logLevel=NO_LOG;
//	public static int logLevel=Log.INFO;
	
	public static int inferLevel=NO_LOG;
//	public static int inferLevel=Log.INFO;
	
	public static boolean bV=logLevel<=Log.VERBOSE;
	public static boolean bD=logLevel<=Log.DEBUG;
	public static boolean bI=logLevel<=Log.INFO;
	public static boolean bW=logLevel<=Log.WARN;
	public static boolean bE=logLevel<=Log.ERROR;
	
	public static void setLogLevel(int level){
		logLevel=level;
		bV=logLevel<=Log.VERBOSE;
		bD=logLevel<=Log.DEBUG;
		bI=logLevel<=Log.INFO;
		bW=logLevel<=Log.WARN;
		bE=logLevel<=Log.ERROR;
	}
	
	public static void v(String tag, String msg){
		if(logLevel<=Log.VERBOSE){
			if(inferLevel<=Log.VERBOSE){
				inferCaller();
				Log.v(tag,inferCallInfoStr);
			}
			Log.v(tag, msg);
		}
	}
	public static void d(String tag, String msg){
		if(logLevel<=Log.DEBUG){
			if(inferLevel<=Log.DEBUG){
				inferCaller();
				Log.d(tag,inferCallInfoStr);
			}
			Log.d(tag, msg);
		}
	}
	public static void i(String tag, String msg){
		if(logLevel<=Log.INFO){
			if(inferLevel<=Log.INFO){
				inferCaller();
				Log.i(tag,inferCallInfoStr);
			}
			Log.i(tag, msg);
		}
	}
	public static void w(String tag, String msg){
		if(logLevel<=Log.WARN){
			if(inferLevel<=Log.WARN){
				inferCaller();
				Log.w(tag,inferCallInfoStr);
			}
			Log.w(tag, msg);
		}
	}
	public static void e(String tag, String msg){
		if(logLevel<=Log.ERROR){
			if(inferLevel<=Log.ERROR){
				inferCaller();
				Log.e(tag,inferCallInfoStr);
			}
			Log.e(tag, msg);
		}
	}
	
	private static String sourceClassName;
	private static String SourceMethodName;
	private static int SourceLineNumber;
	
	private static String inferCallInfoStr;
    
	private static void inferCaller()
    {
        // Get the stack trace.
        StackTraceElement stack[] = (new Throwable()).getStackTrace();

        //the line number that the caller made the call from
        int lineNumber = -1;

        // First, search back to a method in the SIP Communicator Logger class.
        int ix = 0;
        while (ix < stack.length)
        {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (cname.equals("com.atcle.log.MyLog"))
            {
                break;
            }
            ix++;
        }
        // Now search for the first frame before the SIP Communicator Logger class.
        while (ix < stack.length)
        {
            StackTraceElement frame = stack[ix];
            lineNumber=stack[ix].getLineNumber();
            String cname = frame.getClassName();
            if (!cname.equals("com.atcle.log.MyLog"))
            {
                // We've found the relevant frame.
               // record.setSourceClassName(cname);
               // record.setSourceMethodName(frame.getMethodName());
                sourceClassName=cname;
                SourceMethodName=frame.getMethodName();
                break;
            }
            ix++;
        }
        SourceLineNumber=lineNumber;
        inferCallInfoStr=sourceClassName+" "+SourceMethodName+" "+SourceLineNumber+"\n";
        return;
    }
}
