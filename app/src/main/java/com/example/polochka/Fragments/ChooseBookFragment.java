package com.example.polochka.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.polochka.R;
import com.example.polochka.adapters.GoogleBookRecyclerViewAdapter;
import com.example.polochka.models.GoogleBook;
import com.example.polochka.utils.GoogleBooksApiFacade;

import java.util.ArrayList;


public class ChooseBookFragment extends Fragment implements GoogleBookRecyclerViewAdapter.OnItemClickListener {


    private Button searchBtn;
    private EditText searchText;
    private RecyclerView recyclerView;
    private Button skipBtn;
    private ArrayList<GoogleBook> items = new ArrayList<>();
    private GoogleBookRecyclerViewAdapter adapter;

    private GoogleBooksApiFacade googleBooksApiFacade;

    public ChooseBookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_choose_book, container, false);
        searchBtn = view.findViewById(R.id.findButton);
        searchText = view.findViewById(R.id.searchField);
        recyclerView = view.findViewById(R.id.recyclerView);
        skipBtn = view.findViewById(R.id.skip);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        googleBooksApiFacade = new GoogleBooksApiFacade(requireContext());
        skipBtn.setOnClickListener(v -> skip());
        searchBtn.setOnClickListener(v -> FindBooks());

        return view;
    }

    private void FindBooks(){
        String prompt = searchText.getText().toString();
        googleBooksApiFacade.findBooks(prompt,requireActivity(),requireContext(),this::setUpItems);

    }

    // Метод для обновления адаптера с новыми данными
    private void setUpItems(ArrayList<GoogleBook> newItems) {
        items.clear();
        items.addAll(newItems);

        // Инициализация RecyclerView
        adapter = new GoogleBookRecyclerViewAdapter(requireContext(), items, this);
        recyclerView.setAdapter(adapter);
    }

    public static ChooseBookFragment newInstance(String param1, String param2) {
        ChooseBookFragment fragment = new ChooseBookFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    public void skip() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fl_wrapper, new NewItemFragment())
                .addToBackStack(null)
                .commit();

    }
    @Override
    public void onItemClick(GoogleBook item) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fl_wrapper, new NewItemFragment(item))
                .addToBackStack(null)
                .commit();

    }
}