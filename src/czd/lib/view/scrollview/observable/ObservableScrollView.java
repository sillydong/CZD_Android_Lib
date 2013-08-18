package czd.lib.view.scrollview.observable;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;

public class ObservableScrollView extends ScrollView {
	private static final int DEFAULT_ADVANCE_GAP = 150;
	private int height = 0;
	private int lastY = 0;
	private boolean onTop = true, onBottom = true, onStop = true;
	private int advance_gap = DEFAULT_ADVANCE_GAP;
	private int tall = 0;

	private ObservableScrollListener listener = null;
	private ScrollHandler handler = new ScrollHandler(this);

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ObservableScrollView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			onStop = false;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (listener != null) {
			listener.onScroll(this, l, t, oldl, oldt);
		}
		handler.sendMessageDelayed(handler.obtainMessage(0, t, oldt), 100);

		whereami(t);
	}

	private void whereami(int t) {
		if (t <= advance_gap) {
			if (!onTop && listener != null) {
				onTop = true;
				listener.onTop(t);
			}
		}
		else {
			if (onTop && listener != null) {
				onTop = false;
				listener.outTop(t);
			}
		}
		if (tall - t - height <= advance_gap) {
			if (!onBottom && listener != null) {
				onBottom = true;
				listener.onBottom(t);
			}
		}
		else {
			if (onBottom && listener != null) {
				onBottom = false;
				listener.outBottom(t);
			}
		}
	}

	public void setAdvanceGap(int gap) {
		this.advance_gap = gap;
	}

	public int getAdvanceGap() {
		return this.advance_gap;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (height == 0) {
			height = getHeight();
			advance_gap = Math.min(advance_gap, height / 5);
		}
		else {
			tall = getChildAt(0).getHeight();
			whereami(lastY);
		}
	}

	public void setScrollListener(ObservableScrollListener listener) {
		this.listener = listener;
	}

	private static class ScrollHandler extends Handler {
		WeakReference<ObservableScrollView> scroll_weak;

		public ScrollHandler(ObservableScrollView scroll) {
			this.scroll_weak = new WeakReference<ObservableScrollView>(scroll);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ObservableScrollView scroll = scroll_weak.get();
			if (scroll != null) {
				if (!scroll.onStop && msg.arg1 == scroll.lastY) {
					scroll.onStop = true;
					if (scroll.listener != null) {
						scroll.listener.onStop(msg.arg1);
					}
				}
				scroll.lastY = scroll.getScrollY();
			}
		}
	}

}
