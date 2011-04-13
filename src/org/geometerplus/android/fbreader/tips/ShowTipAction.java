package org.geometerplus.android.fbreader.tips;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.fbreader.fbreader.FBAction;
import org.geometerplus.fbreader.fbreader.FBReaderApp;

import android.util.Log;

public class ShowTipAction extends FBAction {
	private final FBReader myBaseActivity;

	public ShowTipAction(FBReader activity, FBReaderApp fbreader){
		super(fbreader);
		myBaseActivity = activity;
	}
	
	@Override
	protected void run() {
		new TipsDialog(myBaseActivity, "1", "2").show();
		Log.v(TipsService.TIPS_LOG, "ShowTipAction");
	}

}
