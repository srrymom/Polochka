package com.example.polochka.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.polochka.R;
import com.example.polochka.models.ItemModel;

import java.io.Serializable;

public class ProductDetailsFragment extends Fragment {


    private ItemModel book;

    // Метод для создания нового экземпляра фрагмента с данными о книге
    public static ProductDetailsFragment newInstance(ItemModel book) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book); // Передаем книгу в аргументы фрагмента
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        // Получаем данные о книге из аргументов фрагмента
        if (getArguments() != null) {
            book = (ItemModel) getArguments().getSerializable("book");
        }

        // Отображаем информацию о книге
        TextView titleTextView = view.findViewById(R.id.tvBookTitle);

        titleTextView.setText(book.getBookTitle());

        return view;
    }

}