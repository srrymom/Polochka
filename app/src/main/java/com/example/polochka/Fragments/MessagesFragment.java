package com.example.polochka.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.polochka.ChatModel;
import com.example.polochka.ChatRecyclerViewAdapter;
import com.example.polochka.R;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ChatModel> chatModels;

    public MessagesFragment() {
        // Required empty public constructor
    }

    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.chatsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Инициализация данных
        chatModels = setUpChatModels();

        // Настройка адаптера
        ChatRecyclerViewAdapter chatAdapter = new ChatRecyclerViewAdapter(getContext(), chatModels);
        recyclerView.setAdapter(chatAdapter);

        return view;
    }

    private ArrayList<ChatModel> setUpChatModels() {
        ArrayList<ChatModel> models = new ArrayList<>();
        String[] userNames = getResources().getStringArray(R.array.user_names);
        String[] bookNames = getResources().getStringArray(R.array.book_titles);
        String[] lastMessages = getResources().getStringArray(R.array.last_messages);

        for (int i = 0; i < userNames.length; i++) {
            models.add(new ChatModel(bookNames[i], userNames[i], lastMessages[i]));
        }

        return models;
    }
}
