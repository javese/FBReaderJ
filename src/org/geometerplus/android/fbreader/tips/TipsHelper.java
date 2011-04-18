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
import java.util.Random;

import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.network.opds.OPDSEntry;
import org.geometerplus.fbreader.network.opds.OPDSFeedMetadata;
import org.geometerplus.fbreader.network.opds.OPDSFeedReader;
import org.geometerplus.fbreader.network.opds.OPDSXMLReader;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.filesystem.ZLResourceFile;
import org.geometerplus.zlibrary.core.options.ZLBooleanOption;
import org.geometerplus.zlibrary.core.options.ZLIntegerOption;

import android.app.Activity;
import android.util.Log;

public class TipsHelper {
	private static String TIPS_PATH;
	private Activity myActivity;
	
	public TipsHelper(Activity activity){
		Log.v(TipsKeys.TIPS_LOG, "TipsHelper was created");
		myActivity = activity;
	}

	public void showTip(){
		TIPS_PATH = Paths.networkCacheDirectory()+"/tips/tips.xml";	
		boolean isShowTips = new ZLBooleanOption(TipsKeys.OPTION_GROUP, TipsKeys.SHOW_TIPS, true).getValue();
		if (isShowTips){
			int currDate = new Date().getDate();
			int lastDate = new ZLIntegerOption(TipsKeys.OPTION_GROUP, TipsKeys.LAST_TIP_DATE, currDate).getValue();

			//FIXME later (lastDate < currDate)
			if (lastDate <= currDate){
				tryShowTip();
			}
		}
	}
	
	private void tryShowTip(){
		int currId = -1;
		ZLFile tipsFile = ZLFile.createFileByPath(TIPS_PATH);
		if (tipsFile.exists()){
			ZLIntegerOption idOpt = new ZLIntegerOption(TipsKeys.OPTION_GROUP, TipsKeys.CURR_TIP_ID, 0);
			currId = idOpt.getValue();
			if (currId >= 3){
				idOpt.setValue(0);
				tipsFile.getPhysicalFile().delete();
				
				Random random = new Random();
				currId = 1 + random.nextInt(10);
				tipsFile = getDefaultTipsFile();
			} else {
				currId++;
				idOpt.setValue(currId);
			}
		} else {
			Random random = new Random();
			currId = 1 + random.nextInt(10);
			tipsFile = getDefaultTipsFile();
		}
		
		new OPDSXMLReader(new TipsODPSFeedReader(currId)).read(tipsFile);
	}
	
	private ZLFile getDefaultTipsFile(){
		return ZLResourceFile.createResourceFile("tips/tips.xml");		
	}

	private class TipsODPSFeedReader implements OPDSFeedReader{
		int myTipId;
		TipsODPSFeedReader(int tipId){
			myTipId = tipId;
		}

		int myCount = 1;
		@Override
		public boolean processFeedEntry(OPDSEntry entry) {
			if (myCount == myTipId){
				Tip tip = new Tip(entry);
//				State.putToState(TIPS_STATE_KEY, tip);
//				final FBReaderApp fbReader = (FBReaderApp)FBReaderApp.Instance();
//				fbReader.doAction(ActionCode.SHOW_TIP);
				
				new TipsDialog(myActivity, tip).show();
				
				return true;
			}
			myCount++;
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
