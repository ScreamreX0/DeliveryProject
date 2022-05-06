package com.example.deliveryproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.deliveryproject.R;
import com.example.deliveryproject.fragments.UserProductsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserRestaurantsAdapter extends ArrayAdapter<Object> {
    Context context;
    Object[] items;

    public UserRestaurantsAdapter(@NonNull Context context, @NonNull Object[] items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.item_shops, parent, false);
        TextView name = row.findViewById(R.id.i_shops_name);
        ImageView icon = row.findViewById(R.id.i_shops_icon);
        TextView isOpen = row.findViewById(R.id.i_shops_isOpen);

        DataSnapshot dataSnapshot = (DataSnapshot) items[position];
        name.setText(dataSnapshot.child("Name").getValue().toString());
        setImage(icon, dataSnapshot.child("Photo path").getValue().toString());
        setOpen(isOpen, dataSnapshot.child("isOpen").getValue().toString());

        row.setOnClickListener(view -> {
            Iterable<DataSnapshot> items = dataSnapshot.child("Range").getChildren();

            List<DataSnapshot> arr = new ArrayList<>();
            for (DataSnapshot ds : items) {
                arr.add(ds);
            }

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.a_user_fragment, new UserProductsFragment(arr));
            fragmentTransaction.commit();
        });



        return row;
    }

    private void setImage(ImageView imageView, String photoName) {
        if (photoName.equals("")) {
            return;
        }

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference imageRef = firebaseStorage.getReference()
                .child("Shops")
                .child(photoName + ".jpg");

        imageRef.getBytes(1024 * 1024)
                .addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                });
    }

    private void setOpen(TextView isOpenTv, String isOpen) {
        if (isOpen.equals("true")) {
            isOpenTv.setText("Открыто");
        } else {
            isOpenTv.setText("Закрыто");
        }
    }
}
