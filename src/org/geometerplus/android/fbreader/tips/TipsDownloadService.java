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

import java.io.File;

import org.geometerplus.fbreader.Paths;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;
import org.geometerplus.zlibrary.core.network.ZLNetworkManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TipsDownloadService extends Service  {

	private static final String TIPS_URL = "http://data.fbreader.org/tips/tips.xml"; // FIXME
	private static String TIPS_PATH;
	
	@Override
	public void onCreate() {
		super.onCreate();
		ZLFile.createFileByPath(Paths.networkCacheDirectory()+"/tips").getPhysicalFile().mkdir();
		TIPS_PATH = Paths.networkCacheDirectory()+"/tips/tips.xml";
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		ZLFile tipsFile = ZLFile.createFileByPath(TIPS_PATH);
		if (!tipsFile.exists()){
			Log.v(TipsKeys.TIPS_LOG, "TipsDownloadService - !tipsFile.exists()" );

			Runnable r = new Runnable() {
				@Override
				public void run() {
					boolean isContinue = true;
					while (isContinue){
						if (TipsUtil.isOnline(TipsDownloadService.this)){
							downloadTips();
							isContinue = false;
						} else {
							long delay = 5 * 60 * 1000; 	// 5 min
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
								Log.v(TipsKeys.TIPS_LOG, "TipsDownloadService exception :" + e.getMessage());
								isContinue = false;
							}
						}
					}
				}
			};
			
			new Thread(r).start();
		}
	}

	private void downloadTips(){
		try {
			File outFile = new File(TIPS_PATH);
			ZLNetworkManager.Instance().downloadToFile(TIPS_URL, outFile);
			Log.v(TipsKeys.TIPS_LOG, "download done");
		} catch (ZLNetworkException e) {
			Log.v(TipsKeys.TIPS_LOG, "download exception: " + e.getMessage());
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
