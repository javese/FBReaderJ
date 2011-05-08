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

import org.geometerplus.android.fbreader.tips.TipsHelper.Tip;
import org.geometerplus.android.fbreader.tips.TipsHelper.ITipFeedListener;
import org.geometerplus.zlibrary.core.options.ZLIntegerOption;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.util.ZLNetworkUtil;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class TipsActivity extends Activity {
	ITipFeedListener myTipFeedListener;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		ZLIntegerOption dateOpt = TipsHelper.getDateOprion();
		int lastDate = dateOpt.getValue();
		if (lastDate == new Date().getDate()) {
//		if (lastDate == new Date().getDate() && false){ 	// for testing
			finish();
			return;
		}

		setContentView(R.layout.tip_dialog);

		final ZLResource dialogResource = ZLResource.resource("dialog");
		final TextView textView = ((TextView)findViewById(R.id.plugin_dialog_text));
		final CheckBox checkBox = (CheckBox)findViewById(R.id.plugin_dialog_checkbox);
		checkBox.setText(dialogResource.getResource("tips").getResource("dontShowAgain").getValue());

		Button btnOk = (Button)findViewById(R.id.button_ok);
		btnOk.setText(dialogResource.getResource("button").getResource("ok").getValue());
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkBox.isChecked()){
					dontShowAction();
				}
				finish();
			}
		});

		Button btnNext = (Button)findViewById(R.id.button_next);
		btnNext.setText(dialogResource.getResource("button").getResource("next").getValue());
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new TipsHelper(myTipFeedListener).showTip();
			}
		});

		myTipFeedListener = new ITipFeedListener() {
			@Override
			public void tipFound(Tip tip) {
				setTitle(tip.getTipTitle());
				textView.setText(tip.getTipContent());
				textView.setMovementMethod(LinkMovementMethod.getInstance());
			}
		};
		new TipsHelper(myTipFeedListener).showTip();
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	}

	private void dontShowAction(){
		TipsHelper.getShowOption().setValue(false);
	}
}
