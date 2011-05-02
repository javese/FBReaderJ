package org.geometerplus.android.fbreader.tips;

import org.geometerplus.android.fbreader.tips.TipsHelper.ITipFeedListener;
import org.geometerplus.android.fbreader.tips.TipsHelper.Tip;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
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
			public void tipFound(Tip tip) {
				textView.setText((tip.getTipContext()));
				parseTextViewCotext(textView);
				setTitle(tip.getTipTitle());
			}
		};
		new TipsHelper(myTipFeedListener).showTip();
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	}	
	
	private void dontShowAction(){
		TipsUtil.getShowOption().setValue(false);
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
