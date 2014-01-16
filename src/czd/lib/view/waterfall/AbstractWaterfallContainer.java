package czd.lib.view.waterfall;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class AbstractWaterfallContainer extends ScrollView {
	protected static final int DEFAULT_ADVANCE_GAP = 300;
	protected static final int DEFAULT_COLUMN_COUNT = 2;
	protected static final int DEFAULT_SAVE_GAP = 300;

	public static final int MOVE_UP = 1, MOVE_DOWN = 2;

	protected int height = 0, width = 0, column_width = 0;
	protected int lastY = 0;
	protected boolean onTop = true, onBottom = true, onStop = true;
	protected int advance_gap = DEFAULT_ADVANCE_GAP;
	protected int column_gap = 10;
	protected int column_count = DEFAULT_COLUMN_COUNT;
	protected boolean done_init = false;
	protected boolean pulling = false;

	protected LinearLayout container;
	protected View header = null, footer = null;
	protected Context context;
	protected int[] column_heights;
	protected int min_column = 0, min_height = 0, max_height = 0;
	protected ArrayList<Integer>[] item_positions;
	protected int[] display_position, recycle_position;
	protected int item_count = 0;
	protected int header_height = 0;
	protected int tall = 0;
	protected int m = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

	protected OnClickListener click_listener = null;
	protected WaterfallScrollListener scroll_listener = null;
	protected WaterfallHandler handler = null;
	protected OnInitListener init_listener = null;

	protected boolean overscroll = false;

	public AbstractWaterfallContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public AbstractWaterfallContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public AbstractWaterfallContainer(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (height == 0)
		{
			height = getHeight();
			advance_gap = Math.min(advance_gap, height / 5);
		}
		if (width == 0)
		{
			width = getWidth();
			column_width = (width - getPaddingLeft() - getPaddingRight() - column_gap * (column_count - 1)) / column_count;
		}
		if (height != 0 && width != 0)
		{
			if (!done_init || container == null)
				init();
			else if (tall != getChildAt(0).getHeight())
			{
				tall = getChildAt(0).getHeight();
				handleView(MOVE_DOWN);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE)
			onStop = false;
		return super.onTouchEvent(ev);
	}

	protected void init() {
		done_init = true;
		this.setScrollbarFadingEnabled(true);
		initScroll();
		handler = new WaterfallHandler(this);

		column_heights = new int[column_count];
		item_positions = new ArrayList[column_count];
		display_position = new int[column_count];
		recycle_position = new int[column_count];

		for (int i = 0; i < column_count; i++)
		{
			LinearLayout column = new LinearLayout(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(column_width, LayoutParams.WRAP_CONTENT);
			if (i == 0)
			{
				params.rightMargin = column_gap / 2;
				params.leftMargin = 0;
			}
			else if (i == column_count - 1)
			{
				params.leftMargin = column_gap / 2;
				params.rightMargin = 0;
			}
			else
				params.leftMargin = params.rightMargin = column_gap / 2;
			column.setLayoutParams(params);
			column.setOrientation(LinearLayout.VERTICAL);
			container.addView(column);

			item_positions[i] = new ArrayList<Integer>();
			display_position[i] = 0;
			recycle_position[i] = 0;
			column_heights[i] = header_height + getPaddingTop();
		}
		if (init_listener != null)
			init_listener.sendEmptyMessage(0);
	}

	protected void initScroll() {
		if (header == null && footer == null)
		{
			container = new LinearLayout(context);
			container.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			container.setOrientation(LinearLayout.HORIZONTAL);
			container.setGravity(Gravity.CENTER_HORIZONTAL);
			this.addView(container);
		}
		else
		{
			LinearLayout top_container = new LinearLayout(context);
			top_container.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			top_container.setOrientation(LinearLayout.VERTICAL);
			top_container.setGravity(Gravity.CENTER_HORIZONTAL);
			this.addView(top_container);
			if (header != null)
			{
				header.measure(m, m);
				top_container.addView(header);
				header_height = header.getMeasuredHeight();
			}
			container = new LinearLayout(context);
			container.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			container.setOrientation(LinearLayout.HORIZONTAL);
			container.setGravity(Gravity.CENTER_HORIZONTAL);
			top_container.addView(container);
			if (footer != null)
			{
				footer.measure(m, m);
				top_container.addView(footer);
			}
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (onStop)
			onStop = false;
		if (item_count > 0)
		{
			if (scroll_listener != null)
				scroll_listener.onScroll(this, l, t, oldl, oldt);
			handler.sendMessageDelayed(handler.obtainMessage(0, t, oldt), 150);
		}
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		if (clampedY)
			this.overscroll = true;
	}

	private void handleView(int direction) {
		if (item_count > 0 && onStop)
		{
			//handling = true;
			switch (direction)
			{
				case MOVE_DOWN:
					//下滑
					((Activity)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							for (int i = 0; i < column_count; i++)
							{
								int tmp_item_count = item_positions[i].size();
								if (tmp_item_count > 0)
								{
									//recycle
									for (int j = recycle_position[i]; j < tmp_item_count; j++)
									{
										WaterfallItem rec_item = (WaterfallItem)((LinearLayout)container.getChildAt(i)).getChildAt(j);
										if (item_positions[i].get(j) < lastY - rec_item.getItemHeight() - DEFAULT_SAVE_GAP)
										{
											rec_item.recycle();
											if (recycle_position[i] < tmp_item_count - 1)
												recycle_position[i]++;
										}
										else
											break;
									}
									//load
									for (int k = display_position[i]; k < tmp_item_count; k++)
									{
										int top_pos = item_positions[i].get(k);
										WaterfallItem item = (WaterfallItem)((LinearLayout)container.getChildAt(i)).getChildAt(k);
										int bottom_pos = top_pos + item.getItemHeight();
										if (bottom_pos < lastY - DEFAULT_SAVE_GAP)
										{
											if (display_position[i] < tmp_item_count - 1)
												display_position[i]++;
										}
										else if (top_pos > lastY + height + DEFAULT_SAVE_GAP)
											break;
										else
										{
											if (onStop)
												item.load();
											if (display_position[i] < tmp_item_count - 1)
												display_position[i]++;
										}
									}
								}
							}
							System.gc();
						}
					});
					break;
				case MOVE_UP:
					//上滑
					((Activity)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							for (int i = 0; i < column_count; i++)
							{
								if (item_positions[i].size() > 0)
								{
									//recycle
									for (int j = display_position[i]; j >= 0; j--)
									{
										WaterfallItem rec_item = (WaterfallItem)((LinearLayout)container.getChildAt(i)).getChildAt(j);
										if (item_positions[i].get(j) > lastY + height + DEFAULT_SAVE_GAP)
										{
											rec_item.recycle();
											if (display_position[i] > 0)
												display_position[i]--;
										}
										else
											break;
									}
									//load
									for (int k = recycle_position[i]; k >= 0; k--)
									{
										int top_pos = item_positions[i].get(k);
										WaterfallItem item = (WaterfallItem)((LinearLayout)container.getChildAt(i)).getChildAt(k);
										int bottom_pos = top_pos + item.getItemHeight();
										if (top_pos > lastY + height + DEFAULT_SAVE_GAP)
										{
											if (recycle_position[i] > 0)
												recycle_position[i]--;
										}
										else if (bottom_pos < lastY - DEFAULT_SAVE_GAP)
											break;
										else
										{
											if (onStop)
												item.load();
											if (recycle_position[i] > 0)
												recycle_position[i]--;
										}
									}
								}
							}
							System.gc();
						}
					});
					break;
				default:
					break;
			}

			//handling = false;
		}
	}

	protected void whereami(int t) {
		if (t <= advance_gap)
		{
			if (!onTop && scroll_listener != null)
			{
				onTop = true;
				scroll_listener.onTop(t);
			}
		}
		else
		{
			if (onTop && scroll_listener != null)
			{
				onTop = false;
				scroll_listener.outTop(t);
			}
		}
		if (tall - t - height <= advance_gap)
		{
			if (!onBottom && scroll_listener != null)
			{
				onBottom = true;
				scroll_listener.onBottom(t);
			}
		}
		else
		{
			if (onBottom && scroll_listener != null)
			{
				onBottom = false;
				scroll_listener.outBottom(t);
			}
		}
	}

	public void addItem(WaterfallItem item) {
		if (!done_init || container == null)
		{
			init();
		}
		if (item != null)
		{
			if (click_listener != null)
			{
				item.setOnClickListener(click_listener);
			}
			item.setLayoutParams();
			((LinearLayout)container.getChildAt(min_column)).addView((View)item);
			requestLayout();
			item_positions[min_column].add(column_heights[min_column]);
			column_heights[min_column] = column_heights[min_column] + item.getItemHeight();
			item_count++;
			for (int i = 0; i < column_count; i++)
			{
				if (i != min_column)
				{
					min_height = column_heights[min_column];
					max_height = column_heights[i];
					if (min_height >= max_height)
					{
						min_column = i;
						max_height = min_height;
					}
				}
			}
		}
	}

	public void cleanItems() {
		for (int i = 0; i < column_count; i++)
		{
			((LinearLayout)container.getChildAt(i)).removeAllViews();
			item_positions[i].clear();
			display_position[i] = 0;
			recycle_position[i] = 0;
			column_heights[i] = 0;
		}
		min_column = 0;
		min_height = 0;
		max_height = 0;
		item_count = 0;
		lastY = 0;
		onTop = true;
		onBottom = true;
		onStop = true;
		height = 0;
		requestLayout();
	}

	public void setAdvanceGap(int gap) {
		this.advance_gap = gap;
	}

	public int getAdvanceGap() {
		return this.advance_gap;
	}

	public void setColumnCount(int count) {
		this.column_count = count;
	}

	public int getColumnCount() {
		return this.column_count;
	}

	public int getColumnWidth() {
		return column_width;
	}

	public int getColumnGap() {
		return column_gap;
	}

	public void setColumnGap(int gap) {
		this.column_gap = gap;
	}

	public int getItemCount() {
		return this.item_count;
	}

	public int getTall() {
		return this.tall;
	}

	public void setItemClickListener(OnClickListener listener) {
		this.click_listener = listener;
	}

	public void setHeader(View header) {
		this.header = header;
	}

	public View getHeader() {
		return this.header;
	}

	public void setFooter(View footer) {
		this.footer = footer;
	}

	public View getFooter() {
		return this.footer;
	}

	public void setScrollListener(WaterfallScrollListener listener) {
		this.scroll_listener = listener;
	}

	public void setOnInitListener(OnInitListener listener) {
		this.init_listener = listener;
	}

	protected static class WaterfallHandler extends Handler {
		WeakReference<AbstractWaterfallContainer> waterfall_weak;

		public WaterfallHandler(AbstractWaterfallContainer waterfall) {
			this.waterfall_weak = new WeakReference<AbstractWaterfallContainer>(waterfall);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final AbstractWaterfallContainer waterfall = waterfall_weak.get();
			if (waterfall != null && !waterfall.pulling)
			{
				//msg.arg1 -> t
				//msg.arg2 -> oldt
				if (!waterfall.onStop && msg.arg1 == waterfall.lastY)
				{
					waterfall.onStop = true;
					if (msg.arg2 <= 0)
						waterfall.handleView(MOVE_UP);
					else if (msg.arg2 + waterfall.height >= waterfall.tall)
						waterfall.handleView(MOVE_DOWN);
					else if (msg.arg1 < msg.arg2)
					{
						if (waterfall.overscroll)
							waterfall.handleView(MOVE_DOWN);
						else
							waterfall.handleView(MOVE_UP);
					}
					else if (msg.arg1 >= msg.arg2)
						waterfall.handleView(MOVE_DOWN);
					if (waterfall.scroll_listener != null)
						waterfall.scroll_listener.onStop(msg.arg1);
					waterfall.overscroll = false;
				}
				waterfall.lastY = waterfall.getScrollY();
				waterfall.whereami(msg.arg1);
			}
		}

	}

	public static class OnInitListener extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			onInit();
		}

		public void onInit() {

		}
	}
}
