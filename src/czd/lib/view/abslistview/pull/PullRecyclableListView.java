package czd.lib.view.abslistview.pull;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import czd.lib.R;
import czd.lib.view.ViewUtil;
import czd.lib.view.abslistview.recyclable.RecyclableListView;

public class PullRecyclableListView extends RecyclableListView implements OnScrollListener {

	private static final int STATE_RELEASETOREFRESH = 0;
	private static final int STATE_PULLTOREFRESH = 1;
	private static final int STATE_REFRESHING = 2;
	private static final int STATE_DONE = 3;

	private static final int RATIO = 3;

	private boolean onTop = true, onBottom = true;

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

	private int firstVisibleItem;
	private int state = STATE_DONE;

	private boolean refreshable = false;
	private boolean pulling = true;
	private int startY = 0;
	private boolean isback = false;
	private AbsListPullListener listener;

	private int m = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

	private int header_height = 0;

	public PullRecyclableListView(Context context) {
		super(context);
		init(context, null);
	}

	public PullRecyclableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public PullRecyclableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context, AttributeSet attrs) {
		this._context = context;
		if (attrs != null)
		{
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
		this.header = (LinearLayout)ViewUtil.viewById(this._context, R.layout.common_pull_header);
		this.header_iv = (ImageView)this.header.findViewById(R.id.common_pull_header_image);
		this.header_pb = (ProgressBar)this.header.findViewById(R.id.common_pull_header_progress);
		this.header_stv = (TextView)this.header.findViewById(R.id.common_pull_header_state);
		this.header_itv = (TextView)this.header.findViewById(R.id.common_pull_header_info);
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

		addHeaderView(this.header, null, false);
		setOnScrollListener(this);

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
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.listener.onScrollStateChanged(view, scrollState);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		if (pulling)
		{
			setSelection(0);
		}
		if (this.listener != null)
		{
			this.listener.onScrollStop(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
		whereami(firstVisibleItem, visibleItemCount, totalItemCount);
	}

	private void whereami(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem <= 2)
		{
			if (!onTop && this.listener != null)
			{
				onTop = true;
				listener.onTop(firstVisibleItem, visibleItemCount, totalItemCount);
			}
		}
		else
		{
			if (onTop && listener != null)
			{
				onTop = false;
				listener.outTop(firstVisibleItem, visibleItemCount, totalItemCount);
			}
		}
		if (totalItemCount - visibleItemCount - firstVisibleItem + 1 <= 2)
		{
			if (!onBottom && listener != null)
			{
				onBottom = true;
				listener.onBottom(firstVisibleItem, visibleItemCount, totalItemCount);
			}
		}
		else
		{
			if (onBottom && listener != null)
			{
				onBottom = false;
				listener.outBottom(firstVisibleItem, visibleItemCount, totalItemCount);
			}
		}
	}

	public void setInfo(String text) {
		this.header_itv.setText(text);
		if (this.header_itv.getVisibility() != View.VISIBLE)
		{
			this.header_itv.setVisibility(View.VISIBLE);
		}
	}

	public void setInfoVisible(int visible) {
		this.header_itv.setVisibility(visible);
	}

	public void setPullListener(AbsListPullListener listener) {
		this.listener = listener;
		this.refreshable = true;
	}

	public void onRefreshComplete() {
		state = STATE_DONE;
		resetHeader();
		invalidateViews();
		setSelection(0);
	}

	private void onRefresh() {
		if (listener != null)
		{
			listener.onRefresh();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (refreshable)
		{
			switch (ev.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					if (state != STATE_REFRESHING && firstVisibleItem == 0)
					{
						startY = (int)ev.getY();
					}
					break;
				case MotionEvent.ACTION_UP:
					if (state != STATE_REFRESHING)
					{
						switch (state)
						{
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
					pulling = false;
					break;
				case MotionEvent.ACTION_MOVE:
					if (state != STATE_REFRESHING && firstVisibleItem == 0)
					{
						int tmpY = (int)ev.getY();
						switch (state)
						{
							case STATE_RELEASETOREFRESH:
								setSelection(0);
								if (tmpY <= startY)
								{
									state = STATE_DONE;
									resetHeader();
								}
								else if ((tmpY - startY) / RATIO < header_height)
								{
									state = STATE_PULLTOREFRESH;
									resetHeader();
								}
								break;
							case STATE_PULLTOREFRESH:
								setSelection(0);
								if (tmpY <= startY)
								{
									state = STATE_DONE;
									resetHeader();
								}
								else if ((tmpY - startY) / RATIO >= header_height)
								{
									state = STATE_RELEASETOREFRESH;
									isback = true;
									resetHeader();
								}
								break;
							case STATE_DONE:
								if (tmpY > startY)
								{
									state = STATE_PULLTOREFRESH;
									resetHeader();
									pulling = true;
								}
								break;
							default:
								break;
						}
						if (state == STATE_PULLTOREFRESH)
						{
							header.setPadding(0, -1 * header_height + (tmpY - startY) / RATIO, 0, 0);
						}
						else if (state == STATE_RELEASETOREFRESH)
						{
							header.setPadding(0, (tmpY - startY) / RATIO - header_height, 0, 0);
						}
					}
					break;
			}
		}
		return super.onTouchEvent(ev);
	}

	private void resetHeader() {
		switch (state)
		{
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
				if (isback)
				{
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
}
