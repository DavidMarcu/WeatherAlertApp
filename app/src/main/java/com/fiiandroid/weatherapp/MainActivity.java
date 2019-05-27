package com.fiiandroid.weatherapp;

import android.Manifest;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.*;

public class MainActivity extends Activity {

    private static final String DATABASE_TAG = "DATABASE";
    private static final int INTERNET_PERMISSION = 1;
    private String temperatureText;
    private Button buttonNewNotification;
    private List<String> notifications;
    private ListView notificationListView;
    private List<Map<String, Object>> itemList;
    private Context context;

    private TimePickerDialog.OnTimeSetListener timePickerListener = (view, hourOfDay, minutes) -> {
        addNotification(hourOfDay, minutes);
        getNotifications();
    };

    private TimerTask notificationUpdates = new TimerTask() {
        @Override
        public void run() {
            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, NotificationObject.class);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationDatabaseHelper notificationDatabaseHelper = NotificationDatabaseHelper.getInstance(context);
            Cursor cursor = notificationDatabaseHelper.getDataOrderedByTimestamp();
            long timestamp = new Date().getTime() / 1000L;
            long currentTime = timestamp;
            if (cursor.moveToFirst()) {
                timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.TIMESTAMP_COL));
            }
            if(currentTime > timestamp){
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, timestamp * 1000L, alarmPendingIntent);
                //Increment update with a day;
                long newTimestamp = timestamp + 86400;
                Log.i(NotificationDatabaseHelper.TAG, "Timestamp in update method: " + timestamp);
                notificationDatabaseHelper.updateNotification(timestamp, newTimestamp);
            }
            cursor.close();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Timer notificationTimer = new Timer();
        notificationTimer.schedule(notificationUpdates, 0, 5000);
        getNotifications();
        addNewNotificationButtonListener();
        requestInternetService();
    }

    private void getNotifications() {
        NotificationDatabaseHelper notificationDatabase = NotificationDatabaseHelper.getInstance(this);
        Cursor cursor = notificationDatabase.getDataOrderedByTimeText();

        notificationListView = findViewById(R.id.notificationList);
        ListAdapter notificationsListAdapter = new CustomSimpleAdapter(this, cursor, CursorAdapter.FLAG_AUTO_REQUERY);
        notificationListView.setAdapter(notificationsListAdapter);
    }


    private void addNewNotificationButtonListener() {
        buttonNewNotification = findViewById(R.id.timeButton);
        buttonNewNotification.setOnClickListener(view -> new TimePickerDialog(this, timePickerListener, 0, 0, true).show());
    }

    private void addNotification(int hour, int minute) {
        NotificationDatabaseHelper notificationDatabaseHelper = NotificationDatabaseHelper.getInstance(this);
        if (!notificationDatabaseHelper.insertNotification(hour, minute)) {
            Log.e(DATABASE_TAG, "Error inserting in database");
        }
        Log.i(DATABASE_TAG, "Notification inserted successfully");
    }


    private void requestInternetService() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case INTERNET_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setMessage("We cannot provide the weather information without internet access!")
                            .setTitle("Error Weather")
                            .create();
                    alertDialog.show();
                }

        }
    }


//    private void getData() {
//        WeatherApiCall weatherApiCall = new WeatherApiCall();
//        String weatherInfo = weatherApiCall.getWeatherInfo("weather", "Iasi,ro", "metric");
//        try {
//            JSONObject weatherApiResult = new JSONObject(weatherInfo);
//            TextView temperature = findViewById(R.id.temperatureLabel);
//            CharSequence labelText = temperature.getText();
//            temperatureText = labelText + " " + weatherApiResult.getJSONObject("main").getDouble("temp") + " \u00B0C";
//            temperature.setText(temperatureText);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void getNotification() {
//
//
//    }
}
