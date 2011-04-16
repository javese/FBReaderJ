/*
 * Copyright (C) 2009-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.tips;

import java.util.Date;

import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.network.opds.OPDSEntry;
import org.geometerplus.fbreader.network.opds.OPDSFeedMetadata;
import org.geometerplus.fbreader.network.opds.OPDSFeedReader;
import org.geometerplus.fbreader.network.opds.OPDSXMLReader;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
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

		boolean isShowTips = new ZLBooleanOption(TipsKeys.OPTION_GROUP, TipsKeys.SHOW_TIPS, true).getValue();
		if (isShowTips){
			int currDate = new Date().getDate();
			int lastDate = new ZLIntegerOption(TipsKeys.OPTION_GROUP, TipsKeys.LAST_TIP_DATE, currDate).getValue();

			// FIXME later (lastDate < currDate)
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
		if (TipsUtil.getIntId(currId) >= 2){
			ZLFile.createFileByPath(currFile).getPhysicalFile().delete(); // attention
			
			String nextFile = TipsUtil.netxFile(currFile);
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
		String nextTipId = TipsUtil.nextId(currId);
		ZLFile file = ZLFile.createFileByPath(currFile);
		new OPDSXMLReader(new TipsODPSFeedReader(nextTipId)).read(file);
		idOpt.setValue(nextTipId);
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
	
	
}
