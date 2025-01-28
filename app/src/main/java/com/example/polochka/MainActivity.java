package com.example.polochka;

import static java.security.AccessController.getContext;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import com.example.polochka.Fragments.FavoriteFragment;
import com.example.polochka.Fragments.MainFragment;
import com.example.polochka.Fragments.NewItemFragment;
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
        super.onCreate(savedInstanceState);

        String apiKey = "6d4f46d0-123a-47c0-b6f1-e082e11a9472"; // todo: спрятать ключ
        MapKitFactory.setApiKey(apiKey);


        setContentView(R.layout.main_page);

        // Инициализация элементов интерфейса
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Установить начальный фрагмент
        setUpInitialFragment();

        // Настройка BottomNavigationView
        setUpBottomNavigation();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Check if the fragment stack has entries
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack(); // Go back to the previous fragment
            }
//            else {
//                // Optionally, finish the activity or perform other actions
//                backPressed(); // or finish() to close the Activity
//            }
        });


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
                return new NewItemFragment();
//            case R.id.nav_profile:
//                return new ProfileFragment();
            case R.id.nav_saved:
                return new FavoriteFragment();
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
