package czd.lib.view.dropdown;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import czd.lib.R;

public class DropdownContainer extends PopupWindow {
	public DropdownContainer(Context context) {
		super(context);
	}

	public DropdownContainer(Context context, int layout_id, Drawable background, DropdownAdapter adapter, OnItemClickListener listener) throws Exception {
		super(context);
		int m = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		AbsListView content = (AbsListView)LayoutInflater.from(context).inflate(layout_id, null);
		if (content instanceof ListView)
		{
			((ListView)content).setAdapter(adapter);
		}
		else if (content instanceof GridView)
		{
			((GridView)content).setAdapter(adapter);
		}
		else
		{
			throw new Exception("not an AbsListView");
		}
		content.setOnItemClickListener(listener);
		content.measure(m, m);
		if (adapter.getRows() > adapter.getWrapRow())
		{
			init(content, LayoutParams.WRAP_CONTENT, content.getMeasuredHeight() * adapter.getWrapRow(), R.style.drop_down_anim, background);
		}
		else
		{
			init(content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.style.drop_down_anim, background);
		}
	}

	@SuppressWarnings("deprecation")
	private void init(final View view, int width, int height, int anim, Drawable background) {
		setContentView(view);
		if (anim != 0)
		{
			setAnimationStyle(anim);
		}
		if (background == null)
		{
			setBackgroundDrawable(new BitmapDrawable());
		}
		else
		{
			setBackgroundDrawable(background);
		}
		setWidth(width);
		setHeight(height);
		setInputMethodMode(INPUT_METHOD_NOT_NEEDED);
		setOutsideTouchable(true);
		setTouchable(true);
		setFocusable(true);
	}

}
