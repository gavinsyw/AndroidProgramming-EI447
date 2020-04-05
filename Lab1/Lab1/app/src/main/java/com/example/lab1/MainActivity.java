package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.PermissionRequest;

import java.security.Permission;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    static int i=0, j=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(new TestPath(this));
        this.getFilesDir();
    }

    @Override
    @SuppressLint("ResourceType")
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.toolsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawView dv = (DrawView) findViewById(R.id.drawView1);
        switch (item.getItemId()) {
            case R.id.red:
                dv.p.setColor(Color.RED);
                dv.p.setXfermode(null);
                break;
            case R.id.blue:
                dv.p.setColor(Color.BLUE);
                dv.p.setXfermode(null);
                break;
            case R.id.green:
                dv.p.setColor(Color.GREEN);
                dv.p.setXfermode(null);
                break;
            case R.id.width_1:
                dv.p.setStrokeWidth(5);
                break;
            case R.id.width_2:
                dv.p.setStrokeWidth(10);
                break;
            case R.id.width_3:
                dv.p.setStrokeWidth(15);
                break;
            case R.id.eraser:
                dv.eraser();
                break;
            case R.id.clearAll:
                dv.clearAll();
                break;
            case R.id.back:
                dv.back();
                break;
            case R.id.save:
                j = 0;
                dv.save(i, j);
                i++;
                break;
        }
        return true;
    }
}
