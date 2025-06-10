package com.example.odyssiaproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorChat extends RecyclerView.Adapter {

    private ArrayList<ChatModal> chatModalArrayList;
    private Context context;

    public AdaptadorChat(ArrayList<ChatModal> chatModalArrayList, Context context) {
        this.chatModalArrayList = chatModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_msg, parent, false);
                return new UserViewHolder(view);

            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bot_msg, parent, false);
                return new BotViewHolder(view);

            case 2:
            default:
                // Puedes reutilizar el layout de usuario o crear uno específico para fallback
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_msg, parent, false);
                return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModal chatModal = chatModalArrayList.get(position);
        switch (chatModal.getSender()) {
            case "user":
                ((UserViewHolder) holder).twUser.setText(chatModal.getMensaje());
                break;
            case "bot":
                ((BotViewHolder) holder).twBot.setText(chatModal.getMensaje());
                break;
            default:
                // Si quieres manejar otro tipo o mostrar mensaje por defecto
                ((UserViewHolder) holder).twUser.setText(chatModal.getMensaje());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (chatModalArrayList.get(position).getSender()) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return 2;
        }
    }

    @Override
    public int getItemCount() {
        return chatModalArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView twUser;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            twUser = itemView.findViewById(R.id.twUser);
        }

    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {

        TextView twBot;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            twBot = itemView.findViewById(R.id.twBot);
        }

    }

}
