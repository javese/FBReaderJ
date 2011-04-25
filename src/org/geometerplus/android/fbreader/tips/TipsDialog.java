/*
 * Copyright (C) 2010-2011 Geometer Plus <contact@geometerplus.com>
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


import org.geometerplus.android.fbreader.tips.TipsHelper.ITipFeedListener;
import org.geometerplus.android.fbreader.tips.TipsHelper.Tip;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class TipsDialog {
	private AlertDialog myDialog;	
	ITipFeedListener myTipFeedListener;
	
	public TipsDialog(final Activity activity){
		final View view = activity.getLayoutInflater().inflate(R.layout.tip_dialog, null, false);

		final ZLResource dialogResource = ZLResource.resource("dialog");	
		final TextView textView = ((TextView)view.findViewById(R.id.plugin_dialog_text));
		final CheckBox checkBox = (CheckBox)view.findViewById(R.id.plugin_dialog_checkbox);
		checkBox.setText(dialogResource.getResource("tips").getResource("dontShowAgain").getValue());

		Button btnOk = (Button)view.findViewById(R.id.button_ok);
		btnOk.setText(dialogResource.getResource("button").getResource("ok").getValue());
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkBox.isChecked()){
					dontShowAction();
				}
				myDialog.dismiss();
			}
		});

		Button btnNext = (Button)view.findViewById(R.id.button_next);
		btnNext.setText(dialogResource.getResource("button").getResource("next").getValue());
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new TipsHelper(myTipFeedListener).showTipForce();
			}
		});

		myDialog = new AlertDialog.Builder(activity)
			.setView(view)
			.setIcon(0)
			.create();
		
		myTipFeedListener = new ITipFeedListener() {
			@Override
			public void tipFound(Tip tip) {
				textView.setText((tip.getTipContext()));
				parseTextViewCotext(textView);
				myDialog.setTitle(tip.getTipTitle());
				myDialog.show();
			}
		};
	}
	
			
	private void dontShowAction(){
		TipsUtil.getShowOption().setValue(false);
	}
	
	public void show(){
//		myDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//	    myDialog.show();
		new TipsHelper(myTipFeedListener).showTip();
	}
	
	private void parseTextViewCotext(TextView view) {
		for (String s : view.getText().toString().split("\\s+")){
			if (isLink(s)){
				parseTextViewCotext(view, s);
			} 
		}
	}
	
	private void parseTextViewCotext(TextView textView, String url) {
		CharSequence text = textView.getText();
		int start = text.toString().indexOf(url);
		int end = start + url.length();
		if (start == -1)
			return;

		if (text instanceof Spannable) {
			((Spannable) text).setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			SpannableString s = SpannableString.valueOf(text);
			s.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			textView.setText(s);
		}

		MovementMethod m = textView.getMovementMethod();
		if ((m == null) || !(m instanceof LinkMovementMethod)) {
			textView.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}
	
	private boolean isLink(String str){
			return str.contains("://") ||  str.matches("(?s)^[a-zA-Z][a-zA-Z0-9+-.]*:.*$"); 
	}	
	
}



