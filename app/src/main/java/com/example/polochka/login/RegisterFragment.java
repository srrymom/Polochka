package com.example.polochka.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.polochka.MainActivity;
import com.example.polochka.R;
import com.example.polochka.utils.ServerCommunicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterFragment extends Fragment {

    private EditText loginInput;
    private EditText passwordInput;
    private EditText passwordAgainInput;
    private Button inputButton;
    private Button toLoginButton;

    private ServerCommunicator serverCommunicator;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        serverCommunicator = new ServerCommunicator(requireContext());

        loginInput = view.findViewById(R.id.loginInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        passwordAgainInput = view.findViewById(R.id.passwordAgainInput);
        inputButton = view.findViewById(R.id.inputButton);
        toLoginButton = view.findViewById(R.id.toLoginButton);

        inputButton.setOnClickListener(v -> registerUser());
        toLoginButton.setOnClickListener(v -> toLogin());

        return view;
    }
    private void toLogin() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fl_login_wrapper, new LoginFragment())
                .addToBackStack(null) // Добавляет в стек возврата (необязательно)
                .commit();

    }
    private void registerUser() {
        String login = loginInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String passwordAgain = passwordAgainInput.getText().toString().trim();

        if (TextUtils.isEmpty(login)) {
            loginInput.setError("Введите логин");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Введите пароль");
            return;
        }

        if (TextUtils.isEmpty(passwordAgain)) {
            passwordAgainInput.setError("Повторите пароль");
            return;
        }

        if (!password.equals(passwordAgain)) {
            passwordAgainInput.setError("Пароли не совпадают");
            return;
        }

        try {
            JSONObject payload = new JSONObject();
            payload.put("username", login);
            payload.put("password", password);


            // Отправка данных
            serverCommunicator.regUser(payload, new Callback() {
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


    private void handleServerError(IOException e) {
        getActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), "Ошибка отправки данных: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }

    private void handleServerResponse(Response response) throws IOException {
        if (response.isSuccessful()){

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", loginInput.getText().toString().trim());
            editor.putBoolean("is_logged_in", true);
            editor.apply();

            Intent myIntent = new Intent(requireContext(), MainActivity.class);
            startActivity(myIntent);

        }
        else{
            // Чтение тела ошибки
            String errorResponse = response.body() != null ? response.body().string() : "{}";

            // Парсим JSON ответ
            try {
                JSONObject errorJson = new JSONObject(errorResponse);
                String errorMessage = errorJson.optString("detail", "Неизвестная ошибка");
                int statusCode = errorJson.optInt("status_code", 0);

                // Показать ошибку в Toast
                String toastMessage = "Ошибка при регистрации: " + errorMessage + " (код: " + statusCode + ")";
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
                });
            } catch (JSONException e) {
                // Если не удается разобрать JSON
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Ошибка при обработке ответа", Toast.LENGTH_SHORT).show();
                });
        }


    }

}}
