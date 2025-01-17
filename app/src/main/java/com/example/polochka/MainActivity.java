package com.example.polochka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.polochka.Fragments.FavoriteFragment;
import com.example.polochka.Fragments.MainFragment;
import com.example.polochka.Fragments.MessagesFragment;
import com.example.polochka.Fragments.NewItemFragment;
import com.example.polochka.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        // Инициализация элементов интерфейса
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Установить начальный фрагмент
        setUpInitialFragment();

        // Настройка BottomNavigationView
        setUpBottomNavigation();
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
            case R.id.nav_messages:
                return new MessagesFragment();
            case R.id.nav_new_item:
                return new NewItemFragment();
            case R.id.nav_profile:
                return new ProfileFragment();
            case R.id.nav_saved:
                return new FavoriteFragment();
        }
        return null;
    }

    private void setUpInitialFragment() {
        // Устанавливаем начальный фрагмент
        makeCurrentFragment(new MainFragment());
    }

    private void makeCurrentFragment(Fragment fragment) {
        // Метод для замены фрагментов
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_wrapper, fragment); // Контейнер для фрагментов
        fragmentTransaction.commit();
    }
}
