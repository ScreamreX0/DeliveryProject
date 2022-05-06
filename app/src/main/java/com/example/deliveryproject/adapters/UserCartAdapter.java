package com.example.deliveryproject.adapters;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.chrono.HijrahChronology;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class UserCartAdapter extends RecyclerView.Adapter<UserCartAdapter.CartItem> {
    public UserCartAdapter(SharedPreferences preferences, ViewGroup parent, View view, FragmentManager fragmentManager, Fragment fragment) {
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;
        this.preferences = preferences;
        this.map = preferences.getAll();
        this.parent = parent;
        this.view = view;
    }

    FragmentManager fragmentManager;
    Fragment fragment;

    SharedPreferences preferences;
    Map<String, ?> map;
    View parent;
    View view;

    class CartItem extends RecyclerView.ViewHolder {
        public CartItem(@NonNull View itemView) {
            super(itemView);

            view.findViewById(R.id.f_cart_confirm).setOnClickListener(v -> {
                insertToHistory();

                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                notifyItemRangeRemoved(0, getItemCount());

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.a_user_fragment, fragment);
                fragmentTransaction.commit();
            });


            if (!(preferences.getAll().size() == 0)) {
                image = itemView.findViewById(R.id.i_cart_image);
                name = itemView.findViewById(R.id.i_cart_name);
                price = itemView.findViewById(R.id.i_cart_price);
                count = itemView.findViewById(R.id.i_cart_count);
                deleteBucket = itemView.findViewById(R.id.i_cart_delete_bucket);
            }
        }

        ImageView image;
        TextView name;
        TextView price;
        TextView count;
        ImageView deleteBucket;

        @SuppressLint("SetTextI18n")
        void bind(String image, String name, String price, String count) {
            if (!(preferences.getAll().size() == 0)) {
                this.name.setText(name);
                this.price.setText(price);
                this.count.setText(count);
                setImage(this.image, image);

                deleteBucket.setOnClickListener(view -> {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(name);
                    editor.apply();

                    notifyItemRemoved(getAdapterPosition());

                    if (preferences.getAll().size() == 0) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.a_user_fragment, fragment);
                        fragmentTransaction.commit();
                    } else {
                        ((TextView)parent.findViewById(R.id.f_cart_total_sum)).setText(String.valueOf(getTotalSum()) + " руб.");
                    }
                });
            }
        }

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
    }

    @NonNull
    @Override
    public CartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItem holder, int position) {
        List<String> keys = new ArrayList<>(map.keySet());
        List<String> values = new ArrayList<String>((Collection<? extends String>) map.values());

        holder.bind(values.get(position).split(";")[0],
                keys.get(position),
                values.get(position).split(";")[1] + " руб.",
                values.get(position).split(";")[2] + " шт.");
    }

    @Override
    public int getItemCount() {
        return preferences.getAll().size();
    }

    private float getTotalSum() {
        float sum = 0;

        for (Object s : preferences.getAll().values()) {
            sum += Float.parseFloat(s.toString().split(";")[1]) * Integer.parseInt(s.toString().split(";")[2]);
        }

        return sum;
    }

    @SuppressLint("SimpleDateFormat")
    private void insertToHistory() {
        List<String> keys = new ArrayList<>(preferences.getAll().keySet());
        List<String> values = new ArrayList<String>((Collection<? extends String>) preferences.getAll().values());

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        HashMap<String, Object> historyMap = new HashMap<>();
        historyMap.put("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

        HashMap<String, HashMap<String, String>> productsMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            HashMap<String, String> productMap = new HashMap<>();
            productMap.put("Name", keys.get(i));
            productMap.put("Photo path", values.get(i).split(";")[0]);
            productMap.put("Price", values.get(i).split(";")[1]);
            productMap.put("Count", values.get(i).split(";")[2]);

            productsMap.put("Product" + i, productMap);
        }
        historyMap.put("Products", productsMap);

        firebaseDatabase.child("Users").child(firebaseAuth.getUid()).child("History").push().setValue(historyMap);
    }
}
