package com.levelup.jiemimoshengren.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.levelup.jiemimoshengren.R;


public class RoundImageView extends ImageView {

    private boolean isCircle;/*是否是圆形*/
    private int borderRadius;/*y圆角半径半径*/

    private static final int DEFAULT_BORDER_RADIUS = 0;
    private static final boolean DEFAULT_TYPE = false;

    private Paint bitmapPaint; /*绘图的Paint*/
    private Matrix matrix; /*控制图片的缩放*/
    private BitmapShader bitmapShader; /*为绘制图片着色*/

    private int width; /*图片的宽度*/
    private RectF roundRect; /*图片的外边框矩形*/

    public RoundImageView(Context context, AttributeSet attrs){
        super(context,attrs);

        //初始化工具
        matrix = new Matrix();
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        borderRadius = ta.getDimensionPixelSize(R.styleable.RoundImageView_borderRadius,dp2px(DEFAULT_BORDER_RADIUS));
        isCircle = ta.getBoolean(R.styleable.RoundImageView_isCircle,DEFAULT_TYPE);
        ta.recycle();/*记得要在使用完后recycle*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        /*如果类型是圆形，则强制改变view的宽高一致*/
        if(isCircle){
            width = Math.min(getMeasuredHeight(),getMeasuredWidth());
            borderRadius = width/2;
            setMeasuredDimension(width,width);
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        setupShader();
        if(isCircle){
            canvas.drawCircle(borderRadius,borderRadius,borderRadius,bitmapPaint);
        }else{
            canvas.drawRoundRect(roundRect,borderRadius,borderRadius,bitmapPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w,int h,int oldw,int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        if(!isCircle){
            roundRect = new RectF(0,0,getWidth(),getHeight());
        }
    }

    private void setupShader(){
        Drawable drawable = getDrawable();
        if(drawable == null){
            /*可以在这里加载一张默认的图片*/
            return ;
        }
        Bitmap bmp = drawableToBitmap(drawable);
        bitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if(isCircle){
            int size = Math.min(bmp.getWidth(),bmp.getHeight());
            scale = width*1.0f/size;
        }else{
            scale = Math.max(getWidth()*1.0f/bmp.getWidth(),getHeight()*1.0f/bmp.getHeight());
        }
        matrix.setScale(scale,scale);
        bitmapShader.setLocalMatrix(matrix);
        bitmapPaint.setShader(bitmapShader);
    }

    /*将drawable转化成Bitmap*/
    private Bitmap drawableToBitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            BitmapDrawable bd = (BitmapDrawable)drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicHeight();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,w,h);
        drawable.draw(canvas);
        return  bitmap;
    }

    /*将dp转化成px*/
    private int dp2px(int dpVal){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,getResources().getDisplayMetrics());
    }

    /*设置圆角半径*/
    public void setBorderRadius(int borderRadius){
        int pxVal = dp2px(borderRadius);
        if(this.borderRadius!=pxVal){
            this.borderRadius = pxVal;
            invalidate();
        }
    }

    /*设置图片类型*/
    public void setType(boolean isCircle){
        if(this.isCircle!=isCircle){
            this.isCircle = isCircle;
            requestLayout();
        }
    }

}