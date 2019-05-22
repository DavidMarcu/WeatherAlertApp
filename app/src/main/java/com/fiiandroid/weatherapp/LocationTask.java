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

public class LocationTask{

//    private JSONObject weatherResult;
//    private Location currentLocation;
//
//    @SuppressLint("MissingPermission")
//    @Override
//    protected JSONObject doInBackground(FusedLocationProviderClient... locationProviderClients) {
//        WeatherApiCall weatherApiCall = new WeatherApiCall();
//        Task<Location> weatherTask = locationProviderClients[0].getLastLocation()
//                .addOnSuccessListener(new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if(location != null){
//                            currentLocation = location;
//                    }
//                });
//
//        try {
//            Tasks.await(weatherTask);
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        return weatherResult;
//    }
}


