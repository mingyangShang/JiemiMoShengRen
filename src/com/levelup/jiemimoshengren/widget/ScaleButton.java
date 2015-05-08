package com.levelup.jiemimoshengren.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;

import com.levelup.jiemimoshengren.R;

public class ScaleButton extends View {

	private int DEFAULT_OUTER_RADIUS = 10; // 默认的圆半径
	private int DEFAULT_BACK_COLOR = 0xff0000; // 默认的背景色
	private int DEFAULT_STROKE_COLOR = 0xffffff; // 默认的边缘色
	private int DEFAULT_STROKE_WIDTH = 2; // 默认的边缘线条宽度
	private int DEFAULT_TEXT_COLOR = 0xffffff; // 默认的文字颜色
	private int DEFAULT_TEXT_SIZE = 1; // 默认的文字大小

	private int mOuterRadius; // 外圈的圆半径
	private int mBackColor; // 背景色
	private int mStrokeColor; // 边缘线条颜色
	private int mStrokeWidth; // 边缘线条宽度
	private int mTextColor; // 文字颜色
	private int mTextSize; // 文字大小
	private String mText = ""; // 显示文字

	private GradientDrawable mBackDrawable; // 背景Drawable
	private int mPreX; //原始x坐标

	private Paint mPaint;
	
	private AnimatorListener mAnimatorListener; //动画监听器

	public ScaleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 取得外圈圆半径等相关属性
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.ScaleButton);
		mOuterRadius = (int) ta.getDimension(
				R.styleable.ScaleButton_outerRadius,
				dp2px(DEFAULT_OUTER_RADIUS));
		mBackColor = ta.getColor(R.styleable.ScaleButton_android_background,
				DEFAULT_BACK_COLOR);
		mStrokeColor = ta.getColor(R.styleable.ScaleButton_outerStrokeColor,
				DEFAULT_STROKE_COLOR);
		mStrokeWidth = ta.getDimensionPixelOffset(
				R.styleable.ScaleButton_outerStrokeWidth,
				dp2px(DEFAULT_STROKE_WIDTH));

		mText = ta.getString(R.styleable.ScaleButton_android_text);
		mTextColor = ta.getColor(R.styleable.ScaleButton_android_textColor,
				DEFAULT_TEXT_COLOR);
		mTextSize = ta.getDimensionPixelOffset(
				R.styleable.ScaleButton_android_textSize,
				sp2px(DEFAULT_TEXT_SIZE));

		ta.recycle();

		// 设置paint
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(mTextColor);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setTextSize(sp2px(mTextSize));

		// 建立背景色drawable
		mBackDrawable = new GradientDrawable();
		mBackDrawable.setColor(mBackColor);
		mBackDrawable.setStroke(mStrokeWidth, mStrokeColor);
		// 在后面还要设置corners

		this.setBackgroundDrawable(mBackDrawable);
	}

	public ScaleButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScaleButton(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		mOuterRadius = getMeasuredHeight()/2;
		System.out.println("radius"+mOuterRadius);
		mBackDrawable.setCornerRadius(mOuterRadius);
		
		System.out.println(""+getMeasuredWidth()+","+getMeasuredHeight()+"------------------");

//		setMeasuredDimension(getMeasuredWidth() + 2 * mOuterRadius,getMeasuredHeight());
		setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
	}

	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mPreX = left;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 绘制text文字

		int baseX = getWidth() / 2;
		int baseY = (int) (canvas.getHeight() / 2 - mPaint.descent() / 2 - mPaint
				.ascent() / 2);
		if(mText!=null && !TextUtils.isEmpty(mText)){
			canvas.drawText(mText, baseX, baseY, mPaint);
		}
		System.out.println("onDraw"+this.mOuterRadius+",getWidth:"+getWidth());
	}

	public void startAnimation() {
		ScaleButtonWrapper viewWrapper = new ScaleButtonWrapper(this);
		AnimatorSet animatorSet = new AnimatorSet();
		Animator animator = ObjectAnimator.ofInt(viewWrapper, "width",2*mOuterRadius);
		Animator animator2 = ObjectAnimator.ofFloat(viewWrapper, "x", mPreX+getWidth()/2-mOuterRadius);
		animatorSet.playTogether(animator);
		animatorSet.setInterpolator(new AccelerateInterpolator());
		animatorSet.setDuration(500);
		animatorSet.start();
		if(this.mAnimatorListener!=null){
			animatorSet.addListener(this.mAnimatorListener);
		}
	}
	
	
	public void setText(String text){
		this.mText = text;
		this.invalidate();
	}
	public String getText(){
		return this.mText;
	}
	
	public void setAnimatorListener(AnimatorListener animatorListener){
		this.mAnimatorListener = animatorListener;
	}
	public AnimatorListener getAnimatorListener(){
		return this.mAnimatorListener;
	}
	/**对View的包装*/
	public static class ScaleButtonWrapper {
		private View mTarget;

		public ScaleButtonWrapper(View target) {
			mTarget = target;
		}

		public int getWidth() {
			return mTarget.getLayoutParams().width;
		}
		public void setWidth(int width) {
			mTarget.getLayoutParams().width = width;
			mTarget.requestLayout();
		}

		public float getX() {
			return mTarget.getX();
		}
		public void setX(float x) {
			this.mTarget.setX(x);
		}
	}
		
	/** 将dp转化成px */
	private int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, getResources().getDisplayMetrics());
	}

	/** 将sp转化为px */
	private int sp2px(int spVap) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVap, getResources().getDisplayMetrics());
	}

}
