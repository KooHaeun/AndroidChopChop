package com.example.chop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SnackAdapter extends RecyclerView.Adapter<SnackAdapter.ItemViewHolder>{

    private ArrayList<Snack_Item> data = new ArrayList<>();

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView snack;
        ImageView snack_pic;

        ItemViewHolder(View itemView) {
            super(itemView);

            snack = itemView.findViewById(R.id.snack);
            snack_pic= itemView.findViewById(R.id.snack_pic);


        }
        void onBind(Snack_Item item, int position) {
            snack.setText(item.getName()+" "+item.getKcal());
            snack_pic.setImageBitmap(item.getImage());

        }

    }
    @NonNull
    @Override
    public SnackAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snack_item, parent, false);
        return new SnackAdapter.ItemViewHolder(view);
    }

    public void onBindViewHolder(@NonNull SnackAdapter.ItemViewHolder holder, int position) {
        holder.onBind(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItem(Snack_Item item) {
        data.add(item);
    }
}
