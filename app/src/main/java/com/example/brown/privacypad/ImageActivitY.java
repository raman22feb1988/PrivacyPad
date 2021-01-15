package com.example.brown.privacypad;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class
ImageActivitY extends AppCompatActivity implements PointCollecterListener  {

    public static final String RESET_PASSPOINT = "ResetPasspoints";
    public static final String RESET_IMAGE = "ResetImage";
    private boolean doPasspointReset = false;

    private final static String CURRENT_IMAGE = "CurrentImage";
    private  final static String PASSWORD_SET = "PASSWORD_SET";
    private static final int POINT_CLOSENESS = 50;
    private PointCollector pointCollector = new PointCollector();
    private Database db = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_activit_y);

        addTouchListener();

        pointCollector.setListener(this);


        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);

        String newImage = null;


        Bundle extras = getIntent().getExtras();

        if (extras != null){
            doPasspointReset = extras.getBoolean(RESET_PASSPOINT);

            newImage = extras.getString(RESET_IMAGE);

           // Boolean resetPasspoints = extras.getBoolean(MainActivity.RESET_PASSPOINTS);

           // if (resetPasspoints){
                //RESET THE PASSPOINTS HERE
            }
        if (newImage == null){

            newImage = prefs.getString(CURRENT_IMAGE, null);


        }else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(CURRENT_IMAGE, newImage);
            editor.commit();

        }

        setImage(newImage);

        if (!passpointsSet || doPasspointReset){

        showSetPasspointsPrompt();
        }


    }

    private void setImage(String path){
        ImageView imageView = (ImageView)findViewById(R.id.touch_image);




        if (path == null){
//might make mistakes here. chechk vid 43. time 10;25
            Drawable image = getResources().getDrawable(R.mipmap.rosemond);
            imageView.setImageDrawable(image);

        }
        else {
            imageView.setImageURI(Uri.parse(path));

        }

    }


    private void showSetPasspointsPrompt(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setTitle(R.string.create_passpoints);
        builder.setMessage(R.string.create_passpoints_text);


        AlertDialog dlg = builder.create();
        dlg.show();



    }

    private void showLoginPrompt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setTitle(R.string.enter_passpoints_title);
        builder.setMessage(R.string.enter_passpoints_text);

        AlertDialog dlg = builder.create();

        dlg.show();

    }

    private void addTouchListener() {
        ImageView image = (ImageView) findViewById(R.id.touch_image);
        image.setOnTouchListener(pointCollector);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflates menu: this add items to the action bar if it is present
        getMenuInflater().inflate(R.menu.activity_image_activit_y, menu);

        return true;
    }

    private void savePassPoints (final  List<Point> points){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.storing_data);
        final AlertDialog dlg = builder.create();
        dlg.show();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                db.storePoints(points);
                List<Point> test = db.getPoints();
                Log.d(MainActivity.DEBUGTAG, "POINTS SAVED" + points.size());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PASSWORD_SET, true);
                editor.commit();


                dlg.dismiss();
                pointCollector.clear();
            }
        };


        task.execute();


    }

    private void verifyPasspoints(final List<Point> touchedpoints){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.checking_passpoints);

        final AlertDialog dlg = builder.create();
        dlg.show();

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                List<Point> savedPoints = db.getPoints();

                Log.d(MainActivity.DEBUGTAG, "Points Saved: " + savedPoints.size());
                if (savedPoints.size()!= PointCollector.NUM_POINTS
                        || touchedpoints.size()!=PointCollector.NUM_POINTS){
                    return false;
                }
                for (int i =0; i<PointCollector.NUM_POINTS; i++){

                    Point savedPoint = savedPoints.get(i);
                    Point touchedPoint = touchedpoints.get(i);

                    int xDiff = savedPoint.x - touchedPoint.x;
                    int yDiff = savedPoint.y - touchedPoint.y;

                    int distSquared = xDiff*xDiff + yDiff*yDiff;

                    Log.d(MainActivity.DEBUGTAG, "Distance squared: " + distSquared);

                    if (distSquared> POINT_CLOSENESS*POINT_CLOSENESS){
                        return false;

                    }


                }

                return true;
            }


            @Override
            protected void onPostExecute(Boolean pass) {

                dlg.dismiss();
                pointCollector.clear();

                if (pass) {
                    //The add notes class should be invoked here
                    Intent i = new Intent(ImageActivitY.this,
                            MainActivity.class);
                    startActivity(i);

                }


                    else{

                    Toast.makeText(ImageActivitY.this, R.string.acess_denied,
                            Toast.LENGTH_SHORT).show();

                    }
            }
        };

        task.execute();


    }

    @Override
    public void pointscollected(final List<Point> points) {

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);


        if (doPasspointReset||!passpointsSet){
            savePassPoints(points);
            doPasspointReset = false;
            showLoginPrompt();
        }
        else {
            verifyPasspoints(points);
        }

        }

    }

