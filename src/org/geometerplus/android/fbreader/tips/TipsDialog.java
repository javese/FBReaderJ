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

import org.geometerplus.android.fbreader.tips.TipsHelper.Tip;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class TipsDialog {
	
	private AlertDialog myDialog;

	public TipsDialog(Activity activity, Tip tip){
		this(activity, tip.getTitle(), tip.getSummary());
	}
	
	public TipsDialog(final Activity activity, String title, String mess) {
		final ZLResource dialogResource = ZLResource.resource("dialog");
		
		final View view = activity.getLayoutInflater().inflate(R.layout.plugin_dialog, null, false);
		((TextView)view.findViewById(R.id.plugin_dialog_text)).setText(mess);
		final CheckBox checkBox = (CheckBox)view.findViewById(R.id.plugin_dialog_checkbox);
		checkBox.setText(dialogResource.getResource("tips").getResource("dontShowAgain").getValue());
		
		myDialog = new AlertDialog.Builder(activity)
			.setTitle(title)
			.setView(view)
			.setIcon(0)
			.setPositiveButton(
				dialogResource.getResource("button").getResource("ok").getValue(),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(checkBox.isChecked()){
							donShowAction();
						}
					}
				}
			)
			.create();
	}

	private void donShowAction(){
		// TODO
	}
	
	public void show(){
		myDialog.show();
	}
	
}



