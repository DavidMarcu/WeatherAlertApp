package com.fiiandroid.weatherapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationDatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "alarms_db";

    private static final String TABLE_NAME = "alarm_table";
    public static final String ALARM_COL = "alarm_time";
    public static final String TIMESTAMP_COL = "next_alarm";

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
                ALARM_COL + " TEXT, " + TIMESTAMP_COL + " INTEGER)";
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
        long nowTimestamp;
        long supposedAlarmTimestamp;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            timeText = LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
            localDateTime = localDateTime.plusHours(hour).plusMinutes(minute);

            nowTimestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
            supposedAlarmTimestamp = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();

        } else {
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            timeText = df.format(date);
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            calendar.set(year, month, day, hour, minute, 0);

            nowTimestamp = date.getTime();
            supposedAlarmTimestamp = calendar.getTimeInMillis() / 1000L;
        }

        long timestamp = nowTimestamp >= supposedAlarmTimestamp ? supposedAlarmTimestamp + 86400L : supposedAlarmTimestamp;
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALARM_COL, timeText);
        contentValues.put(TIMESTAMP_COL, timestamp);

        long result = db.insertOrThrow(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public void deleteNotification(String timeText) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = ALARM_COL + "=\"" + timeText + "\"";
        int result = db.delete(TABLE_NAME, whereClause, null);
        Log.i(TAG, "Rows affected: " + result);
    }

    public Cursor getDataOrderedByTimeText() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY 2";
        return db.rawQuery(query, null);
    }

    public Cursor getDataOrderedByTimestamp() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY 3";
        return db.rawQuery(query, null);
    }

    public void updateNotification(long oldTimestamp, long newTimestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        String rawQuery = "UPDATE " + TABLE_NAME + " SET " + TIMESTAMP_COL + "=" + newTimestamp + " WHERE "
                + TIMESTAMP_COL + "= ?";
        Log.i(TAG, "Timestamp in update method: " + oldTimestamp);
        Cursor result = db.rawQuery(rawQuery, new String[]{String.valueOf(oldTimestamp)});
        result.close();
    }
}
