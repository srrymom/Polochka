package com.example.polochka.Fragments;


import android.os.Bundle;
import android.util.Log;
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
import com.example.polochka.utils.ServerCommunicator;


import java.util.ArrayList;

public class MainFragment extends Fragment implements ItemRecyclerViewAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ArrayList<ItemModel> items = new ArrayList<>();
    private ItemRecyclerViewAdapter adapter;
    private ServerCommunicator serverCommunicator;
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        // Инициализация фрагмента
        View view = inflater.inflate(R.layout.fragment_main, container, false);



        recyclerView = view.findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Запрос на получение списка книг с сервера
        serverCommunicator = new ServerCommunicator(requireContext());

        serverCommunicator.getBooksFromServer(getActivity(),requireContext(),this::setUpItems);

        return view;
    }


    // Метод для обновления адаптера с новыми данными
    private void setUpItems(ArrayList<ItemModel> newItems) {
        items.clear();
        items.addAll(newItems);

        // Инициализация RecyclerView
        adapter = new ItemRecyclerViewAdapter(requireContext(), items, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(ItemModel item) {
        ProductDetailsFragment fragment = ProductDetailsFragment.newInstance(item);
        if (getActivity() != null) {
            ((MainActivity) getActivity()).makeCurrentFragment(fragment);
        }
    }}
