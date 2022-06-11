package com.example.deliveryproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class UserShopsAdapter extends ArrayAdapter<Object> implements Filterable {
    public UserShopsAdapter(@NonNull Context context, @NonNull Object[] items) {
        super(context, 0, items);
        this.context = context;

        this.items = new ArrayList<>();
        this.allItems = new ArrayList<>();
        for (Object ds : items) {
            this.items.add((DataSnapshot)ds);
            this.allItems.add((DataSnapshot)ds);
        }
    }

    Context context;
    List<DataSnapshot> items;
    List<DataSnapshot> allItems;

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.item_shops, parent, false);
        TextView name = row.findViewById(R.id.i_shops_name);
        ImageView icon = row.findViewById(R.id.i_shops_icon);
        TextView isOpen = row.findViewById(R.id.i_shops_isOpen);

        if (position >= this.items.size()) {
            row = layoutInflater.inflate(R.layout.fragment_empty, parent, false);
            row.setEnabled(false);
            return row;
        }

        DataSnapshot dataSnapshot = this.items.get(position);
        name.setText(dataSnapshot.child("Name").getValue().toString());
        setImage(icon, dataSnapshot.child("Photo path").getValue().toString());
        setOpen(isOpen,
                dataSnapshot.child("isOpen").getValue().toString(),
                row.findViewById(R.id.i_shops_layout));

        row.setOnClickListener(view -> {
            Iterable<DataSnapshot> items = dataSnapshot.child("Range").getChildren();

            ArrayList<DataSnapshot> arr = new ArrayList<>();
            for (DataSnapshot ds : items) {
                arr.add(ds);
            }

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.a_user_fragment, new UserProductsFragment(arr, dataSnapshot.child("Name").getValue().toString()));
            fragmentTransaction.commit();
        });
        return row;
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
            items = (ArrayList<DataSnapshot>) filterResults.values;
            notifyDataSetChanged();
        }
    };

    // Метод для установки изображения
    private void setImage(ImageView imageView, String photoName) {
        if (photoName.equals("")) {
            return;
        }

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference imageRef = firebaseStorage.getReference()
                .child("Shops")
                .child(photoName + ".jpg");

        imageRef.getBytes(256 * 256)
                .addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                });
    }

    // Метод для открытия/закрытия магазина
    private void setOpen(TextView isOpenTv, String isOpen, ConstraintLayout layout) {
        if (isOpen.equals("true")) {
            isOpenTv.setText("Открыто");
            isOpenTv.setTextColor(Color.GREEN);
            layout.setEnabled(true);
        } else {
            isOpenTv.setText("Закрыто");
            isOpenTv.setTextColor(Color.RED);
            layout.setEnabled(false);
        }
    }
}
