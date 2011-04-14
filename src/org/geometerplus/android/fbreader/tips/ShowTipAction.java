package org.geometerplus.android.fbreader.tips;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.tips.TipsService.Tip;
import org.geometerplus.fbreader.fbreader.FBAction;
import org.geometerplus.fbreader.fbreader.FBReaderApp;

public class ShowTipAction extends FBAction {
	private final FBReader myBaseActivity;

	public ShowTipAction(FBReader activity, FBReaderApp fbreader){
		super(fbreader);
		myBaseActivity = activity;
	}
	
	@Override
	protected void run() {
		Object object = State.popFromState(TipsService.TIPS_STATE_KEY);
		if (object instanceof Tip){
			Tip tip = (Tip)object;
			new TipsDialog(myBaseActivity, tip).show();
		}
	}

}
