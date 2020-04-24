package com.example.wifiscanner;

import android.util.Log;

import java.util.Vector;

class Locate {

    private double loc_X, loc_Y;

    Locate(int[] RSS_Record, int[][] POS) {
        if (RSS_Record.length < 3 || POS.length != RSS_Record.length) {
            loc_X = -1; loc_Y = -1;
        }
        else {
            loc_X = 0; loc_Y = 0;

            // Location algorithm, use LANDMARC here.
            Vector<Double> dis = new Vector<>(RSS_Record.length);
            double total_dis = 0;
            for (int rss : RSS_Record) {
                double d = 1.0 / distance(rss);
                dis.add(d);
                total_dis += d;
            }
            Vector<Double> weight = new Vector<>(RSS_Record.length);
            for (double d : dis) {
                weight.add(d/total_dis);
            }
            for (int i = 0; i < RSS_Record.length; ++i) {
                loc_X += weight.get(i) * POS[i][0];
                loc_Y += weight.get(i) * POS[i][1];
            }
        }
    }

    Vector<Double> location() {
        Vector<Double> loc = new Vector<>(2);
        loc.add(loc_X); loc.add(loc_Y);
        return loc;
    }

    private double distance(int RSS_value) {
        int tmp = (-RSS_value+23)/20;
        double dis = 10^tmp;
        Log.d("WiFiScanner", "RSS_value: "+RSS_value+" dis: "+dis+"\r\n");
        return dis;
    }
}
