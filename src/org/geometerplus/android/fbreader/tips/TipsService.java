package org.geometerplus.android.fbreader.tips;

import java.io.File;

import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.network.opds.OPDSEntry;
import org.geometerplus.fbreader.network.opds.OPDSFeedMetadata;
import org.geometerplus.fbreader.network.opds.OPDSFeedReader;
import org.geometerplus.fbreader.network.opds.OPDSXMLReader;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;
import org.geometerplus.zlibrary.core.network.ZLNetworkManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TipsService extends Service {
	public static final String TIPS_LOG = "tips";
	
	private static final String TIPS_URL = "http://data.fbreader.org/tips/tips.xml"; // FIXME
	private static String TIPS_PATH;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TIPS_LOG, "TipsService - onCreate");
		TIPS_PATH = Paths.networkCacheDirectory()+"/tips1.xml";
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.v(TIPS_LOG, "TipsService - onStart");

		try {
			File outFile = new File(TIPS_PATH);
			ZLNetworkManager.Instance().downloadToFile(TIPS_URL, outFile);
			Log.v(TIPS_LOG, "download done");
		} catch (ZLNetworkException e) {
			Log.v(TIPS_LOG, "exception: " + e.getMessage());
		}
		testParser();
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

	
	
	private void testParser(){
		ZLFile file = ZLFile.createFileByPath(TIPS_PATH);
		new OPDSXMLReader(new MyODPSFeedReader()).read(file);
	}

	private class MyODPSFeedReader implements OPDSFeedReader{
		@Override
		public void processFeedStart() {
		}
		
		@Override
		public void processFeedEnd() {
		}

		@Override
		public boolean processFeedMetadata(OPDSFeedMetadata feed, boolean beforeEntries) {
			Log.v(TIPS_LOG, "processFeedMetadata >> " + feed.toString());
			return false;
		}
		
		@Override
		public boolean processFeedEntry(OPDSEntry entry) {
			Log.v(TIPS_LOG, "processFeedEntry >>" + entry.toString());
			return false;
		}
		
	}
	
}
