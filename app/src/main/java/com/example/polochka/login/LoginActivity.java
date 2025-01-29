package com.example.polochka.login;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.polochka.Fragments.MainFragment;
import com.example.polochka.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setUpInitialFragment();


    }

    private void toLogin() {
        // Устанавливаем начальный фрагмент
        makeCurrentFragment(new LoginFragment());
    }

    private void toRegistration() {
        // Устанавливаем начальный фрагмент
        makeCurrentFragment(new RegisterFragment());
    }
    private void setUpInitialFragment() {
        // Устанавливаем начальный фрагмент
        makeCurrentFragment(new RegisterFragment());
    }
    public void makeCurrentFragment(Fragment fragment) {
        // Метод для замены фрагментов
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_login_wrapper, fragment); // Контейнер для фрагментов
        fragmentTransaction.addToBackStack(null); // Add to back stack
        fragmentTransaction.commit();
    }
}