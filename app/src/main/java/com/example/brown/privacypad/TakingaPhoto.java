package com.example.brown.privacypad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class TakingaPhoto extends AppCompatActivity {

    private File imageFile;
    private static final int PHOTO_TAKEN = 0;
    private static final int BROWSE_GALLERY_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takinga_photo);

        addSnapButtonListener();
    }

    private void addSnapButtonListener() {
        Button snap = (Button)findViewById(R.id.snap);

        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                imageFile = new File(picturesDirectory, "passpoints_image");

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(i, PHOTO_TAKEN);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_TAKEN){
            Bitmap photo = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

            if (photo != null){


                ImageView imageview = (ImageView)findViewById(R.id.view);
                imageview.setImageBitmap(photo);
            }
            else {
                Toast.makeText(this, R.string.unable_to_save_photo_file, Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
