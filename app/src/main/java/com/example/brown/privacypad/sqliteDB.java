package com.example.brown.privacypad;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class sqliteDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Notes.db";
    public Context con;

    public sqliteDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
        // TODO Auto-generated constructor stub
        con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table notes " +
                        "(timestamp integer primary key, title text, contents text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    public boolean insertNotes(long timestamp, String title, String contents)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("timestamp", timestamp);
        contentValues.put("title", title);
        contentValues.put("contents", contents);

        db.insert("notes", null, contentValues);
        return true;
    }


    public List<String> getTitle() {
        List<String> titleList = new ArrayList<String>();
        // Select All Query

        // Open database for Read / Write
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title FROM notes ORDER BY timestamp DESC", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(0);

                // Adding contact to list
                titleList.add(data);
            } while (cursor.moveToNext());
        }

        // return user list
        return titleList;
    }

    public List<String> getTimestamp() {
        List<String> timestampList = new ArrayList<String>();
        // Select All Query

        // Open database for Read / Write
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT timestamp FROM notes ORDER BY timestamp DESC", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(0);

                // Adding contact to list
                timestampList.add(data);
            } while (cursor.moveToNext());
        }

        // return user list
        return timestampList;
    }

    public boolean contains(String title) {
        boolean contains;
        int occurrences = 0;
        // Select All Query

        // Open database for Read / Write
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(title) FROM notes WHERE title = \"" + title + "\"", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                occurrences = Integer.parseInt(cursor.getString(0));

                // Adding contact to list
            } while (cursor.moveToNext());
        }

        // return user list
        return (occurrences > 0);
    }

    public String getContents(long timestamp) {
        String data = new String();
        // Select All Query

        // Open database for Read / Write
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT contents FROM notes WHERE timestamp = \"" + Long.toString(timestamp) + "\"", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                data = cursor.getString(0);

                // Adding contact to list
            } while (cursor.moveToNext());
        }

        // return user list
        return data;
    }

    // Deleting single contact
    public void deleteData(long timestamp) {
        SQLiteDatabase db = getReadableDatabase();
        db.delete("notes", "timestamp = ?",
                new String[] {Long.toString(timestamp)});
//        Toast.makeText(con, "Successfully Deleted", Toast.LENGTH_LONG).show();
        db.close();
    }

    // Updating single data
    public int updateData(long timestamp, String contents) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("contents", contents);

        // updating row
        return db.update("notes", values, "timestamp = ?",
                new String[] {Long.toString(timestamp)});
    }

    // Updating single data
    public int updateTitle(long timestamp, String newtitle) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", newtitle);

        // updating row
        return db.update("notes", values, "timestamp = ?",
                new String[] {Long.toString(timestamp)});
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}