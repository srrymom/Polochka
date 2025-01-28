package com.example.polochka.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.polochka.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageSender {

    private Context context;
    private Activity activity;
    private  String SERVER_URL;


    public ImageSender(Context context,Activity activity) {
        this.context = context;
        this.activity = activity;
        SERVER_URL = context.getString(R.string.server_url);

    }

    public void sendImageToServer(Uri imageUri, String bookId) {
        try {
            // Получение ContentResolver для работы с URI
            ContentResolver contentResolver = context.getContentResolver();

            // Чтение изображения как Bitmap
            InputStream inputStream = contentResolver.openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Изменение размера изображения (например, 800x800)
            int maxWidth = 1200;
            int maxHeight = 1200;

            Bitmap resizedBitmap = resizeBitmap(originalBitmap, maxWidth, maxHeight);

            // Сжатие изображения в файл (JPEG, качество 85%)
            File tempFile = new File(context.getCacheDir(), "compressed_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.close();

            // Подготовка файла для отправки
            OkHttpClient client = new OkHttpClient();

            RequestBody fileBody = RequestBody.create(tempFile, MediaType.parse("image/jpeg"));
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", tempFile.getName(), fileBody)
                    .addFormDataPart("book_id", bookId)  // добавляем ID книги

                    .build();

            Request request = new Request.Builder()
                    .url(SERVER_URL + "/upload")
                    .post(requestBody)
                    .build();

            // Выполнение HTTP-запроса
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("NewItemFragment", "Ошибка при отправке изображения на сервер", e);
                    activity.runOnUiThread(() ->
                            Toast.makeText(context, "Ошибка отправки: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        activity.runOnUiThread(() ->
                                Toast.makeText(context, "Изображение успешно отправлено!", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        activity.runOnUiThread(() ->
                                Toast.makeText(context, "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });

        } catch (Exception e) {
            Log.e("NewItemFragment", "Ошибка при обработке изображения", e);
            Toast.makeText(context, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private Bitmap resizeBitmap(Bitmap originalBitmap, int maxWidth, int maxHeight) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        float aspectRatio = (float) width / height;

        // Рассчитать новые размеры
        if (width > maxWidth || height > maxHeight) {
            if (aspectRatio > 1) {
                // Ширина больше высоты
                width = maxWidth;
                height = (int) (maxWidth / aspectRatio);
            } else {
                // Высота больше ширины
                height = maxHeight;
                width = (int) (maxHeight * aspectRatio);
            }
        }

        // Создать новый Bitmap с изменённым размером
        return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
    }

}
