package czd.lib.view.abslistview.recyclable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

public class RecyclableGridView extends GridView implements OnScrollListener {
	private OnScrollListener listener;
	private int recycleTop = 0, recycleBottom = 0;

	public RecyclableGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RecyclableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RecyclableGridView(Context context) {
		super(context);
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		super.setOnScrollListener(l);
		this.listener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.listener.onScrollStateChanged(view, scrollState);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		//recycle
		if (firstVisibleItem > 0)
		{
			for (int i = recycleTop; i < firstVisibleItem; i++)
			{
				RecyclableItem item = (RecyclableItem)view.getItemAtPosition(i);
				if (item != null && item.isLoaded())
				{
					item.recycle();
				}
				if (recycleTop < firstVisibleItem - 1)
				{
					recycleTop++;
				}
			}
		}
		//recycle
		if (firstVisibleItem + visibleItemCount - 1 < totalItemCount)
		{
			for (int i = recycleBottom; i > firstVisibleItem + visibleItemCount - 1; i--)
			{
				RecyclableItem item = (RecyclableItem)view.getItemAtPosition(firstVisibleItem);
				if (item != null && item.isLoaded())
				{
					item.recycle();
				}
				if (recycleBottom > firstVisibleItem + visibleItemCount - 1)
				{
					recycleBottom--;
				}
			}
		}
		//load
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount - 1; i++)
		{
			RecyclableItem item = (RecyclableItem)view.getItemAtPosition(firstVisibleItem);
			if (item != null && !item.isLoaded())
			{
				item.load();
			}
		}
	}

}
