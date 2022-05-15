package com.example.deliveryproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserDishesAdapter extends RecyclerView.Adapter<UserDishesAdapter.DishesItem> implements Filterable {
    public UserDishesAdapter(ArrayList<DataSnapshot> snapshot, Context context, String shopName) {
        this.snapshot = snapshot;
        this.context = context;

        this.shopName = shopName;

        this.allItems = new ArrayList<>(this.snapshot);
        settings = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Context context;

    String shopName;

    ArrayList<DataSnapshot> snapshot;
    ArrayList<DataSnapshot> allItems;

    class DishesItem extends RecyclerView.ViewHolder {
        @SuppressLint("SetTextI18n")
        public DishesItem(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.i_dishes_iv);
            name = itemView.findViewById(R.id.i_dishes_tv);
            price = itemView.findViewById(R.id.i_dishes_price);
            minus = itemView.findViewById(R.id.i_dishes_minus);
            plus = itemView.findViewById(R.id.i_dishes_plus);
        }

        ImageView image;
        TextView name;
        TextView price;
        TextView minus;
        TextView plus;

        // Метод для настройки элементов интерфейса
        @SuppressLint("SetTextI18n")
        void bind(String image, String name, String price) {
            this.name.setText(name);
            this.price.setText(getPrice(price, getCountFromStorage(name)));

            setImage(this.image, image);

            minus.setOnClickListener(view -> {
                String productPrice = price.split("x")[0].replace(" ", "");
                int count = getCountFromStorage(this.name.getText().toString());

                count--;
                this.price.setText(getPrice(productPrice, count));

                editor.putString(name, image + ";"
                        + productPrice + ";"
                        + getCount(count) + ";"
                        + shopName);
                editor.apply();
            });

            plus.setOnClickListener(view -> {
                String productPrice = price.split("x")[0].replace(" ", "");
                int count = getCountFromStorage(this.name.getText().toString());

                count++;
                this.price.setText(getPrice(productPrice, count));

                editor.putString(name, image + ";"
                        + productPrice + ";"
                        + getCount(count) + ";"
                        + shopName);
                editor.apply();
            });
        }

        // Метод для установки изображнеия
        private void setImage(ImageView imageView, String photoName) {
            if (photoName.equals("")) {
                return;
            }

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference imageRef = firebaseStorage.getReference()
                    .child(photoName + ".jpg");

            imageRef.getBytes(1024 * 1024)
                    .addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    });
        }

        // Метод для получения цены
        private String getPrice(String price, int count) {
            if (count <= 0) {
                return price + " руб.";
            }

            if (count >= 99) {
                return price + "руб. x99";
            }
            return price + "руб. x" + count;
        }

        // Метод для получения кол-ва продуктов в диапазоне
        private int getCount(int count) {
            if (count <= 0) {
                return 0;
            }
            if (count >= 99) {
                return 99;
            }
            return count;
        }

        // Метод для получения кол-ва продуктов из внутреннего хранилища
        private int getCountFromStorage(String name) {
            if (settings.getString(name, null) != null) {
                String answer = settings.getString(name, "");

                if (!answer.equals("")) {
                    return Integer.parseInt(answer.split(";")[2]);
                }
            }

            return 0;
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<DataSnapshot> filteredList = new ArrayList<>();

            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(allItems);
            } else {
                for (DataSnapshot ds : allItems) {
                    if ((ds)
                            .child("Name")
                            .getValue()
                            .toString()
                            .toLowerCase()
                            .contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(ds);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            snapshot = (ArrayList<DataSnapshot>) filterResults.values;
            notifyDataSetChanged();
        }
    };

    @NonNull
    @Override
    public DishesItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dishes, parent, false);
        return new DishesItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishesItem holder, int position) {
        if (position >= getItemCount()) {
            return;
        }

        holder.bind(
                snapshot.get(position).child("Photo path").getValue().toString(),
                snapshot.get(position).child("Name").getValue().toString(),
                snapshot.get(position).child("Price").getValue().toString()
        );
    }

    @Override
    public int getItemCount() {
        return snapshot.size();
    }
}

