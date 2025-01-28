package com.example.polochka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polochka.R;

import java.util.ArrayList;

import com.example.polochka.models.ItemModel;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<ItemModel> itemModels;
    private OnItemClickListener listener; // Слушатель для кликов

    // Интерфейс для обработки кликов
    public interface OnItemClickListener {
        void onItemClick(ItemModel item);
    }
    // Конструктор адаптера с добавлением слушателя
    public ItemRecyclerViewAdapter(Context context, ArrayList<ItemModel> itemModels, OnItemClickListener listener) {
        this.context = context;
        this.itemModels = itemModels;
        this.listener = listener; // Устанавливаем слушатель
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_card, parent, false); // Предполагается, что layout называется item_row
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemModel item = itemModels.get(position);

        holder.titleTextView.setText(itemModels.get(position).getBookTitle());
        holder.nameTextView.setText(itemModels.get(position).getUserName());
        // Устанавливаем обработчик клика для элемента
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item)); // Передаем выбранный элемент

    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView nameTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.bookCardTitle); // ID текстового поля для bookTitle
            nameTextView = itemView.findViewById(R.id.bookCardPlace); // ID текстового поля для userName
        }
    }
}
