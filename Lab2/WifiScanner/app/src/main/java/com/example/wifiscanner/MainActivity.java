package com.example.wifiscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    public SuperWiFi rss_scan = null;
    Vector<String> RSSList = null;
    private String testList = null;
    public static int testID = 0;//The ID of the test result

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText ipText = (EditText)findViewById(R.id.ipText);
        final Button changeActivity = (Button)findViewById(R.id.button1);
        final Button cleanList = (Button)findViewById(R.id.button2);

        verifyStoragePermissions(this);
        rss_scan = new SuperWiFi(this);

        testList = "";
        testID = 0;

        changeActivity.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                testID = testID + 1;
                rss_scan.scanRSS();
                while (rss_scan.isScan()) ;
                RSSList = rss_scan.getRSSList();
                final EditText ipText = (EditText)findViewById(R.id.ipText);
                testList = testList + "testID: " + testID + "\n" + RSSList.toString() + "\n";
                ipText.setText(testList);
            }
        });

        cleanList.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                testList = "";
                ipText.setText(testList);
                testID = 0;
            }
        });

    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION };

    public static void verifyStoragePermissions(Activity activity) {
        // check write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}
