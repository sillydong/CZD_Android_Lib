package czd.lib.view.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import czd.lib.R;

public class TabButton extends LinearLayout {

	private Context _context;
	public boolean _checked = false;
	private Drawable _drawableOff;
	private Drawable _drawableOn;
	private ImageView _iv;
	private String _text;
	private int _textOffColor;
	private int _textOnColor;
	private float _textSize;
	private TextView _tv;
	private Drawable _backgroundOff;
	private Drawable _backgroundOn;
	private int _orientation;

	public TabButton(Context context) {
		super(context);
		init(context, null);
	}

	public TabButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		this._context = context;
		if (attrs != null) {
			TypedArray tattrs = this._context.obtainStyledAttributes(attrs, R.styleable.TabButton);
			this._drawableOn = tattrs.getDrawable(0);
			this._drawableOff = tattrs.getDrawable(1);
			this._backgroundOn = tattrs.getDrawable(2);
			this._backgroundOff = tattrs.getDrawable(3);
			this._text = tattrs.getString(4);
			this._textSize = tattrs.getDimension(5, 12f);
			this._textOnColor = tattrs.getColor(6, android.R.color.black);
			this._textOffColor = tattrs.getColor(7, android.R.color.darker_gray);
			this._orientation = tattrs.getInt(8, 0);
			tattrs.recycle();
			setOrientation(this._orientation);
		}
		setGravity(Gravity.CENTER);
		this._iv = new ImageView(this._context);
		this._iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(this._iv);

		this._tv = new TextView(this._context);
		this._tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this._tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, this._textSize);
		this._tv.setSingleLine();
		this._tv.setEllipsize(TextUtils.TruncateAt.END);
		this._tv.setGravity(Gravity.CENTER);
		this._tv.setText(this._text);
		this._tv.setShadowLayer(1.0F, 1.0F, 1.0F, -1);
		this._tv.setTypeface(null, Typeface.BOLD);
		addView(this._tv);

		setIcon();
		setTextColor();
		setBackground();
	}

	public void setChecked(boolean checked) {
		this._checked = checked;
		setIcon();
		setTextColor();
		setBackground();
		setSelected(checked);
	}

	private void setIcon() {
		if (this._drawableOn == null && this._drawableOff == null) {
			removeViewInLayout(this._iv);
		}
		else {
			if (this._drawableOff == null) {
				this._drawableOff = this._drawableOn;
			}
			else if (this._drawableOn == null) {
				this._drawableOn = this._drawableOff;
			}
			if (this._checked) {
				this._iv.setImageDrawable(this._drawableOn);
			}
			else {
				this._iv.setImageDrawable(this._drawableOff);
			}
		}
	}

	private void setTextColor() {
		if (this._text == null || this._text.length() == 0) {
			removeViewInLayout(this._tv);
		}
		else {
			if (this._checked) {
				this._tv.setTextColor(this._textOnColor);
			}
			else {
				this._tv.setTextColor(this._textOffColor);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void setBackground() {
		if (this._backgroundOn != null || this._backgroundOff != null) {
			if (this._backgroundOn == null) {
				this._backgroundOn = this._backgroundOff;
			}
			else if (this._backgroundOff == null) {
				this._backgroundOff = this._backgroundOn;
			}
			if (this._checked) {
				setBackgroundDrawable(this._backgroundOn);
			}
			else {
				setBackgroundDrawable(this._backgroundOff);
			}
		}
	}

}
