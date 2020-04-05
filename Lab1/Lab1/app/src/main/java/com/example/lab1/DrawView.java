package com.example.lab1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class DrawView extends View {
    private static int screen_width, screen_height;
    private float x, y;
    private Path path;
    public Paint p;
    Bitmap bitmap;
    Canvas canvas;

    public DrawView(Context context, AttributeSet set) {
        super(context, set);
        screen_width = context.getResources().getDisplayMetrics().widthPixels;
        screen_height = context.getResources().getDisplayMetrics().heightPixels;
        bitmap = Bitmap.createBitmap(screen_width, screen_height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        path = new Path();
        canvas.setBitmap(bitmap);
        p = new Paint(Paint.DITHER_FLAG);
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStrokeCap(Paint.Cap.ROUND);
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
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX(), currentY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(currentX, currentY);
                x = currentX; y = currentY;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(currentX-x), dy = Math.abs(currentY-y);
                if (dx+dy >= 3) {
                    path.lineTo(currentX, currentY);
                }
                break;
            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, p);
                path.reset();
                break;
        }
        invalidate();
        return true;
    }

    @SuppressLint("ResourceAsColor")
    public void eraser() {
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        p.setColor(R.color.defaultBackground);
        p.setStrokeWidth(50);
    }

    public void back() {
       Canvas last_canvas = new Canvas();
       last_canvas.restore();
    }

    public void clearAll() {
        bitmap = null;
        bitmap = Bitmap.createBitmap(screen_width, screen_height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(bitmap);
        invalidate();
    }

    public void save(int i, int j) {
        try {
            saveBitmap("DrawingPicture", "DrawingPicture"+1, j);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBitmap(String fileSize, String fileName, int j) throws IOException {
        String directoryPath;
        directoryPath = getFilePath(getContext(), fileSize, fileName, j);
        File file = new File(directoryPath);
        try {
            FileOutputStream fileOS = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOS);
            fileOS.flush();
            fileOS.close();
            Toast.makeText(getContext(), "Saved successfully to"+directoryPath, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        getContext().sendBroadcast(intent);
    }

    public static String getFilePath(Context context, String fileSize, String fileName, int j) {
        String directoryPath = "";
        if (j == 0) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                directoryPath = Objects.requireNonNull(context.getExternalFilesDir(fileSize)).getAbsolutePath()+
                        File.separator+fileName+".png";
            }
            else {
                directoryPath = context.getFilesDir().getAbsolutePath()+File.separator+fileName+".png";
            }
        }
        else {
            directoryPath = Environment.getExternalStorageDirectory()+File.separator+
                    Environment.DIRECTORY_DCIM+File.separator+fileName+".png";
        }
        return directoryPath;
    }


}
