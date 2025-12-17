package com.riontech.calendar.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class CustomDrawableView extends FrameLayout {
    Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Float initialPercent = 0f;
    Float fullPercent = 380f;
    Float percent = 0f, numberOfCountsOfDashes = 6f, spaceBetweenDash = 30f;
    Boolean isFirstTime = true;
    Float stokeWidth = 6f;
    public CustomDrawableView(Context context) {
        super(context);
    }
    public CustomDrawableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomDrawableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPercentage(int eventCount) {
        float partOfSingleLine = (initialPercent + fullPercent) / numberOfCountsOfDashes;
        percent = partOfSingleLine * eventCount;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        double centerPointX = getWidth() / 2;
        double centerPointY = getHeight() / 2;
        if (isFirstTime) {
            isFirstTime = false;
            initPaint(backgroundPaint, Color.parseColor("#AABBAA"));
            initPaint(foregroundPaint, Color.parseColor("#000000"));

            double lengthOfCircle = (Math.PI * getWidth());
            double dashWidth = (lengthOfCircle - spaceBetweenDash * (numberOfCountsOfDashes + 1)) / numberOfCountsOfDashes;
            float[] dashEffect = {(float) dashWidth, spaceBetweenDash};
            backgroundPaint.setPathEffect(new DashPathEffect(dashEffect, 0f));
            foregroundPaint.setPathEffect(new DashPathEffect(dashEffect, 0f));
        }
        canvas.drawCircle((float) centerPointX, (float) centerPointY, (float) centerPointX - stokeWidth, backgroundPaint);
        if (percent > 0)
            canvas.drawArc(new RectF(stokeWidth, stokeWidth, getWidth() - stokeWidth, getHeight() - stokeWidth), initialPercent, percent - 6, false, foregroundPaint);
    }

    void initPaint(Paint paint, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stokeWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }
}
