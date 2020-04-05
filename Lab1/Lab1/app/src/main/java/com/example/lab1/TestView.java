package com.example.lab1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

class TestView extends View {

    public Canvas canvas;
    public Paint p;
    private Bitmap bitmap;
    float x,y;
    int bgColor;

    public TestView(Context context) {
        super(context);
        bgColor = Color.WHITE;
        int screen_width = context.getResources().getDisplayMetrics().widthPixels;
        int screen_height = context.getResources().getDisplayMetrics().heightPixels;
        bitmap = Bitmap.createBitmap(screen_width, screen_height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(bitmap);
        p = new Paint(Paint.DITHER_FLAG);
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(8);
    }

    // Touch event
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            canvas.drawLine(x, y, event.getX(), event.getY(), p);
            invalidate();
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
            canvas.drawPoint(x, y, p);
            invalidate();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            x = event.getX();
            y = event.getY();
        }

        x = event.getX();
        y = event.getY();
        return true;
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawBitmap(bitmap, 0, 0, null);
    }

}
