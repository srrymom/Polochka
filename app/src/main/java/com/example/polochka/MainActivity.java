package com.example.polochka;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import com.example.polochka.Fragments.ChooseBookFragment;
import com.example.polochka.Fragments.MainFragment;
import com.example.polochka.Fragments.NewItemFragment;
import com.example.polochka.Fragments.ProfileFragment;
import com.example.polochka.login.LoginActivity;
import com.example.polochka.utils.UserLocationManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yandex.mapkit.MapKitFactory;

public class MainActivity extends AppCompatActivity  {

    private BottomNavigationView bottomNavigationView;
    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    private boolean isLocationPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Запрос разрешений
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_LOCATION_PERMISSION
            );
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserLocationManager locationManager = new UserLocationManager(this);
        locationManager.getUserLocation(this, null);

        super.onCreate(savedInstanceState);

        String apiKey = "6d4f46d0-123a-47c0-b6f1-e082e11a9472"; // todo: спрятать ключ
        MapKitFactory.setApiKey(apiKey);


        setContentView(R.layout.main_page);

        // Инициализация элементов интерфейса
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Установка начального фрагмента
        setUpInitialFragment();

        // Настройка BottomNavigationView
        setUpBottomNavigation();

        // Обработка кнопки "Назад"
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Проверяем, если в стеке фрагментов есть записи, то возвращаемся назад
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack(); // Возвращаемся на предыдущий фрагмент
            } else {
                finish(); // Закрываем активность, если в стеке нет фрагментов
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = this.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        // Проверка, если пользователь не залогинен, переходим на экран входа
        if (!(isLoggedIn && username != null)) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            finish(); // Можно завершить текущую активность, чтобы пользователь не мог вернуться
            return; // Дальше код не выполняется
        }
    }

    private void setUpBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentById(item.getItemId());
            if (selectedFragment != null) {
                makeCurrentFragment(selectedFragment);
            }
            return true;
        });
    }

    private Fragment getFragmentById(int itemId) {
        switch (itemId) {
            case R.id.nav_main:
                return new MainFragment();
            case R.id.nav_new_item:
                return new ChooseBookFragment();
            case R.id.nav_profile:
                return new ProfileFragment();
//            case R.id.nav_saved:
//                return new FavoriteFragment();
        }
        return null;
    }

    private void setUpInitialFragment() {
        // Устанавливаем начальный фрагмент
        makeCurrentFragment(new MainFragment());
    }

    public void makeCurrentFragment(Fragment fragment) {
        // Метод для замены фрагментов
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_wrapper, fragment); // Контейнер для фрагментов
        fragmentTransaction.addToBackStack(null); // Add to back stack
        fragmentTransaction.commit();
    }

}
