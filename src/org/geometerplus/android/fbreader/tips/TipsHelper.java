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
import org.geometerplus.zlibrary.core.options.ZLIntegerOption;

import android.app.Activity;
import android.util.Log;

public class TipsHelper {
	private static String TIPS_PATH;
	private Activity myActivity;
	
	public TipsHelper(Activity activity){
		Log.v(TipsKeys.TIPS_LOG, "TipsHelper was created");
		myActivity = activity;
		TIPS_PATH = Paths.networkCacheDirectory()+"/tips/tips.xml";	
	}

	public void showTip(){
		boolean isShowTips = TipsUtil.getShowOption().getValue();
		if (isShowTips){
			int currDate = new Date().getDate();
			ZLIntegerOption dateOpt = new ZLIntegerOption(TipsKeys.OPTION_GROUP, TipsKeys.LAST_TIP_DATE, 0);
			int lastDate = dateOpt.getValue();

			//if (lastDate != currDate) 		//uncomment later
			if (lastDate != currDate || true){ 	// for testing
				dateOpt.setValue(currDate);
				tryShowTip();
			}
		}
	}
	
	public void showTipForce(){
		boolean isShowTips = TipsUtil.getShowOption().getValue();
		if (isShowTips){
			ZLIntegerOption dateOpt = new ZLIntegerOption(TipsKeys.OPTION_GROUP, TipsKeys.LAST_TIP_DATE, 0);
			dateOpt.setValue(new Date().getDate());
			tryShowTip();
		}
	}
	
	
	private final int maxCountTips = 10;
	private void tryShowTip(){
		int currId = -1;
		ZLFile tipsFile = ZLFile.createFileByPath(TIPS_PATH);
		if (tipsFile.exists()){
			ZLIntegerOption idOpt = new ZLIntegerOption(TipsKeys.OPTION_GROUP, TipsKeys.CURR_TIP_ID, 0);
			currId = idOpt.getValue();
			if (currId >= maxCountTips){
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
	
	public class Tip implements ITip {
		private OPDSEntry myEntry;
		
		Tip(OPDSEntry entry){
			myEntry = entry;
		}

		public String getTipTitle(){
			return myEntry.Title;
		}
		
		public String getTipContext(){
			return myEntry.Content;
		}
	}
	
	public interface ITip{
		String getTipTitle();
		String getTipContext();
	}
	
}
