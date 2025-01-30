package com.example.polochka.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polochka.MainActivity;
import com.example.polochka.R;
import com.example.polochka.adapters.ItemRecyclerViewAdapter;
import com.example.polochka.login.LoginActivity;
import com.example.polochka.models.ItemModel;
import com.example.polochka.utils.ServerCommunicator;

import java.util.ArrayList;

public class ProfileFragment extends Fragment  implements ItemRecyclerViewAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ArrayList<ItemModel> items = new ArrayList<>();
    private ItemRecyclerViewAdapter adapter;

    private TextView usernameLabel;

    private ServerCommunicator serverCommunicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button logOutBtn = view.findViewById(R.id.LogOut);
        logOutBtn.setOnClickListener(v -> logOut());

        recyclerView = view.findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        serverCommunicator = new ServerCommunicator(requireContext());
        serverCommunicator.getUserBooks(username,getActivity(),requireContext(),this::setUpItems);

        usernameLabel = view.findViewById(R.id.usernameLabel);
        usernameLabel.setText(username);


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

    private void logOut() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.putBoolean("is_logged_in", false);
        editor.apply();

        Intent myIntent = new Intent(requireContext(), LoginActivity.class);
        startActivity(myIntent);
    }

    @Override
    public void onItemClick(ItemModel item) {
        ProductDetailsFragment fragment = ProductDetailsFragment.newInstance(item);
        if (getActivity() != null) {
            ((MainActivity) getActivity()).makeCurrentFragment(fragment);
        }
    }
}