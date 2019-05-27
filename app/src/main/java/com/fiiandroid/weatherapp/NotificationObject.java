package com.fiiandroid.weatherapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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

    private void createNotification(Context context, Intent fromIntent) {
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

        WeatherApiCall weatherApiCall = new WeatherApiCall();
        String temperatureText = null;
        String windSpeed = null;
        String clouds = null;
        String weatherDescription = null;
        String weatherTitle = null;
        String pressure = null;
        JSONObject weatherApiResult;
        try {
            String weatherInfo = weatherApiCall.execute("weather", "Iasi,ro", "metric").get();
            weatherApiResult = new JSONObject(weatherInfo);
            temperatureText = "Temperature: " + weatherApiResult.getJSONObject("main").getDouble("temp") + " \u00B0C";
            pressure = weatherApiResult.getJSONObject("main").getInt("pressure") + " hPa";
            JSONObject weatherDetails = (JSONObject)weatherApiResult.getJSONArray("weather").get(0);
            weatherTitle = weatherDetails.getString("main");
            weatherDescription = weatherDetails.getString("description");
            clouds = weatherApiResult.getJSONObject("clouds").getInt("all")+"%";
            windSpeed = weatherApiResult.getJSONObject("wind").getDouble("speed") + "km/h";
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.sun)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Weather report: ")
                .setContentText("Weather: " + weatherTitle + " --- " + temperatureText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(temperatureText + "\n" + "Weather: " + weatherDescription + "\n" + "Clouds: " + clouds + "\n" + "Wind speed: "  + windSpeed + "\n"
                                + "Pressure: " + pressure))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        nm.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
