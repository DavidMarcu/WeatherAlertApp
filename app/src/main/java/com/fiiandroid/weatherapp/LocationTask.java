package com.fiiandroid.weatherapp;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LocationTask extends AsyncTask<FusedLocationProviderClient, Void, JSONObject> {

    @SuppressLint("MissingPermission")
    @Override
    protected JSONObject doInBackground(FusedLocationProviderClient... locationProviderClients) {
        WeatherApiCall weatherApiCall = new WeatherApiCall();
        Task<Location> locationTask = locationProviderClients[0].getLastLocation();
        try {
            Location location = Tasks.await(locationTask);
            weatherApiCall.execute("weather", String.format(Locale.getDefault(), "%.2f", location.getLatitude()),
                    String.format(Locale.getDefault(), "%.2f", location.getLongitude()), "metric");
            return new JSONObject(weatherApiCall.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}


