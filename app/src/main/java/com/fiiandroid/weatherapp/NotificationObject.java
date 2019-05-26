package com.fiiandroid.weatherapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationObject extends BroadcastReceiver {

    private static final String NOTIFICATION_CHANNEL_ID = "notification_01";
    private static final int NOTIFICATION_ID = 432681;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context, intent);
    }

    private void createNotification(Context context, Intent fromIntent){
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Weather Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            nm.createNotificationChannel(notificationChannel);
        }
//        Intent intent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        WeatherApiCall weatherApiCall = new WeatherApiCall();
        String weatherInfo = weatherApiCall.getWeatherInfo("weather", "Iasi,ro", "metric");
        String temperatureText = null;
        JSONObject weatherApiResult;
        try {
            weatherApiResult = new JSONObject(weatherInfo);
            temperatureText = "Temperature: " + weatherApiResult.getJSONObject("main").getDouble("temp") + " \u00B0C";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.sun)
                .setContentTitle(temperatureText)
                .setContentText(temperatureText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        nm.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
