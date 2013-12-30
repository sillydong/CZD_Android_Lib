package czd.lib.view.progress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import czd.lib.R;

public class LoadingFooter extends LinearLayout {
	private LinearLayout loading;
	private TextView loading_text;
	private TextView done;

	public LoadingFooter(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.common_loading_text, this, true);
		loading = (LinearLayout)findViewById(R.id.common_loading_text_loading);
		loading_text = (TextView)findViewById(R.id.common_loading_text_loading_text);
		done = (TextView)findViewById(R.id.common_loading_text_done);
	}

	public void loading(String info) {
		if (done.getVisibility() == View.VISIBLE)
		{
			done.setVisibility(View.INVISIBLE);
		}
		if (info != null && info.length() > 0)
		{
			loading_text.setText(info);
			loading_text.setVisibility(View.VISIBLE);
		}
		else
		{
			loading_text.setVisibility(View.GONE);
		}
		loading.setVisibility(View.VISIBLE);
	}

	public void done() {
		loading.setVisibility(View.INVISIBLE);
	}

	public void nomore(String info) {
		if (loading.getVisibility() == View.VISIBLE)
		{
			loading.setVisibility(View.INVISIBLE);
		}
		if (info != null && info.length() > 0)
		{
			done.setText(info);
			done.setVisibility(View.VISIBLE);
		}
	}
}
