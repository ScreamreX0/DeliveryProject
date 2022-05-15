package com.example.deliveryproject.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserHistoryAdapter extends RecyclerView.Adapter<UserHistoryAdapter.HistoryItem> {
    public UserHistoryAdapter(ViewGroup parent, View view, ArrayList<DataSnapshot> snapshot) {
        this.parent = parent;
        this.view = view;
        this.snapshot = snapshot;
    }
    View parent;
    View view;
    ArrayList<DataSnapshot> snapshot;

    class HistoryItem extends RecyclerView.ViewHolder {
        public HistoryItem(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
        }

        View itemView;

        // Метод для настройки элементов интерфейса
        @SuppressLint("SetTextI18n")
        void bind(DataSnapshot snapshot) {
            TextView title = this.itemView.findViewById(R.id.i_history_title);
            TextView date = this.itemView.findViewById(R.id.i_history_date);
            TextView price = this.itemView.findViewById(R.id.i_history_total_sum);
            TextView products = this.itemView.findViewById(R.id.i_history_products);

            date.setText(snapshot.child("Date").getValue().toString());

            StringBuilder stringBuilder = new StringBuilder();
            float totalPrice = 0;
            for (DataSnapshot ds : snapshot.child("Products").getChildren()) {
                stringBuilder.append(ds.child("Name").getValue().toString())
                        .append(" x")
                        .append(ds.child("Count").getValue().toString())
                        .append("\n");

                totalPrice += Float.parseFloat(ds.child("Price").getValue().toString())
                        * Float.parseFloat(ds.child("Count").getValue().toString());
            }

            products.setText(stringBuilder.toString());
            price.setText(totalPrice + " руб.");
            title.setText("Номер заказа:\n" + snapshot.getKey());
        }
    }

    @NonNull
    @Override
    public HistoryItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryItem holder, int position) {
        holder.bind(snapshot.get(position));
    }

    @Override
    public int getItemCount() {
        return snapshot.size();
    }
}
