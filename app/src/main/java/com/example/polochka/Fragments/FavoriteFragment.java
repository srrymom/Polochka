package com.example.polochka.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polochka.MainActivity;
import com.example.polochka.models.ItemModel;
import com.example.polochka.adapters.ItemRecyclerViewAdapter;
import com.example.polochka.R;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment  implements ItemRecyclerViewAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ArrayList<ItemModel> favoriteItems;

    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Инициализация фрагмента
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.favoriteRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Инициализация списка избранных элементов
        favoriteItems = setUpFavoriteItems();


        // Установка адаптера
        ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(getContext(), favoriteItems, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    // Метод для добавления элементов в список избранных
    private ArrayList<ItemModel> setUpFavoriteItems() {
        ArrayList<ItemModel> models = new ArrayList<>();
        String[] userNames = getResources().getStringArray(R.array.user_names);
        String[] bookTitles = getResources().getStringArray(R.array.book_titles);

        for (int i = 0; i < userNames.length; i++) {
            // Создаем ItemModel с именем пользователя и названием книги
            models.add(new ItemModel(bookTitles[i], userNames[i]));
        }

        return models;
    }

    @Override
    public void onItemClick(ItemModel item) {
        ProductDetailsFragment fragment = ProductDetailsFragment.newInstance(item);
        if (getActivity() != null) {
            ((MainActivity) getActivity()).makeCurrentFragment(fragment);
        }
    }
}