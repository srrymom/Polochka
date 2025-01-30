package com.example.polochka.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.polochka.R;
import com.example.polochka.models.ItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerCommunicator {
    private final String serverUrl;
    private final OkHttpClient client;
    private static final String TAG = "ServerCommunicator";

    public ServerCommunicator(Context context) {

        this.serverUrl = context.getString(R.string.server_url);
        this.client = new OkHttpClient();
    }
    public void regUser(JSONObject payload, Callback callback) {
        RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                    .url(serverUrl + "/registration")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void authUser(JSONObject payload, Callback callback) {
        RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(serverUrl + "/auth")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
    public void sendBook(JSONObject payload, Callback callback) {
        RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(serverUrl + "/books")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void getBooksFromServer(Activity activity, Context context, Consumer<ArrayList<ItemModel>> consumer) {
        fetchBooks(serverUrl + "/books", activity, context, consumer);
    }

    public void getUserBooks(String username, Activity activity, Context context, Consumer<ArrayList<ItemModel>> consumer) {
        fetchBooks(serverUrl + "/books?username=" + username, activity, context, consumer);
    }

    private void fetchBooks(String url, Activity activity, Context context, Consumer<ArrayList<ItemModel>> consumer) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logError("Ошибка при отправке запроса", e);
                showToast(activity, context, "Ошибка при получении данных");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 204) {
                    return;
                }

                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        ArrayList<ItemModel> books = parseBooks(responseBody);

                        activity.runOnUiThread(() -> consumer.accept(books));

                    } catch (Exception e) {
                        logError("Ошибка при разборе ответа", e);
                        showToast(activity, context, "Ошибка при разборе данных");
                    }
                } else {
                    showToast(activity, context, "Ошибка сервера: " + response.code());
                }
            }
        });
    }

    private ArrayList<ItemModel> parseBooks(String responseBody) throws JSONException {
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray booksArray = jsonResponse.getJSONArray("books");

        ArrayList<ItemModel> books = new ArrayList<>();
        for (int i = 0; i < booksArray.length(); i++) {
            JSONObject bookObject = booksArray.getJSONObject(i);

            books.add(new ItemModel(
                    bookObject.getInt("id"),
                    bookObject.getString("title"),
                    bookObject.getString("author"),
                    bookObject.getString("description"),
                    bookObject.getString("city"),
                    bookObject.optString("district", null),
                    bookObject.getDouble("latitude"),
                    bookObject.getDouble("longitude"),
                    bookObject.getString("phone_number"),
                    bookObject.optString("username", null),
                    String.valueOf(bookObject.getInt("id"))
            ));
        }
        return books;
    }

    private void showToast(Activity activity, Context context, String message) {
        activity.runOnUiThread(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }

    private void logError(String message, Exception e) {
        Log.e(TAG, message, e);
    }

}
