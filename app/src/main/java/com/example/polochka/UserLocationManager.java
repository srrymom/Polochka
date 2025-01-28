package com.example.polochka;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
;

public class UserLocationManager {

    private static final String TAG = "UserLocationManager";

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;


    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);

        void onLocationError(String errorMessage);
    }

    public UserLocationManager(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getUserLocation(Activity activity, LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем разрешение
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                callback.onLocationReceived(location.getLatitude(), location.getLongitude());
            } else {
                callback.onLocationError("Не удалось получить местоположение");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Ошибка получения местоположения", e);
            callback.onLocationError("Ошибка получения местоположения: " + e.getMessage());
        });
    }
}