package com.example.polochka.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.polochka.views.CustomMapView;
import com.example.polochka.LocationDetailsListener;
import com.example.polochka.R;
import com.yandex.mapkit.MapKitFactory;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewItemFragment extends Fragment implements LocationDetailsListener {

    private EditText userNameInput, userNumberInput, titleInput, authorInput, descriptionInput;
    private TextView cityLabel, districtLabel;
    private Button submitButton;;
    private CustomMapView mapView;
    private  String SERVER_URL;

    public NewItemFragment() {
        // Required empty public constructor
    }
    public static NewItemFragment newInstance(String param1, String param2) {
        NewItemFragment fragment = new NewItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MapKitFactory.initialize(getContext());
        SERVER_URL = getContext().getString(R.string.server_url);



        View view = inflater.inflate(R.layout.fragment_new_item, container, false);


        mapView = view.findViewById(R.id.mapview);
        mapView.start(requireContext(), requireActivity(), this);

        // Initialize input fields and button
        titleInput = view.findViewById(R.id.bookTitleInput);
        authorInput = view.findViewById(R.id.bookAuthorInput);
        descriptionInput = view.findViewById(R.id.bookDescriptionInput);
        submitButton = view.findViewById(R.id.itemMessageBtn);
        cityLabel = view.findViewById(R.id.cityLabel);
        districtLabel = view.findViewById(R.id.districtLabel);
        userNumberInput = view.findViewById(R.id.userNumberInput);
        userNameInput = view.findViewById(R.id.userNameInput);

        // Set up button click listener
        submitButton.setOnClickListener(v -> sendBookToServer());

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        // Start MapKit and the map view lifecycle
        MapKitFactory.getInstance().onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    public void onStop() {
        // Stop MapKit and the map view lifecycle
        if (mapView != null) {
            mapView.onStop();
        }
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
    private void sendBookToServer() {
        String title = titleInput.getText().toString().trim();
        String author = authorInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String city = cityLabel.getText().toString().trim();
        String district = districtLabel.getText().toString().trim();
        String userName = userNameInput.getText().toString().trim();
        String userNumber = userNumberInput.getText().toString().trim();
        double latitude = mapView.getLatitude();
        double longitude = mapView.getLongitude();
        Log.e("longitude", String.valueOf(longitude));

        // Check if any fields are empty
        if (title.isEmpty() || author.isEmpty() || description.isEmpty() || userName.isEmpty() || userNumber.isEmpty()) {
            Toast.makeText(getContext(), "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonPayload = new JSONObject();
        try {
            jsonPayload.put("title", title);
            jsonPayload.put("author", author);
            jsonPayload.put("description", description);
            jsonPayload.put("city", city);
            jsonPayload.put("district", district);
            jsonPayload.put("username", userName);
            jsonPayload.put("phone_number", userNumber);
            jsonPayload.put("latitude", latitude);
            jsonPayload.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Send data to the server
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(String.valueOf(jsonPayload), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log error for debugging
                Log.e("NewItemFragment", "Ошибка при отправке данных на сервер", e);

                // Show error in Toast
                getActivity().runOnUiThread(() -> {
                    String errorMessage = "Ошибка отправки данных на сервер: " + e.getMessage();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle server response
                if (response.isSuccessful()) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Книга успешно отправлена!", Toast.LENGTH_SHORT).show());
                } else {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
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
