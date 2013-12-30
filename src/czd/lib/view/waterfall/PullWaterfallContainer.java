package czd.lib.view.waterfall;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import czd.lib.R;
import czd.lib.application.ApplicationUtil;
import czd.lib.view.ViewUtil;
import czd.lib.view.progress.LoadingFooter;

public class PullWaterfallContainer extends AbstractWaterfallContainer {
	private static final int STATE_RELEASETOREFRESH = 0;
	private static final int STATE_PULLTOREFRESH = 1;
	private static final int STATE_REFRESHING = 2;
	private static final int STATE_DONE = 3;

	private static final int RATIO = 2;

	private int t = 0;
	private int pull_header_height = 0;

	private String _headerStatePull = "Pull To Refresh";
	private String _headerStateRelease = "Release To Refresh";
	private String _headerStateLoading = "Loading...";
	private String _headerStateDone = "Done Refresh";
	private Drawable _headerBackground = ApplicationUtil.r.getDrawable(R.drawable.empty);
	private int _headerTextColor = Color.BLACK;
	private int _headerInfoVisible = View.INVISIBLE;

	private ImageView header_iv;
	private ProgressBar header_pb;
	private TextView header_stv;
	private TextView header_itv;

	private Animation anim_up;
	private Animation anim_down;

	private int state = STATE_DONE;

	private boolean refreshable = false;
	private int startY = 0;
	private boolean isback = false;

	public PullWaterfallContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		preInit(context, attrs);
	}

	public PullWaterfallContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		preInit(context, attrs);
	}

	public PullWaterfallContainer(Context context) {
		super(context);
		preInit(context, null);
	}

	@Override
	public final boolean onInterceptTouchEvent(MotionEvent event) {
		if (refreshable)
		{
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				if (state != STATE_REFRESHING && t == 0)
				{
					startY = (int)event.getY();
				}
			}
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (refreshable)
		{
			switch (event.getAction())
			{
				case MotionEvent.ACTION_UP:
					if (startY != 0)
					{
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
						startY = 0;
					}
					pulling = false;
					break;
				case MotionEvent.ACTION_MOVE:
					if (state != STATE_REFRESHING && t == 0 && startY > 0)
					{
						int tmpY = (int)event.getY();
						switch (state)
						{
							case STATE_RELEASETOREFRESH:
								pulling = true;
								scrollTo(0, 0);
								if (tmpY < startY)
								{
									state = STATE_DONE;
									resetHeader();
								}
								else if ((tmpY - startY) / RATIO < pull_header_height)
								{
									state = STATE_PULLTOREFRESH;
									resetHeader();
								}
								break;
							case STATE_PULLTOREFRESH:
								pulling = true;
								scrollTo(0, 0);
								if (tmpY <= startY)
								{
									state = STATE_DONE;
									resetHeader();
								}
								else if ((tmpY - startY) / RATIO >= pull_header_height)
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
								else
								{
									startY = 0;
								}
							default:
								break;
						}
						if (pulling && (state == STATE_PULLTOREFRESH || state == STATE_RELEASETOREFRESH))
						{
							header.setPadding(0, (tmpY - startY) / RATIO - pull_header_height, 0, 0);
						}
					}
					break;
				default:
					break;
			}
		}
		return super.onTouchEvent(event);
	}

	@SuppressWarnings("deprecation")
	private void preInit(Context context, AttributeSet attrs) {
		if (attrs != null)
		{
			TypedArray tattrs = this.context.obtainStyledAttributes(attrs, R.styleable.PullView);
			this._headerStatePull = tattrs.getString(0);
			this._headerStateRelease = tattrs.getString(1);
			this._headerStateLoading = tattrs.getString(2);
			this._headerStateDone = tattrs.getString(3);
			this._headerBackground = tattrs.getDrawable(4);
			this._headerTextColor = tattrs.getInt(5, android.R.color.black);
			this._headerInfoVisible = tattrs.getInt(6, View.VISIBLE);
			tattrs.recycle();
		}
		header = (LinearLayout)ViewUtil.viewById(this.context, R.layout.common_pull_header);
		header_iv = (ImageView)this.header.findViewById(R.id.common_pull_header_image);
		header_pb = (ProgressBar)this.header.findViewById(R.id.common_pull_header_progress);
		header_stv = (TextView)this.header.findViewById(R.id.common_pull_header_state);
		header_itv = (TextView)this.header.findViewById(R.id.common_pull_header_info);
		header.setBackgroundDrawable(this._headerBackground);
		header_iv.setVisibility(View.VISIBLE);
		header_pb.setVisibility(View.VISIBLE);
		header_stv.setTextColor(_headerTextColor);
		header_itv.setTextColor(_headerTextColor);
		header_itv.setVisibility(_headerInfoVisible);

		footer = new LoadingFooter(context);

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
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		this.t = t;
		if (pulling)
		{
			scrollTo(0, 0);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public void onRefreshComplete() {
		state = STATE_DONE;
		resetHeader();
		invalidate();
		scrollTo(0, 0);
	}

	private void onRefresh() {
		if (scroll_listener != null)
		{
			scroll_listener.onRefresh();
		}
	}

	private void resetHeader() {
		switch (state)
		{
			case STATE_DONE:
				this.header_stv.setText(_headerStateDone);
				this.header.setPadding(0, -1 * pull_header_height, 0, 0);
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

	public void setHeaderInfo(String text) {
		this.header_itv.setText(text);
		if (this.header_itv.getVisibility() != View.VISIBLE)
		{
			this.header_itv.setVisibility(View.VISIBLE);
		}
	}

	public void setHeaderInfoVisible(int visible) {
		this.header_itv.setVisibility(visible);
	}

	@Override
	public void setScrollListener(WaterfallScrollListener listener) {
		super.setScrollListener(listener);
		this.refreshable = true;
	}

	@Override
	protected void initScroll() {
		super.initScroll();
		if (header != null)
		{
			pull_header_height = header_height;
			header.setPadding(0, -1 * pull_header_height, 0, 0);
			header_height = 0;
		}
	}

}
