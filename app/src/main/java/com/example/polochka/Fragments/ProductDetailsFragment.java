package com.example.polochka.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.polochka.R;
import com.example.polochka.models.ItemModel;


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
        String SERVER_URL = requireContext().getString(R.string.server_url);

        // Отображаем информацию о книге
        ImageView productImage = view.findViewById(R.id.productImage);
        TextView tvBookTitle = view.findViewById(R.id.tvBookTitle);
        TextView tvBookAuthor = view.findViewById(R.id.tvBookAuthor);
        TextView userName = view.findViewById(R.id.userName);
        TextView userNumber = view.findViewById(R.id.userNumber);
        TextView bookDescriptionText = view.findViewById(R.id.bookDescriptionText);
        TextView adressTextView = view.findViewById(R.id.adressText);
        adressTextView.setText(String.format("%s, %s", book.getCity(), book.getDistrict()));
        Glide.with(requireContext())
                .load(SERVER_URL + "/images/" + book.getimageId()) // указываем URL изображения
                .placeholder(R.drawable.placeholder_image)  // Укажите ресурс изображения для плейсхолдера
                .into(productImage); // помещаем изображение в ImageView

        tvBookTitle.setText(book.getTitle());
        tvBookAuthor.setText(book.getAuthor());
        userName.setText(book.getUsername());
        userNumber.setText(book.getPhoneNumber());
        bookDescriptionText.setText(book.getDescription());

        return view;
    }

}