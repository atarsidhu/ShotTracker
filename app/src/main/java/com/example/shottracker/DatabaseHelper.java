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

    private static final String TABLE_NAME = "shots";
    private static final String COLUMN0 = "id";
    private static final String COLUMN1 = "club";
    private static final String COLUMN2 = "distance";
    private static final String COLUMN3 = "ball_flight";
    private static final String COLUMN4 = "notes";

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN1 + " TEXT, " + COLUMN2 + " REAL, " + COLUMN3 + " TEXT, " + COLUMN4 + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String club, Double distance, String ballFlight, String notes){
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN1 + " TEXT, " + COLUMN2 + " REAL, " + COLUMN3 + " TEXT, " + COLUMN4 + " TEXT)");

        //database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN1, club);
        contentValues.put(COLUMN2, distance);
        contentValues.put(COLUMN3, ballFlight);
        contentValues.put(COLUMN4, notes);

        long result = database.insert(TABLE_NAME, null, contentValues);

        return result != -1; //(if result == -1 return false else true
    }

    public Cursor getAllData(){
        //database = this.getWritableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }

    public void deleteData(){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
