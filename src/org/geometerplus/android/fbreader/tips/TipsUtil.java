package org.geometerplus.android.fbreader.tips;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TipsUtil {

	// id format example - "fbreader-ru-hint-0001"
	public static String nextId(String id){
		int val = Integer.parseInt(id.substring(id.length() - 4));
		val++;
		String end = Integer.toString(val);
		return id.substring(0, id.length() - end.length()) + end;
	}

	// id format example - "tips-0001.xml"
	public static String netxFile(String file){
		String nextFile = file.substring(0, file.length() - 4); 
		return nextId(nextFile) + ".xml";
	}
	
	public static int getIntId(String id){
		int result = Integer.parseInt(id.substring(id.length() - 4));
		return result;
	}
	
	public static boolean isOnline(Context c) {
		ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		if (nInfo != null && nInfo.isConnected()) {
		return true;
		}
		else {
		return false;
		}
	}

}
