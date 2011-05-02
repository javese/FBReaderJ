package org.geometerplus.android.fbreader.tips;

import org.geometerplus.android.fbreader.tips.TipsHelper.ITip;
import org.geometerplus.android.fbreader.tips.TipsHelper.ITipFeedListener;
import org.geometerplus.android.fbreader.tips.TipsHelper.Tip;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.util.ZLNetworkUtil;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
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
				new TipsHelper(myTipFeedListener).showTipForce();
			}
		});
		
		myTipFeedListener = new ITipFeedListener() {
			@Override
			public void tipFound(ITip tip) {
				setTitle(tip.getTipTitle());
//				String text = "Посетите  <a href=\"http://fbreader.org\">официальный сайт</a> FBReader";
//				textView.setText(getCommentText(text));
				Log.v(TipsKeys.TIPS_LOG, "content: " + tip.getTipContext());
				textView.setText(getCommentText(tip.getTipContext()));
				textView.setMovementMethod(LinkMovementMethod.getInstance());
			}
		};
		new TipsHelper(myTipFeedListener).showTip();
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	}	
	
	private void dontShowAction(){
		TipsUtil.getShowOption().setValue(false);
	}
		
	private CharSequence getCommentText(String originalText) {
		final SpannableStringBuilder builder = new SpannableStringBuilder();
		while (true) {
			int index = originalText.indexOf("<a");
			if (index == -1) {
				builder.append(originalText);
				break;
			}
			builder.append(originalText.substring(0, index));
			originalText = originalText.substring(index);

			index = originalText.indexOf(">");
			if (index == -1) {
				break;
			}
			String hyperlinkText = originalText.substring(0, index + 1);
			final int start = hyperlinkText.indexOf("\"");
			final int end = hyperlinkText.lastIndexOf("\"");
			if (start == end) {
				break;
			}
			hyperlinkText = hyperlinkText.substring(start + 1, end);
			hyperlinkText = ZLNetworkUtil.appendParameter(hyperlinkText, null, null);

			originalText = originalText.substring(index + 1);

			index = originalText.indexOf("</a>");
			if (index == -1) {
				break;
			}
			final int len = builder.length();
			builder.append(originalText.substring(0, index));
			builder.setSpan(new URLSpan(hyperlinkText), len, len + index, 0);
			originalText = originalText.substring(index + 4);
		}
		
		return builder.subSequence(0, builder.length());
	}

}
