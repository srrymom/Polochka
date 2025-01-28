package com.example.polochka.utils;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ServerCommunicator {
    private final String serverUrl;
    private final OkHttpClient client;

    public ServerCommunicator(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = new OkHttpClient();
    }

    public void sendBook(JSONObject payload, Callback callback) {
        RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(serverUrl + "/books")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
