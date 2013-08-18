package czd.lib.view.scrollview.pull;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.*;
import czd.lib.R;
import czd.lib.view.ViewUtil;

import java.lang.ref.WeakReference;

public class PullScrollView extends ScrollView {
	private static final int DEFAULT_ADVANCE_GAP = 150;
	private int height = 0;
	private int lastY = 0;
	private int t = 0;
	private boolean onTop = true, onBottom = true, onStop = true;
	private int advance_gap = DEFAULT_ADVANCE_GAP;
	private int tall = 0;

	private static final int RATIO = 3;

	private PullScrollListener listener = null;
	private ScrollHandler handler = new ScrollHandler(this);

	private static final int STATE_RELEASETOREFRESH = 0;
	private static final int STATE_PULLTOREFRESH = 1;
	private static final int STATE_REFRESHING = 2;
	private static final int STATE_DONE = 3;

	private Context _context;
	private LinearLayout header;

	private String _headerStatePull;
	private String _headerStateRelease;
	private String _headerStateLoading;
	private String _headerStateDone;
	private Drawable _headerBackground;
	private int _headerTextColor;
	private int _headerInfoVisible;

	private ImageView header_iv;
	private ProgressBar header_pb;
	private TextView header_stv;
	private TextView header_itv;

	private Animation anim_up;
	private Animation anim_down;

	private int state = STATE_DONE;

	private boolean refreshable = false;
	private boolean pulling = true;
	private int startY = 0;
	private boolean isback = false;

	private int m = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	private int header_height = 0;

	private LinearLayout top_container;
	private LinearLayout container;

	public PullScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public PullScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public PullScrollView(Context context) {
		super(context);
		init(context, null);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context, AttributeSet attrs) {
		this._context = context;
		if (attrs != null) {
			TypedArray tattrs = this._context.obtainStyledAttributes(attrs, R.styleable.PullView);
			this._headerStatePull = tattrs.getString(0);
			this._headerStateRelease = tattrs.getString(1);
			this._headerStateLoading = tattrs.getString(2);
			this._headerStateDone = tattrs.getString(3);
			this._headerBackground = tattrs.getDrawable(4);
			this._headerTextColor = tattrs.getInt(5, android.R.color.black);
			this._headerInfoVisible = tattrs.getInt(6, View.VISIBLE);
			tattrs.recycle();
		}
		this.header = (LinearLayout) ViewUtil.viewById(this._context, R.layout.common_pull_header);
		this.header_iv = (ImageView) this.header.findViewById(R.id.common_pull_header_image);
		this.header_pb = (ProgressBar) this.header.findViewById(R.id.common_pull_header_progress);
		this.header_stv = (TextView) this.header.findViewById(R.id.common_pull_header_state);
		this.header_itv = (TextView) this.header.findViewById(R.id.common_pull_header_info);
		this.header.setBackgroundDrawable(this._headerBackground);
		this.header_iv.setVisibility(View.VISIBLE);
		this.header_pb.setVisibility(View.VISIBLE);
		this.header_stv.setTextColor(_headerTextColor);
		this.header_itv.setTextColor(_headerTextColor);
		this.header_itv.setVisibility(_headerInfoVisible);
		header.measure(m, m);
		header_height = header.getMeasuredHeight();

		this.header.setPadding(0, -1 * header_height, 0, 0);
		header.invalidate();

		top_container = new LinearLayout(_context);
		top_container.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		top_container.setOrientation(LinearLayout.VERTICAL);
		top_container.setGravity(Gravity.CENTER_HORIZONTAL);
		this.addView(top_container);
		top_container.addView(this.header);

		container = new LinearLayout(context);
		container.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		container.setOrientation(LinearLayout.VERTICAL);
		container.setGravity(Gravity.CENTER_HORIZONTAL);
		top_container.addView(container);

		this.anim_up = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		this.anim_up.setInterpolator(new LinearInterpolator());
		this.anim_up.setDuration(250);
		this.anim_up.setFillAfter(true);

		this.anim_down = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		this.anim_down.setInterpolator(new LinearInterpolator());
		this.anim_down.setDuration(200);
		this.anim_down.setFillAfter(true);
	}

	@Override
	public final boolean onInterceptTouchEvent(MotionEvent event) {
		if (refreshable) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (state != STATE_REFRESHING && t == 0) {
					startY = (int) event.getY();
				}
			}
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (refreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (startY != 0) {
					if (state != STATE_REFRESHING) {
						switch (state) {
						case STATE_PULLTOREFRESH:
							state = STATE_DONE;
							resetHeader();
							break;
						case STATE_RELEASETOREFRESH:
							state = STATE_REFRESHING;
							resetHeader();
							onRefresh();
							break;
						default:
							break;
						}
					}
					isback = false;
					startY = 0;
				}
				pulling = false;
				break;
			case MotionEvent.ACTION_MOVE:
				onStop = false;
				if (state != STATE_REFRESHING && t == 0 && startY > 0) {
					int tmpY = (int) event.getY();
					switch (state) {
					case STATE_RELEASETOREFRESH:
						pulling = true;
						scrollTo(0, 0);
						if (tmpY < startY) {
							state = STATE_DONE;
							resetHeader();
						}
						else if ((tmpY - startY) / RATIO < header_height) {
							state = STATE_PULLTOREFRESH;
							resetHeader();
						}
						break;
					case STATE_PULLTOREFRESH:
						pulling = true;
						scrollTo(0, 0);
						if (tmpY <= startY) {
							state = STATE_DONE;
							resetHeader();
						}
						else if ((tmpY - startY) / RATIO >= header_height) {
							state = STATE_RELEASETOREFRESH;
							isback = true;
							resetHeader();
						}
						break;
					case STATE_DONE:
						if (tmpY >= startY) {
							state = STATE_PULLTOREFRESH;
							resetHeader();
							pulling = true;
						}
						else{
							startY=0;
						}
					default:
						break;
					}
					if (pulling && (state == STATE_PULLTOREFRESH || state == STATE_RELEASETOREFRESH)) {
						header.setPadding(0, (tmpY - startY) / RATIO - header_height, 0, 0);
					}
				}
				break;
			default:
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	public void onRefreshComplete() {
		state = STATE_DONE;
		resetHeader();
		invalidate();
		scrollTo(0, 0);
	}

	private void onRefresh() {
		if (listener != null) {
			listener.onRefresh();
		}
	}

	public void setInfo(String text) {
		this.header_itv.setText(text);
		if (this.header_itv.getVisibility() != View.VISIBLE) {
			this.header_itv.setVisibility(View.VISIBLE);
		}
	}

	public void setInfoVisible(int visible) {
		this.header_itv.setVisibility(visible);
	}

	private void resetHeader() {
		switch (state) {
		case STATE_DONE:
			this.header_stv.setText(_headerStateDone);
			this.header.setPadding(0, -1 * header_height, 0, 0);
			this.header_pb.setVisibility(View.GONE);
			this.header_iv.clearAnimation();
			this.header_iv.setVisibility(View.VISIBLE);
			break;
		case STATE_PULLTOREFRESH:
			this.header_stv.setText(_headerStatePull);
			this.header_pb.setVisibility(View.GONE);
			this.header_iv.setVisibility(View.VISIBLE);
			if (isback) {
				isback = false;
				this.header_iv.clearAnimation();
				this.header_iv.startAnimation(anim_down);
			}
			break;
		case STATE_RELEASETOREFRESH:
			this.header_stv.setText(_headerStateRelease);
			this.header_pb.setVisibility(View.GONE);
			this.header_iv.setVisibility(View.VISIBLE);

			this.header_iv.clearAnimation();
			this.header_iv.startAnimation(anim_up);
			break;
		case STATE_REFRESHING:
			this.header.setPadding(0, 0, 0, 0);
			this.header_stv.setText(_headerStateLoading);
			this.header_iv.clearAnimation();
			this.header_iv.setVisibility(View.GONE);
			this.header_pb.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		if (this.container == null) {
			super.addView(child, index, params);
		}
		else {
			this.container.addView(child, index, params);
		}
	}

	@Override
	public void addView(View child, int index) {
		if (this.container == null) {
			super.addView(child, index);
		}
		else {
			this.container.addView(child, index);
		}
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		if (this.container == null) {
			super.addView(child, params);
		}
		else {
			this.container.addView(child, params);
		}
	}

	@Override
	public void addView(View child) {
		if (this.container == null) {
			super.addView(child);
		}
		else {
			this.container.addView(child);
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		this.t = t;
		if (pulling) {
			scrollTo(0, 0);
		}
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

	public void setScrollPullListener(PullScrollListener listener) {
		this.listener = listener;
		this.refreshable = true;
	}

	private static class ScrollHandler extends Handler {
		WeakReference<PullScrollView> scroll_weak;

		public ScrollHandler(PullScrollView scroll) {
			this.scroll_weak = new WeakReference<PullScrollView>(scroll);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			PullScrollView scroll = scroll_weak.get();
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
