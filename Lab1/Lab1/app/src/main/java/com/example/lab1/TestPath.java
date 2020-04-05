package com.example.lab1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class TestPath extends View {
    private Path path;
    public Paint p;
    Bitmap bitmap;
    Canvas canvas;

    public TestPath(Context context) {
        super(context);
        int screen_width = context.getResources().getDisplayMetrics().widthPixels;
        int screen_height = context.getResources().getDisplayMetrics().heightPixels;
        bitmap = Bitmap.createBitmap(screen_width, screen_height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        path = new Path();
        canvas.setBitmap(bitmap);
        p = new Paint(Paint.DITHER_FLAG);
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStrokeWidth(10);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(R.color.defaultBackground);
        @SuppressLint("DrawAllocation") Paint bmp = new Paint();
        canvas.drawBitmap(bitmap, 0, 0, bmp);
        canvas.drawPath(path, p);
        canvas.save();

        // test path
        path.moveTo(100, 100);
        path.lineTo(200, 300);
        path.lineTo(300, 400);
        canvas.drawPath(path, p);
        path.reset();
    }

}
