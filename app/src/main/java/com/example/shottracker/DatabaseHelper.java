package com.example.shottracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private SQLiteDatabase database = this.getWritableDatabase();

    private static final String DATABASE_NAME = "Shots.db";
    private static final String TABLE_NAME = "shots";
    private static final String COLUMN0 = "id";
    private static final String COL_CLUB = "club";
    private static final String COL_DISTANCE = "distance";
    private static final String COL_BALL_FLIGHT = "ball_flight";
    private static final String COL_NOTES = "notes";

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CLUB + " TEXT, " + COL_DISTANCE + " REAL, " + COL_BALL_FLIGHT + " TEXT, " + COL_NOTES + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String club, Double distance, String ballFlight, String notes){
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CLUB + " TEXT, " + COL_DISTANCE + " REAL, " + COL_BALL_FLIGHT + " TEXT, " + COL_NOTES + " TEXT)");

        //database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_CLUB, club);
        contentValues.put(COL_DISTANCE, distance);
        contentValues.put(COL_BALL_FLIGHT, ballFlight);
        contentValues.put(COL_NOTES, notes);

        long result = database.insert(TABLE_NAME, null, contentValues);

        return result != -1; //(if result == -1 return false else true
    }

    public Cursor getData(){
        Cursor data = null;
        Cursor cursor = database.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '"
                + TABLE_NAME + "'", null);

        if(cursor.getCount() > 0)
            data = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        cursor.close();
        return data;
    }

    public int deleteShot(String id){
        return database.delete(TABLE_NAME,  "id = ?", new String[] {id});
    }

    public void deleteData(){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //database.delete(TABLE_NAME, null,null);
    }

    public void moveToRow(){
        Cursor cursor;
    }
}
