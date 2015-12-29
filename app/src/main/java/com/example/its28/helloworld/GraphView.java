package com.example.its28.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.jar.Attributes;

/**
 * Created by its28 on 2015/10/6.
 */
public class GraphView extends View {

    private Bitmap mBitmap;
    private Paint mPaint = new Paint();
    private Canvas mCanvas = new Canvas();

    private float mSpeed = 10.0f;
    private float centerX = this.getWidth() / 2;
    private float centerY = this.getHeight() / 2;
    private float mLastX = centerX;
    private float mLastY = centerY;
    private float mScale;
    private float mLastValue;
    private float mYOffset;
    private int mColor;
    private float mWidth;
    private float maxValue = 1024f;

    public GraphView (Context context) {
        super(context);
        init();
    }

    public GraphView (Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        mColor = Color.argb(192, 64, 128, 64); //定義顏色ARGB
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public void addDataPoint(float valueX, float valueY) {
        final Paint paint = mPaint;
        float newX = mLastX + valueX;
        float newY = mLastY + valueY;

        paint.setColor(mColor);
        mCanvas.drawLine(mLastX, mLastY, newX, newY, paint);
        mLastX = newX;
        mLastY = newY;
    }

    public void Invalidate() {
        invalidate();
    }

    public void setMaxValue(int max) {
        maxValue = max;
        mScale = -(mYOffset * (1.0f / maxValue));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);
        mCanvas.drawColor(0xFFFFFFFF);
        mYOffset = h;
        mScale = -(mYOffset * (1.0f / maxValue));
        mWidth = w;
        mLastX = mWidth;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
            if (mBitmap != null) {
                if (mLastX >= mWidth) {
                    mLastX = 0;
                    final Canvas cavas = mCanvas;
                    cavas.drawColor(0xFFFFFFFF);
                    mPaint.setColor(0xFF777777);
                    cavas.drawLine(0, mYOffset, mWidth, mYOffset, mPaint);
                }
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
        }
    }
}
