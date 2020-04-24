package com.example.wifiscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.FileNameMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class SuperWiFi extends MainActivity {

    private String fileLabelName = "fileLabelName";

    private String[] fileNameGroup = {}; // at least 3 WiFis here.
    private static final int[][] POS = {}; // the according positions of WiFis.

    private int testTime = 10;
    private int scanningTime = 10;

    private int numberOfWiFi = fileNameGroup.length;

    // RSS_Value_Record and RSS_Measurement_Number_Record are used to record RSSI values
    private int[] RSS_Value_Record = new int[numberOfWiFi];
    private int[] RSS_Measurement_Number_Record = new int[numberOfWiFi];
    private int[] RSS_Value_Average = new int[numberOfWiFi];

    private WifiManager wifiManager = null;
    private Vector<String> scanned = null;
    boolean isScanning = false;

    public SuperWiFi(Context context) {
        this.wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.scanned = new Vector<String>();
    }

    private void startScan() {
        this.isScanning = true;
        Thread scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                scanned.clear();
                for (int index = 0; index < numberOfWiFi; index++) {
                    RSS_Value_Record[index] = 0;
                    RSS_Measurement_Number_Record[index] = 0;
            }
            int curTestTime = 1;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String curDateString = formatter.format(curDate);
                for (int index = 0; index < numberOfWiFi; index++) {
                write2file(fileLabelName+"-"+fileNameGroup[index]+".txt", "Test_ID: "+testID+" TestTime: "+curDateString+"BEGIN\r\n");
            }

                while (curTestTime++ <= testTime) performScan();

                for (int index = 0; index < numberOfWiFi; index++) {
                    if (RSS_Measurement_Number_Record[index] == 0) {
                        RSS_Value_Average[index] = -100; // a very small value
                    }
                    else {
                        RSS_Value_Average[index] = RSS_Value_Record[index] / RSS_Measurement_Number_Record[index];
                    }
                    scanned.add(fileLabelName+"-"+fileNameGroup[index]+" = "+RSS_Value_Average[index]+"\r\n");
                }

                // location
                Locate l = new Locate(RSS_Value_Average, POS);
                Vector<Double> loc = l.location();
                scanned.add("Location: "+loc.toString()+"\r\n");

                for (int index = 1; index <= numberOfWiFi; index++) {
                    write2file(fileLabelName+"-"+fileNameGroup[index-1]+".txt","testID:"+testID+"\r\n");
                    write2file(fileLabelName+"-"+fileNameGroup[index-1]+".txt", "Location: "+loc.toString()+"\r\n");
                    write2file(fileLabelName+"-"+fileNameGroup[index-1]+".txt","END\r\n");
                }
                isScanning = false;
            }
        });
        scanThread.start();
    }

    private void performScan() {
        if (wifiManager == null) {
            return;
        }
        try {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            wifiManager.startScan();
            try {
                Thread.sleep(scanningTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.scanned.clear();
            List<ScanResult> sr = wifiManager.getScanResults();
            for (ScanResult ap : sr) {
                for (int index = 1; index <= fileNameGroup.length; index++) {
                    if (ap.SSID.equals(fileNameGroup[index - 1])) {
                        RSS_Value_Record[index - 1] = RSS_Value_Record[index - 1] + ap.level;
                        RSS_Measurement_Number_Record[index - 1]++;
                        write2file(fileLabelName + "-" + fileNameGroup[index - 1] + ".txt", ap.level + "\r\n");
                    }
                }
            }
        } catch (Exception e) {
            this.isScanning = false;
            this.scanned.clear();
        }
    }

    public void scanRSS() {
        startScan();
    }

    public boolean isScan() {
        return isScanning;
    }

    public Vector<String> getRSSList() {
        return scanned;
    }

    private void write2file(String fileName, String a) {
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath()+fileName);
            if (!file.exists()) {
                final boolean l = file.createNewFile();
            }
            RandomAccessFile randomFile = new RandomAccessFile(Environment.getExternalStorageDirectory().getPath()+fileName, "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.writeBytes(a);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
