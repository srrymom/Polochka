package com.example.polochka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.polochka.R;

import java.util.ArrayList;

import com.example.polochka.models.ItemModel;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<ItemModel> itemModels;
    private OnItemClickListener listener; // Слушатель для кликов
    private String SERVER_URL;


    // Интерфейс для обработки кликов
    public interface OnItemClickListener {
        void onItemClick(ItemModel item);
    }

    // Конструктор адаптера с добавлением слушателя
    public ItemRecyclerViewAdapter(Context context, ArrayList<ItemModel> itemModels, OnItemClickListener listener) {
        this.context = context;
        this.itemModels = itemModels;
        this.listener = listener; // Устанавливаем слушатель
        SERVER_URL = context.getString(R.string.server_url);

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

        holder.titleTextView.setText(item.getTitle());
        holder.nameTextView.setText(item.getUsername());
        holder.bookCardDateTime.setText(item.getCity());

        Glide.with(this.context)
                .load(SERVER_URL + "/images/" + itemModels.get(position).getimageId()) // указываем URL изображения
                .placeholder(R.drawable.small_book_placeholder2)  // Укажите ресурс изображения для плейсхолдера
                .diskCacheStrategy(DiskCacheStrategy.NONE)  // Отключить кэширование

                .into(holder.previewImage);

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

        ImageView previewImage;

        TextView bookCardDateTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            previewImage = itemView.findViewById(R.id.previewImage);
            titleTextView = itemView.findViewById(R.id.bookCardTitle);
            nameTextView = itemView.findViewById(R.id.bookCardPlace);
            bookCardDateTime = itemView.findViewById(R.id.bookCardDateTime);
        }
    }
}
