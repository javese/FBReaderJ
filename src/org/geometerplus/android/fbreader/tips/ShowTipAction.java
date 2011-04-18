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

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.tips.TipsHelper.Tip;
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
