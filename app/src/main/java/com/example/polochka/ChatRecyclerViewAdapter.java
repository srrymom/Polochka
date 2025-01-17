package com.example.polochka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<ChatModel> chatModels;

    @NonNull
    @Override
    public ChatRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_row, parent, false);
        return new ChatRecyclerViewAdapter.MyViewHolder(view);
    }

    public ChatRecyclerViewAdapter(Context context, ArrayList<ChatModel> chatModels) {
        this.context = context;
        this.chatModels = chatModels;

    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.mesTextView.setText(chatModels.get(position).getLastMessage());
        holder.titleTextView.setText(chatModels.get(position).getBookTitle());
        holder.nameTextView.setText(chatModels.get(position).getUserName());

    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        TextView nameTextView;
        TextView titleTextView;
        TextView mesTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.chatNameLabel);
            titleTextView = itemView.findViewById(R.id.chatBookNameLabel);
            mesTextView = itemView.findViewById(R.id.chatLastMesLabel);

        }
    }
};
