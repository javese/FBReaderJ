package org.geometerplus.android.fbreader.tips;

import java.io.File;
import java.util.Date;

import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.network.opds.OPDSEntry;
import org.geometerplus.fbreader.network.opds.OPDSFeedMetadata;
import org.geometerplus.fbreader.network.opds.OPDSFeedReader;
import org.geometerplus.fbreader.network.opds.OPDSXMLReader;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;
import org.geometerplus.zlibrary.core.network.ZLNetworkManager;
import org.geometerplus.zlibrary.core.options.ZLBooleanOption;
import org.geometerplus.zlibrary.core.options.ZLIntegerOption;
import org.geometerplus.zlibrary.core.options.ZLStringOption;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TipsService extends Service {
	public static final String TIPS_LOG = "tips";
	public static final String TIPS_STATE_KEY = "tips_state_key";
	
	private static final String TIPS_URL = "http://data.fbreader.org/tips/tips.xml"; // FIXME
	private static String TIPS_PATH;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TIPS_LOG, "TipsService - onCreate");
		ZLFile.createFileByPath(Paths.networkCacheDirectory()+"/tips").getPhysicalFile().mkdir();

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.v(TIPS_LOG, "TipsService - onStart");
		
		TIPS_PATH = Paths.networkCacheDirectory()+"/tips/tips0001.xml";	

//		 TODO DownloadTipsService
//		try {
//			File outFile = new File(TIPS_PATH);
//			ZLNetworkManager.Instance().downloadToFile(TIPS_URL, outFile);
//			Log.v(TIPS_LOG, "download done");
//		} catch (ZLNetworkException e) {
//			Log.v(TIPS_LOG, "exception: " + e.getMessage());
//		}

		
		boolean isShowTips = new ZLBooleanOption(TipsKeys.OPTION_GROUP, TipsKeys.SHOW_TIPS, true).getValue();
		if (isShowTips){
			int currDate = new Date().getDate();
			int lastDate = new ZLIntegerOption(TipsKeys.OPTION_GROUP, TipsKeys.LAST_TIP_DATE, currDate).getValue();

			// FIXME later
			if (lastDate <= currDate){
				startParser();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(TIPS_LOG, "TipsService - onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TIPS_LOG, "TipsService - onBind");
		return null;
	}

	//TODO
	static String defaultId = "fbreader-ru-hint-0000";
	private void startParser(){
		ZLStringOption fileOpt = new ZLStringOption(TipsKeys.OPTION_GROUP, TipsKeys.CURR_TIP_FILE, TIPS_PATH);
		ZLStringOption idOpt = new ZLStringOption(TipsKeys.OPTION_GROUP, TipsKeys.CURR_TIP_ID, defaultId);
		String currFile = fileOpt.getValue();
		String currId = idOpt.getValue();

		Log.v(TIPS_LOG, currFile + "  " + currId);
		if (getIntId(currId) >= 2){
			ZLFile.createFileByPath(currFile).getPhysicalFile().delete(); // attention
			
			String nextFile = netxFile(currFile);
			Log.v(TIPS_LOG, "nextFile: " + nextFile);
			
			if (ZLFile.createFileByPath(nextFile).exists()){
				currFile = nextFile;
				currId = defaultId;
				fileOpt.setValue(currFile);				
			} else {
				return;
			}
		}

		// run parser
		ZLFile file = ZLFile.createFileByPath(currFile);
		new OPDSXMLReader(new TipsODPSFeedReader(nextId(currId))).read(file);
		idOpt.setValue(nextId(currId));
	}

	private class TipsODPSFeedReader implements OPDSFeedReader{
		String myTipId;
		TipsODPSFeedReader(String tipId){
			myTipId = tipId;
		}

		@Override
		public boolean processFeedEntry(OPDSEntry entry) {
			Tip tip = new Tip(entry);
			if (tip.getId().equals(myTipId)){
				State.putToState(TIPS_STATE_KEY, tip);
				final FBReaderApp fbReader = (FBReaderApp)FBReaderApp.Instance();
				fbReader.doAction(ActionCode.SHOW_TIP);
				return true;
			}
			return false;
		}

		@Override
		public void processFeedStart() {
		}
		
		@Override
		public void processFeedEnd() {
		}

		@Override
		public boolean processFeedMetadata(OPDSFeedMetadata feed, boolean beforeEntries) {
			return false;
		}
		
	}
	
	public class Tip {
		private OPDSEntry myEntry;
		
		Tip(OPDSEntry entry){
			myEntry = entry;
		}

		public String getId(){
			return myEntry.Id.Uri;
		}

		public String getTitle(){
			return myEntry.Title;
		}

		public String getSummary(){
			return myEntry.Summary;
		}
	}
	
	// id format example - "fbreader-ru-hint-0001"
	private static String nextId(String id){
		int val = Integer.parseInt(id.substring(id.length() - 4));
		val++;
		String end = Integer.toString(val);
		return id.substring(0, id.length() - end.length()) + end;
	}

	// id format example - "tips-0001.xml"
	private static String netxFile(String file){
		String nextFile = file.substring(0, file.length() - 4); 
		return nextId(nextFile) + ".xml";
	}
	
	private static int getIntId(String id){
		int result = Integer.parseInt(id.substring(id.length() - 4));
		return result;
	}
	
}
