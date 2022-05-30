package com.example.deliveryproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.deliveryproject.activities.UserOrderingActivity;
import com.example.deliveryproject.fragments.ModerEditPriceFragment;
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
    FragmentManager fragmentManager;
    Fragment fragment;
    Context context;

    SharedPreferences preferences;
    Map<String, ?> map;
    View parent;
    View view;

    public UserCartAdapter(Context context,
                           SharedPreferences preferences,
                           ViewGroup parent,
                           View view,
                           FragmentManager fragmentManager,
                           Fragment fragment) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;
        this.preferences = preferences;
        this.map = preferences.getAll();
        this.parent = parent;
        this.view = view;
    }

    class CartItem extends RecyclerView.ViewHolder {
        public CartItem(@NonNull View itemView) {
            super(itemView);

            // Слушатель кнопки подтверждения покупки
            view.findViewById(R.id.f_cart_confirm).setOnClickListener(v -> {
                Intent intent = new Intent(context, UserOrderingActivity.class);
                intent.putExtra("TotalSum", getTotalSum());
                context.startActivity(intent);
            });

            if (!(preferences.getAll().size() == 0)) {
                image = itemView.findViewById(R.id.i_cart_image);
                name = itemView.findViewById(R.id.i_cart_name);
                price = itemView.findViewById(R.id.i_cart_price);
                count = itemView.findViewById(R.id.i_cart_count);
                deleteBucket = itemView.findViewById(R.id.i_cart_delete_bucket);
                shopName = itemView.findViewById(R.id.i_cart_shop);
            }
        }

        ImageView image;
        TextView name;
        TextView price;
        TextView count;
        ImageView deleteBucket;
        TextView shopName;

        @SuppressLint("SetTextI18n")
        void bind(String image, String name, String price, String count, String shopName) {
            // Проверка на размер корзины
            if ((preferences.getAll().size() == 0)) {
                return;
            }

            this.name.setText(name);
            this.price.setText(price);
            this.count.setText(count);
            this.shopName.setText(shopName);
            setImage(this.image, image);

            // Слушатель кнопки удаления из корзины
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

        // Метод для установки изображения
        private void setImage(ImageView imageView, String photoName) {
            if (photoName.equals("")) {
                return;
            }

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference imageRef = firebaseStorage.getReference()
                    .child(photoName + ".jpg");

            imageRef.getBytes(256 * 256)
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
        List<String> values = new ArrayList<>((Collection<? extends String>) map.values());

        holder.bind(values.get(position).split(";")[0],
                keys.get(position),
                values.get(position).split(";")[1] + " руб.",
                values.get(position).split(";")[2] + " шт.",
                values.get(position).split(";")[3]);
    }

    @Override
    public int getItemCount() {
        return preferences.getAll().size();
    }

    // Метод для получения общей суммы в корзине
    private float getTotalSum() {
        float sum = 0;

        for (Object s : preferences.getAll().values()) {
            sum += Float.parseFloat(s.toString().split(";")[1]) * Integer.parseInt(s.toString().split(";")[2]);
        }

        return sum;
    }
}
