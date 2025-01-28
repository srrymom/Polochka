package com.example.polochka.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.polochka.LocationDetailsListener;
import com.example.polochka.UserLocationManager;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class CustomMapView extends MapView implements CameraListener {
    private UserLocationManager locationManager;
    private Activity activity;
    private MapObjectCollection mapObjects;
    private PlacemarkMapObject placemark;
    private final OkHttpClient httpClient = new OkHttpClient();

    private LocationDetailsListener listener;

    public void setLocationDetailsListener(LocationDetailsListener listener) {
        this.listener = listener;
    }

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                System.out.println("unlocked");
                this.getParent().requestDisallowInterceptTouchEvent(false);
                fetchLocationDetails(getMap().getCameraPosition().getTarget().getLatitude(), getMap().getCameraPosition().getTarget().getLongitude());

                break;
            case MotionEvent.ACTION_DOWN:
                System.out.println("locked");
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public double getLatitude() {
        return getMap().getCameraPosition().getTarget().getLatitude();
    }
    public double getLongitude() {
        return getMap().getCameraPosition().getTarget().getLongitude();
    }

    private void fetchLocationDetails(double latitude, double longitude) {
        String apiKey = "ca9c3475-3e76-4ec9-bb31-bcfb3a43c18f"; // Замените своим API-ключом
        String baseUrl = "https://geocode-maps.yandex.ru/1.x/?apikey=" + apiKey + "&geocode=" + longitude + "," + latitude + "&format=json&results=1";

        // Запрос для района (district)
        String districtUrl = baseUrl + "&kind=district";
        Request districtRequest = new Request.Builder().url(districtUrl).build();
        Call districtCall = httpClient.newCall(districtRequest);

        // Запрос для города (locality)
        String localityUrl = baseUrl + "&kind=locality";
        Request localityRequest = new Request.Builder().url(localityUrl).build();
        Call localityCall = httpClient.newCall(localityRequest);

        // Выполнение запросов асинхронно
        districtCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("LocationDetails", "Ошибка при запросе района: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray featureMemberArray = jsonResponse
                                .getJSONObject("response")
                                .getJSONObject("GeoObjectCollection")
                                .getJSONArray("featureMember");
                        Log.e("LocationDetails", String.valueOf(featureMemberArray));
                        String district;
                        if (featureMemberArray.length() > 0) {
                            JSONObject geoObject = featureMemberArray.getJSONObject(0).getJSONObject("GeoObject");
                            district = geoObject
                                    .getString("name"); // Получаем название района

                        } else {
                            district = null;
                        }

                        activity.runOnUiThread(() -> listener.onDistrictChanged(district));

                    } catch (Exception e) {
                        Log.e("LocationDetails", "Ошибка парсинга района: " + e.getMessage());
                    }
                } else {
                    Log.e("LocationDetails", "Неудачный ответ сервера для района: " + response.message());
                }
            }
        });

        // Обработка запроса для города
        localityCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("LocationDetails", "Ошибка при запросе города: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray featureMemberArray = jsonResponse
                                .getJSONObject("response")
                                .getJSONObject("GeoObjectCollection")
                                .getJSONArray("featureMember");

                        String city;
                        if (featureMemberArray.length() > 0) {
                            JSONObject geoObject = featureMemberArray.getJSONObject(0).getJSONObject("GeoObject");
                            city = geoObject
                                    .getString("name");


                        } else {
                            city = null;
                        }

                        // Обновляем поле для города, если оно получено
                        activity.runOnUiThread(() -> listener.onCityChanged(city));

                    } catch (Exception e) {
                        Log.e("LocationDetails", "Ошибка парсинга города: " + e.getMessage());
                    }
                } else {
                    Log.e("LocationDetails", "Неудачный ответ сервера для города: " + response.message());
                }
            }
        });
    }


    public void start(Context context, Activity activity, LocationDetailsListener locationDetailsListener) {

        this.listener = locationDetailsListener;
        this.activity = activity;
        mapObjects = getMapWindow().getMap().getMapObjects();
        // Инициализация менеджера геолокации
        locationManager = new UserLocationManager(context);
        requestUserLocation();

    }


    private void requestUserLocation() {
        locationManager.getUserLocation(activity, new UserLocationManager.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                Log.d("UserLocation", "Координаты получены: широта = " + latitude + ", долгота = " + longitude);

                // Обрабатываем координаты
                Point userPoint = new Point(latitude, longitude);
                placemark = mapObjects.addPlacemark(userPoint);
                getMap().move(new CameraPosition(userPoint, 17.0f, 0.0f, 0.0f));
                getMap().addCameraListener(CustomMapView.this);
                fetchLocationDetails(getMap().getCameraPosition().getTarget().getLatitude(), getMap().getCameraPosition().getTarget().getLongitude());

            }

            @Override
            public void onLocationError(String errorMessage) {
                Log.e("UserLocation", "Ошибка при получении местоположения: " + errorMessage);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCameraPositionChanged(@NonNull com.yandex.mapkit.map.Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason cameraUpdateReason, boolean b) {
        if (placemark != null) {
            placemark.setGeometry(cameraPosition.getTarget());
        }


    }
}