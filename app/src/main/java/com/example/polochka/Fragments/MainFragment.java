package com.example.polochka.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polochka.MainActivity;
import com.example.polochka.models.ItemModel;
import com.example.polochka.adapters.ItemRecyclerViewAdapter;
import com.example.polochka.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainFragment extends Fragment implements ItemRecyclerViewAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ArrayList<ItemModel> items = new ArrayList<>();
    private  String SERVER_URL;
    private ItemRecyclerViewAdapter adapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        SERVER_URL = getContext().getString(R.string.server_url);

        // Инициализация фрагмента
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);



        recyclerView = view.findViewById(R.id.favoriteRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Запрос на получение списка книг с сервера
        getBooksFromServer();

        return view;
    }

    // Метод для получения книг с сервера
    private void getBooksFromServer() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL)  // Здесь указываете URL вашего API
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Логирование ошибки для отладки
                Log.e("MainFragment", "Ошибка при отправке запроса на сервер", e);

                // Показать сообщение об ошибке в Toast
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ошибка при получении данных", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Если запрос успешен
                if (response.isSuccessful()) {

                    try {
                        // Получаем тело ответа в виде строки
                        String responseBody = response.body().string();

                        // Разбираем JSON-ответ
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray booksArray = jsonResponse.getJSONArray("books");

                        // Обновляем список книг
                        ArrayList<ItemModel> newItems = new ArrayList<>();
                        for (int i = 0; i < booksArray.length(); i++) {
                            JSONObject bookObject = booksArray.getJSONObject(i);
                            String title = bookObject.getString("title");
                            String author = bookObject.getString("author");  // Или другой ключ, если у вас другая структура

                            // Добавляем в список
                            newItems.add(new ItemModel(title, author));
                        }

                        // Обновляем интерфейс с новыми данными
                        getActivity().runOnUiThread(() -> setUpItems(newItems));

                    } catch (Exception e) {
                        Log.e("MainFragment", "Ошибка при разборе ответа", e);
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Ошибка при разборе данных", Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    // Ошибка на сервере
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    // Метод для обновления адаптера с новыми данными
    private void setUpItems(ArrayList<ItemModel> newItems) {
        items.clear();
        items.addAll(newItems);

        // Инициализация RecyclerView
        adapter = new ItemRecyclerViewAdapter(getContext(), items, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(ItemModel item) {
        ProductDetailsFragment fragment = ProductDetailsFragment.newInstance(item);
        if (getActivity() != null) {
            ((MainActivity) getActivity()).makeCurrentFragment(fragment);
        }
    }}
