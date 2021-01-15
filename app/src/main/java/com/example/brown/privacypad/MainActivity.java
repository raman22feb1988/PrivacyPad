package com.example.brown.privacypad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity {
    public static final String DEBUGTAG = "JB";
    public static final String TETXFILE = "privacypad.txt";
    public static final String FILESAVED = "FileSaved";
    public static final String RESET_PASSPOINTS = "ResetPasspoints";
    private Uri image;
    private static final int PHOTO_TAKEN_REQUEST = 0;
    private static final int BROWSE_GALLERY_REQUEST = 1;

    List<String> l;
    List<String> t;
    sqliteDB sqlli;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        l = new ArrayList<String>();
        t = new ArrayList<String>();
        sqlli = new sqliteDB(MainActivity.this);

        addSaveButtonListener();
        addLockButtonListener();

//        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//        boolean filesaved = prefs.getBoolean(FILESAVED, false);
//
//        if (filesaved) {
            loadSavedFile();
//        }


    }


    private void resetPasspoints(Uri image) {
        Intent i = new Intent(this, ImageActivitY.class);
        i.putExtra(ImageActivitY.RESET_PASSPOINT, true);
        // i.putExtra(ImageActivitY.RESET_IMAGE, image.getPath());

        if (image != null){

            i.putExtra(ImageActivitY.RESET_IMAGE, image.getPath());
        }

        startActivity(i);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_passpoints_reset:
                resetPasspoints(null);
                return true;
            case R.id.menu_replace_image:
                replaceImage();
                return true;
            default:

                return super.onOptionsItemSelected(item);

        }

    }


    private void replaceImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.replace_image, null);
        builder.setTitle(R.string.replace_lock_image);
        builder.setView(v);



        final AlertDialog dlg = builder.create();
        dlg.show();

        // Button TakingaPhoto = (Button) dlg.findViewById(R.id.take_photo);
        Button browseGallery = (Button) dlg.findViewById(R.id.browse_gallery);

//        TakingaPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, TakingaPhoto.class));
//
//            }
//        });


        browseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseGallery();
            }
        });
    }




    //THIS METHOD INVOKES BROWSE GALLERY ACTIVITY

    private void browseGallery(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, BROWSE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if(requestCode == BROWSE_GALLERY_REQUEST){

            String[] columns = {MediaStore.Images.Media.DATA};

            Uri imageuri = intent.getData();
            Cursor cursor = getContentResolver().query(imageuri, columns, null, null, null);

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(columns[0]);
            String imagePath = cursor.getString(columnIndex);

            cursor.close();

            image = Uri.parse(imagePath);
        }

        if (image == null){

            Toast.makeText(this, R.string.unable_to_display_image, Toast.LENGTH_LONG).show();

            return;
        }


        Log.d(DEBUGTAG, "Photo:" + image.getPath());
        resetPasspoints(image);

    }


    private void addLockButtonListener() {
        Button lockBtn = (Button) findViewById(R.id.lock);
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUGTAG, "start image activity");
                startActivity(new Intent(MainActivity.this, ImageActivitY.class));

            }
        });
    }

    private void loadSavedFile(){
        ListView lv = (ListView)findViewById(R.id.list);

//        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//        String s = prefs.getString("data", null);

        l = sqlli.getTitle();
        t = sqlli.getTimestamp();

//        if(s == null || s.equals("[]")) {
//            l = new ArrayList<String>();
//        }
//        else {
//            s = s.substring(1, s.length()-1);
//            l = new ArrayList<String>(Arrays.asList(s.split(", ")));
//        }

        customadapter cusadapter = new customadapter(MainActivity.this, R.layout.listelements, l);
        lv.setAdapter(cusadapter);
    }

    public class customadapter extends ArrayAdapter<String>
    {
        Context con;
        int _resource;
        List<String> lival;

        public customadapter(Context context, int resource, List<String> li) {
            super(context, resource, li);
            // TODO Auto-generated constructor stub
            con = context;
            _resource = resource;
            lival = li;
        }

        @Override
        public View getView(final int position, View v, ViewGroup vg)
        {
            View vi = null;
            LayoutInflater linflate = (LayoutInflater)(MainActivity.this).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = linflate.inflate(_resource, null);

            TextView t1 = (TextView) vi.findViewById(R.id.title);
            Button b1 = (Button) vi.findViewById(R.id.edittitle);
            Button b2 = (Button) vi.findViewById(R.id.editcontents);
            Button b3 = (Button) vi.findViewById(R.id.delete);

            final String data = lival.get(position);
            final long timestamp = Long.parseLong(t.get(position));
            t1.setText(data);

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    final View yourCustomView = inflater.inflate(R.layout.alertbox, null);

                    final EditText input = (EditText) yourCustomView.findViewById(R.id.text);
                    input.setText(data);

                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Modify title")
                            .setView(yourCustomView)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String newtext = input.getText().toString();
                                    sqlli.updateTitle(timestamp, newtext);

//                                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = prefs.edit();
//                                    editor.putString("data", l.toString());
//                                    editor.putBoolean(FILESAVED, true);
//                                    editor.commit();

                                    Activity mainactivity = MainActivity.this;
                                    mainactivity.recreate();
                                }
                            }).create();
                    dialog.show();
                }
            });

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                   Intent intent = new Intent(MainActivity.this, Contents.class);
                   intent.putExtra("timestamp", timestamp);

                   startActivity(intent);
                   finish();
                }
            });

            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
//                    l.remove(position);
//
//                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putString("data", l.toString());
//                    editor.putBoolean(FILESAVED, true);
//                    editor.commit();

                    sqlli.deleteData(timestamp);

                    Activity mainactivity = MainActivity.this;
                    mainactivity.recreate();
                }
            });

            return vi;
        }

    }

    private void addSaveButtonListener(){

        Button saveBtn = (Button) findViewById(R.id.save);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View yourCustomView = inflater.inflate(R.layout.alertbox, null);

                final EditText input = (EditText) yourCustomView.findViewById(R.id.text);

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Enter new notes")
                        .setView(yourCustomView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String text = input.getText().toString();
                                sqlli.insertNotes(System.currentTimeMillis(), text, "");
//                                l.add(0, text);
//
//                                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//                                SharedPreferences.Editor editor = prefs.edit();
//                                editor.putString("data", l.toString());
//                                editor.putBoolean(FILESAVED, true);
//                                editor.commit();

                                Activity mainactivity = MainActivity.this;
                                mainactivity.recreate();
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    private void saveText() {

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View yourCustomView = inflater.inflate(R.layout.alertbox, null);

        final EditText input = (EditText) yourCustomView.findViewById(R.id.text);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Enter new notes")
                .setView(yourCustomView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String text = input.getText().toString();
                        sqlli.insertNotes(System.currentTimeMillis(), text, "");
//                        l.add(0, text);
//
//                        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putString("data", l.toString());
//                        editor.putBoolean(FILESAVED, true);
//                        editor.commit();

                        Activity mainactivity = MainActivity.this;
                        mainactivity.recreate();
                    }
                }).create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}