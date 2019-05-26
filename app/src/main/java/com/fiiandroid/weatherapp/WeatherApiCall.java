package com.fiiandroid.weatherapp;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherApiCall{

    private static final String baseURL = "https://api.openweathermap.org/data/2.5/";

    public String getWeatherInfo(String... params) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = HttpUrl.get(baseURL)
                .newBuilder()
                .addPathSegment(params[0])
                .addQueryParameter("q", params[1])
                .addQueryParameter("units", params[2])
                .addQueryParameter("appid", BuildConfig.weatherAPIKey)
                .build();

        Request request = new Request.Builder()
                .get()
                .url(httpUrl)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
