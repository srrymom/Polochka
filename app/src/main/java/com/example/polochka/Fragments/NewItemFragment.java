package com.example.polochka.Fragments;



import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.polochka.utils.ImageSender;
import com.example.polochka.utils.ServerCommunicator;
import com.example.polochka.views.CustomMapView;
import com.example.polochka.utils.LocationDetailsListener;
import com.example.polochka.R;
import com.yandex.mapkit.MapKitFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewItemFragment extends Fragment implements LocationDetailsListener {
    private EditText  userNumberInput, titleInput, authorInput, descriptionInput;
    private TextView cityLabel, districtLabel;
    private Button submitButton, btnPickImage;
    private ImageView previewImage;
    private CustomMapView mapView;

    private ServerCommunicator serverCommunicator;

    private ImageSender imageSender;

    public void setImageUri(Uri image_uri) {
        this.imageUri = image_uri;
    }

    private Uri imageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        MapKitFactory.initialize(getContext());
        View view = inflater.inflate(R.layout.fragment_new_item, container, false);

        // Инициализация
        initializeViews(view);
        serverCommunicator = new ServerCommunicator(requireContext());
        ImageHandler imageHandler = new ImageHandler(this, previewImage, this::setImageUri);

        imageSender = new ImageSender(requireContext(), requireActivity());

        // Установка обработчиков
        submitButton.setOnClickListener(v -> sendBookToServer());
        btnPickImage.setOnClickListener(v -> imageHandler.pickImage());

        mapView.start(requireContext(), requireActivity(), this);

        return view;

    }

    private void initializeViews(View view) {
        titleInput = view.findViewById(R.id.bookTitleInput);
        authorInput = view.findViewById(R.id.bookAuthorInput);
        descriptionInput = view.findViewById(R.id.bookDescriptionInput);
        submitButton = view.findViewById(R.id.itemMessageBtn);
        cityLabel = view.findViewById(R.id.cityLabel);
        districtLabel = view.findViewById(R.id.districtLabel);
        userNumberInput = view.findViewById(R.id.userNumberInput);
        btnPickImage = view.findViewById(R.id.addPicButton);
        previewImage = view.findViewById(R.id.imagePreview);
        mapView = view.findViewById(R.id.mapview);
    }


    private void sendBookToServer() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("username", null);

        // Сбор данных
        String title = titleInput.getText().toString().trim();
        String author = authorInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String city = cityLabel.getText().toString().trim();
        String district = districtLabel.getText().toString().trim();
        String userNumber = userNumberInput.getText().toString().trim();
        double latitude = mapView.getLatitude();
        double longitude = mapView.getLongitude();

        if (validateInputs(title, author, description, userNumber)) {
            Toast.makeText(getContext(), "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject payload = new JSONObject();
            payload.put("title", title);
            payload.put("author", author);
            payload.put("description", description);
            payload.put("city", city);
            payload.put("district", district);
            payload.put("username", userName);
            payload.put("phone_number", userNumber);
            payload.put("latitude", latitude);
            payload.put("longitude", longitude);

            // Отправка данных
            serverCommunicator.sendBook(payload, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handleServerError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    handleServerResponse(response);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean validateInputs(String... inputs) {
        for (String input : inputs) {
            if (input.isEmpty()) return true;
        }
        return false;
    }

    private void handleServerError(IOException e) {
        getActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), "Ошибка отправки данных: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }

    private void handleServerResponse(Response response) {
        getActivity().runOnUiThread(() -> {
            String message = response.isSuccessful() ? "Книга успешно отправлена!" : "Ошибка сервера: " + response.code();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        try {
            // Извлекаем JSON-ответ
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            // Получаем book_id из ответа
            String bookId = jsonResponse.getString("book_id");

            if (imageUri != null) {
                // Отправляем изображение на сервер
                imageSender.sendImageToServer(imageUri, bookId);
            }


        } catch (Exception e) {
            Log.e("NewItemFragment", "Ошибка при обработке ответа", e);
            Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();}

    }


    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onCityChanged(String city) {
        cityLabel.setText(city);
    }

    @Override
    public void onDistrictChanged(String district) {
        districtLabel.setText(district);
    }
}
