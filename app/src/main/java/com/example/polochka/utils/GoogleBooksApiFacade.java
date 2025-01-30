package com.example.polochka.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.polochka.R;
import com.example.polochka.models.GoogleBook;
import com.example.polochka.models.ItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleBooksApiFacade {
    private final String SEARCH_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private String apiKey;
    private final OkHttpClient client;
    private static final String TAG = "GoogleBooksApiFacade";

    public GoogleBooksApiFacade(Context context) {

        this.apiKey = context.getString(R.string.google_api_key);
        this.client = new OkHttpClient();
    }

    public void findBooks(String prompt, Activity activity, Context context, Consumer<ArrayList<GoogleBook>> consumer) {
        String requestUrl = "https://www.googleapis.com/books/v1/volumes?q=" + Uri.encode(prompt) + "&key=" + apiKey;
        fetchBooks(requestUrl, activity, context, consumer);
    }

    private void fetchBooks(String url, Activity activity, Context context, Consumer<ArrayList<GoogleBook>> consumer) {
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
                        ArrayList<GoogleBook> books = parseBooks(responseBody);

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


    private ArrayList<GoogleBook> parseBooks(String responseBody) throws JSONException {
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray booksArray = jsonResponse.getJSONArray("items");

        ArrayList<GoogleBook> books = new ArrayList<>();
        for (int i = 0; i < booksArray.length(); i++) {
            JSONObject bookObject = booksArray.getJSONObject(i);
            JSONObject volumeInfo = bookObject.getJSONObject("volumeInfo");
            JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");

            try {
                String thumbnail = imageLinks != null ? imageLinks.optString("thumbnail", null) : null;
                String author = volumeInfo.optJSONArray("authors") != null && volumeInfo.getJSONArray("authors").length() > 0
                        ? volumeInfo.getJSONArray("authors").getString(0) : "Unknown";
                String description = volumeInfo.optString("description", "No description available");
                String title = volumeInfo.optString("title", "Untitled");

                books.add(new GoogleBook(thumbnail, author, description, title));
            } catch (Exception e) {
                Log.e("ParseBooksError", "Error parsing book at index " + i, e);
            }
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