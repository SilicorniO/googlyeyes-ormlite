package com.silicornio.geormlite.general;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GEL {

	//TYPES OF LOGS
	private static final int TYPE_D = 0;
	private static final int TYPE_I = 1;
	private static final int TYPE_W = 2;
	private static final int TYPE_E = 3;

	/** Flag to show or not DATE AND TIME **/
	private static final boolean DATETIME_SHOW = false; //change as you prefer

	/** Format of the date **/
	private static final String DATETIME_FORMAT = "HH:mm:ss.SSS";

	/** Prefix to use for logs **/
	private static final String PREFIX_LOG = "VITAL";

	/** Flag to show logs or not. Default to false. Set true from code using compiler constants **/
	public static boolean showLogs = false;

	/** SimpleDateFormat to format date **/
	private static SimpleDateFormat simpleDateFormat;

	/**
	 * Show log of text received, showing the class and the method
	 * @param text String to show
	 */
	public static void d(String text){
		log(text, TYPE_D);
	}

	/**
	 * Show log of text received, showing the class and the method
	 * @param text String to show
	 */
	public static void i(String text){
		log(text, TYPE_I);
	}

	/**
	 * Show log of text received, showing the class and the method
	 * @param text String to show
	 */
	public static void w(String text){
		log(text, TYPE_W);
	}


	/**
	 * Show log of text received, showing the class and the method
	 * @param text String to show
	 */
	public static void e(String text){
		log(text, TYPE_E);
	}


	/**
	 * Add a log with the text received. It shows class, method and time
	 * @param text String log to show
	 */
	private static void log(String text, int type){

		if (showLogs) {
			String sClass = "";
			String sMethod = "";
			String sLine = "";

			//get the class and the method names
			StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
			if(stackTraceElements.length>2){
				sClass = stackTraceElements[2].getClassName();
				int pos = sClass.lastIndexOf(".");
				if(pos!=-1){
					sClass = sClass.substring(pos+1);
				}
				sMethod = stackTraceElements[2].getMethodName();
				sLine = String.valueOf(stackTraceElements[2].getLineNumber());
			}

			//call to old log
			log(sClass, sLine, sMethod, text, type);
		}
	}

	/**
	 * Write a log if SHOW_LOGS is activated in Constants
	 * @param sClass String Name of the class
	 * @param sLine String Line of the class
	 * @param sMethod String Name of the method
	 * @param text String Text to write
	 * @param type int Type of log
	 */
	private static void log(String sClass, String sLine, String sMethod, String text, int type) {
		if (showLogs) {
			try {
				String str = getTextLog(sClass, sLine, sMethod, text);

				switch(type){
					case TYPE_D: Log.d(PREFIX_LOG, str); break;
					case TYPE_I: Log.i(PREFIX_LOG, str); break;
					case TYPE_W: Log.w(PREFIX_LOG, str); break;
					case TYPE_E: Log.e(PREFIX_LOG, str); break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get the text to write in the log
	 * @param sClass String Name of the class
	 * @param sLine String Line of the class
	 * @param sMethod String Name of the method
	 * @param text String Text to write in the log
	 * @return String Text with the time, class, method and text
	 */
	private static String getTextLog(String sClass, String sLine, String sMethod, String text){
		try {

			//create simpleDateFormat if not already created
			if(simpleDateFormat==null){
				simpleDateFormat = new SimpleDateFormat(DATETIME_FORMAT);
			}

			//generate the log
			String sLog = "(" + sClass + ".java:" + sLine + ") [" + sMethod + "]: " + text + "\n";

			if(DATETIME_SHOW){
				//generate the date and return the text
				return "[" + simpleDateFormat.format(new Date()) + "] " + sLog;
			}else{
				return sLog;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}

}
