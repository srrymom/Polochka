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
import com.example.polochka.models.GoogleBook;

import java.util.ArrayList;

public class GoogleBookRecyclerViewAdapter extends RecyclerView.Adapter<GoogleBookRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<GoogleBook> googleBooks;
    private OnItemClickListener listener; // Слушатель для кликов


    // Интерфейс для обработки кликов
    public interface OnItemClickListener {
        void onItemClick(GoogleBook item);
    }

    // Конструктор адаптера с добавлением слушателя
    public GoogleBookRecyclerViewAdapter(Context context, ArrayList<GoogleBook> googleBooks, OnItemClickListener listener) {
        this.context = context;
        this.googleBooks = googleBooks;
        this.listener = listener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.book_find_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GoogleBook item = googleBooks.get(position);

        holder.titleTextView.setText(item.getTitle());
        holder.authorTextView.setText(item.getAuthor());

        String thumbnailUrl = item.getThumbnail();

        // Проверяем, не null ли thumbnailUrl
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            // Заменяем http на https и загружаем изображение
            Glide.with(this.context)
                    .load(thumbnailUrl.replace("http://", "https://"))
                    .placeholder(R.drawable.small_book_placeholder2)  // Плейсхолдер
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // Отключаем кэширование
                    .into(holder.previewImage);
        } else {
            // Если URL не задан, используем только плейсхолдер
            Glide.with(this.context)
                    .load(R.drawable.small_book_placeholder2)  // Только плейсхолдер
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // Отключаем кэширование
                    .into(holder.previewImage);
        }

        // Устанавливаем обработчик клика для элемента
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item)); // Передаем выбранный элемент

    }

    @Override
    public int getItemCount() {
        return googleBooks.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView authorTextView;
        ImageView previewImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            previewImage = itemView.findViewById(R.id.bookImage);
            titleTextView = itemView.findViewById(R.id.tvBookTitle);
            authorTextView = itemView.findViewById(R.id.tvAuthor);
        }
    }
}
