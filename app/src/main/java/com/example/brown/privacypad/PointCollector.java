package com.example.brown.privacypad;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brown on 1/31/17.
 */

public class PointCollector implements View.OnTouchListener {


    public static final int NUM_POINTS = 4;
    private PointCollecterListener listener;
    private List<Point> points = new ArrayList<Point>();

    public PointCollecterListener getListener() {
        return listener;
    }



    public boolean onTouch(View v, MotionEvent event) {
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        String message = String.format("Coordinates: (%d, %d)", x,y );

        Log.d(MainActivity.DEBUGTAG, message);

        points.add(new Point(x, y));

        if (points.size() == NUM_POINTS){

        if (listener != null){
            listener.pointscollected(points);


        }
        }

        return false;
    }

    public void setListener(PointCollecterListener listener) {
        this.listener = listener;
    }

    public void clear(){

        points.clear();
    }
}
