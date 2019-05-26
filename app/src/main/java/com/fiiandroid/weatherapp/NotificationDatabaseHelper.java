package com.fiiandroid.weatherapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class NotificationDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "alarms_db";

    private static final String TABLE_NAME = "alarm_table";
    public static final String ALARM_COL = "alarm_time";

    private static NotificationDatabaseHelper notificationDatabaseHelper;


    public static NotificationDatabaseHelper getInstance(Context context) {
        if (notificationDatabaseHelper != null) {
            return notificationDatabaseHelper;
        }
        notificationDatabaseHelper = new NotificationDatabaseHelper(context);
        return notificationDatabaseHelper;
    }

    private NotificationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ALARM_COL + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertNotification(int hour, int minute) {
        SQLiteDatabase db = this.getWritableDatabase();
        String timeText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            timeText = LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            timeText = df.format(date);
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALARM_COL, timeText);
        long result = db.insertOrThrow(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public void deleteNotification(String timeText) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = ALARM_COL + "=\"" + timeText + "\"";
        int result = db.delete(TABLE_NAME, whereClause, null);
        Log.i(TAG, "Rows affected: " + result);
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY 2";
        return db.rawQuery(query, null);
    }
}
