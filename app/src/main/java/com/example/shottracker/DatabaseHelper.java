package com.example.shottracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase database;

    private static final String TABLE_NAME = "shot";
    private static final String COLUMN0 = "id";
    private static final String COL_CLUB = "club";
    private static final String COL_DISTANCE = "distance";
    private static final String COL_BALL_FLIGHT = "ball_flight";
    private static final String COL_NOTES = "notes";

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
        database = this.getWritableDatabase();
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

    /**
     * Adds the data to the database.
     * @param club the club used
     * @param distance the distance the ball has travelled
     * @param ballFlight the flight of the golf ball
     * @param notes additional notes
     * @return <b>true</b> if the data has been successfully added to the database and <b>false</b> is it has not been added.
     */
    public boolean addData(String club, Double distance, String ballFlight, String notes){
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CLUB + " TEXT, " + COL_DISTANCE + " REAL, " + COL_BALL_FLIGHT + " TEXT, " + COL_NOTES + " TEXT)");

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_CLUB, club);
        contentValues.put(COL_DISTANCE, distance);
        contentValues.put(COL_BALL_FLIGHT, ballFlight);
        contentValues.put(COL_NOTES, notes);

        long result = database.insert(TABLE_NAME, null, contentValues);

        return result != -1; //(if result == -1 return false else true
    }

    /**
     * Gets all rows in the database
     * @return all rows
     */
    public Cursor getData(){
        Cursor data = null;
        Cursor cursor = database.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '"
                + TABLE_NAME + "'", null);

        if(cursor.getCount() > 0)
            data = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        cursor.close();
        return data;
    }

    /**
     * Deletes the shot that was requested by the user.
     * @param id the ID of the shot to be deleted
     * @return <b>true</b> if the shot has been deleted or <b>false</b> if otherwise.
     */
    public int deleteShot(String id){
        return database.delete(TABLE_NAME,  "id = ?", new String[] {id});
    }

    /**
     * Removes the table from the database.
     * @return <b>true</b> if the table has been deleted or <b>false</b> if otherwise.
     */
    public boolean deleteData(){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        return getData() == null;
    }
}
