package com.example.brown.privacypad;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class Contents extends AppCompatActivity {
    sqliteDB sqlli;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contents);

        sqlli = new sqliteDB(Contents.this);

        TextView t1 = (TextView) findViewById(R.id.textview);
        Button b1 = (Button) findViewById(R.id.edit);
        Button b2 = (Button) findViewById(R.id.delete);
        Button b3 = (Button) findViewById(R.id.back);

        Intent intent = getIntent();
        final long timestamp = intent.getLongExtra("timestamp", 0);
        final String contents = sqlli.getContents(timestamp);

        t1.setText(contents);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LayoutInflater inflater = LayoutInflater.from(Contents.this);
                final View yourCustomView = inflater.inflate(R.layout.alertbox, null);

                final EditText input = (EditText) yourCustomView.findViewById(R.id.text);
                input.setText(contents);

                AlertDialog dialog = new AlertDialog.Builder(Contents.this)
                        .setTitle("Modify contents")
                        .setView(yourCustomView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String newtext = input.getText().toString();
                                sqlli.updateData(timestamp, newtext);

//                                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = prefs.edit();
//                                    editor.putString("data", l.toString());
//                                    editor.putBoolean(FILESAVED, true);
//                                    editor.commit();

                                Activity contents = Contents.this;
                                contents.recreate();
                            }
                        }).create();
                dialog.show();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sqlli.deleteData(timestamp);

                Intent intent = new Intent(Contents.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Contents.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}