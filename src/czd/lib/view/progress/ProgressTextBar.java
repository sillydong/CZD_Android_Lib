package czd.lib.view.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import czd.lib.data.MathUtil;
import czd.lib.io.DeviceUtil;

public class ProgressTextBar extends ProgressBar{
	private String text;
	private Paint mPaint;
	private Rect rect;

	public ProgressTextBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initText();
	}

	public ProgressTextBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initText();
	}

	public ProgressTextBar(Context context) {
		super(context);
		initText();
	}
	
	private void initText(){
		this.text="";
		this.mPaint=new Paint();
		this.mPaint.setColor(Color.WHITE);
		this.mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		this.mPaint.setTextSize(10*DeviceUtil.getScreenSize().density);
		this.rect=new Rect();
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
		int x=(getWidth()/2)-rect.centerX();
		int y=(getHeight()/2)-rect.centerY();
		canvas.drawText(this.text, x, y, this.mPaint);
	}

	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);
		setText(progress);
	}

	private void setText(int progress){
		this.text=MathUtil.percent(progress, this.getMax());
	}
}
