package czd.lib.view.progress;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import czd.lib.data.MathUtil;

public class ProgressCircle extends View {
	private String text;
	private Paint back_p, done_p, text_p;
	private RectF rectf;
	private Rect rect;
	private boolean show = false;
	private long max;
	private long progress;
	private int size = 64;

	private int back_c = Color.WHITE, done_c = Color.LTGRAY, text_c = Color.BLACK;

	public ProgressCircle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ProgressCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ProgressCircle(Context context) {
		super(context);
	}

	private void init() {
		this.back_p = new Paint();
		this.back_p.setColor(back_c);
		this.back_p.setAntiAlias(true);

		this.done_p = new Paint();
		this.done_p.setColor(done_c);
		this.done_p.setAntiAlias(true);

		this.text = "";
		this.text_p = new Paint();
		this.text_p.setColor(text_c);
		this.text_p.setAntiAlias(true);
		this.text_p.setTextSize(20);
		this.rect = new Rect();

		this.rectf = new RectF();
		this.rectf.left = (getWidth() - size) / 2;
		this.rectf.right = (getWidth() + size) / 2;
		this.rectf.top = (getHeight() - size) / 2;
		this.rectf.bottom = (getHeight() + size) / 2;
	}

	public void showText(boolean show) {
		this.show = show;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setColor(int back, int done, int text) {
		this.back_c = back;
		this.done_c = done;
		this.text_c = text;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		init();
	}

	public long getMax() {
		return this.max;
	}

	public void setProgress(long progress) {
		this.progress = progress;
		if (show) {
			setText(progress);
		}
		invalidate();
	}

	private void setText(long progress) {
		this.text = MathUtil.percent(progress, this.getMax());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (progress > 0 && progress <= max) {
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, size / 2, back_p);
			canvas.drawArc(rectf, -90, 360 * ((float) progress / (float) max), true, done_p);
			if (show) {
				this.text_p.getTextBounds(this.text, 0, this.text.length(), rect);
				int x = (getWidth() / 2) - rect.centerX();
				int y = (getHeight() / 2) - rect.centerY();
				canvas.drawText(this.text, x, y, this.text_p);
			}
		}
	}

}
