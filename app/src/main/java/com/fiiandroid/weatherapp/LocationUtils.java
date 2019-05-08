package com.fiiandroid.weatherapp;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class LocationUtils{

    private LocationManager locationManager;
    private JSONObject response;
    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            WeatherApiCall weatherApiCall = new WeatherApiCall();
            String weatherResponseString = weatherApiCall.requestWeatherInfo("weather", String.format(Locale.getDefault(), "%.2f", location.getLatitude()),
                                String.format(Locale.getDefault(), "%.2f", location.getLongitude()), "metric");
            try {
                response = new JSONObject(weatherResponseString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public LocationUtils(LocationManager locationManager){
        this.locationManager = locationManager;
    }

    @SuppressLint("MissingPermission")
    public JSONObject provideLocation(){
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new MyLocationListener(), null);
        return response;
    }

    //    @SuppressLint("MissingPermission")
//    @Override
//    protected JSONObject doInBackground(FusedLocationProviderClient... locationProviderClients) {
//        WeatherApiCall weatherApiCall = new WeatherApiCall();
//        locationProviderClients[0].getLastLocation()
//                .addOnSuccessListener(location -> {
//                    if(location!=null){
//                        Log.d("LOCATION", String.valueOf(location.getLatitude()));
//                        weatherApiCall.execute("weather", String.format(Locale.getDefault(), "%.2f", location.getLatitude()),
//                                String.format(Locale.getDefault(), "%.2f", location.getLongitude()), "metric");
//                    }
//                });
//        try {
//            return new JSONObject(weatherApiCall.get());
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}


