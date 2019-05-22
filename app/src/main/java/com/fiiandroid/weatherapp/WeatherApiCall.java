package com.fiiandroid.weatherapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherApiCall {

    private static final String baseURL = "https://api.openweathermap.org/data/2.5/";

    public String getWeatherResult(String... params) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = HttpUrl.get(baseURL)
                .newBuilder()
                .addPathSegment(params[0])
                .addQueryParameter("lat", params[1])
                .addQueryParameter("lon", params[2])
                .addQueryParameter("units", params[3])
                .addQueryParameter("appid", BuildConfig.weatherAPIKey)
                .build();

        Request request = new Request.Builder()
                .get()
                .url(httpUrl)
                .build();
        Log.i("REQUEST", request.url().toString());

        try (Response response = client.newCall(request).execute()) {
            String responseString = response.body().string();
            Log.i("RESPONSE", responseString);
            return responseString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
