package com.example.deliveryproject.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryproject.R;
import com.example.deliveryproject.fragments.AdminEditShopFragment;
import com.example.deliveryproject.fragments.ModerEditNameFragment;
import com.example.deliveryproject.fragments.ModerEditPriceFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ModerPositionsAdapter extends RecyclerView.Adapter<ModerPositionsAdapter.PositionItem> implements Filterable {
    public ModerPositionsAdapter(ArrayList<DataSnapshot> items,
                                 FragmentManager fragmentManager,
                                 Context context,
                                 DatabaseReference menuRef,
                                 String type,
                                 String name,
                                 Activity activity) {
        this.items = items;
        this.allItems = new ArrayList<>(items);

        this.context = context;
        this.fragmentManager = fragmentManager;
        this.menuRef = menuRef;

        this.type = type;
        this.name = name;

        this.parent = activity;
    }

    ArrayList<DataSnapshot> allItems;
    ArrayList<DataSnapshot> items;
    FragmentManager fragmentManager;
    Context context;
    DatabaseReference menuRef;
    String type;
    String name;
    Activity parent;

    public class PositionItem extends RecyclerView.ViewHolder{
        public PositionItem(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.i_positions_iv);
            name = itemView.findViewById(R.id.i_positions_tv);
            price = itemView.findViewById(R.id.i_positions_price);
            layout = itemView.findViewById(R.id.i_positions_layout);
        }

        ImageView image;
        TextView name;
        TextView price;
        ConstraintLayout layout;

        public void bind(DataSnapshot snapshot, String shopName) {
            String photoPath = snapshot.child("Photo path").getValue().toString();
            String name = snapshot.child("Name").getValue().toString();
            String price = snapshot.child("Price").getValue().toString();

            this.name.setText(name);
            this.price.setText(price);
            setImage(this.image, photoPath);

            // Слушатель долгого нажатия на позицию
            layout.setOnLongClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Удалить позицию?");
                builder.setPositiveButton("Да", (dialogInterface, i) -> {
                    if (items.size() <= 1) {
                        menuRef.setValue("");
                    } else {
                        menuRef.child(snapshot.getKey()).removeValue();
                    }
                    dialogInterface.dismiss();
                });

                builder.setNegativeButton("Отмена", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            });

            // Слушатель нажатия на цену
            this.price.setOnClickListener(view -> {
                ModerEditPriceFragment moderEditPriceFragment = new ModerEditPriceFragment(
                        snapshot,
                        type,
                        shopName);
                moderEditPriceFragment.show(fragmentManager, "");
            });

            // Слушатель нажатия на имя
            this.name.setOnClickListener(view -> {
                ModerEditNameFragment moderEditPriceFragment = new ModerEditNameFragment(
                        snapshot,
                        type,
                        shopName);
                moderEditPriceFragment.show(fragmentManager, "");
            });

            // Слушатель нажатия на иконку
            this.image.setOnClickListener(view -> {
                SharedPreferences sharedPreferences = context.getSharedPreferences("Clicked", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ImagePosition", type + ";" + shopName + ";" + snapshot.getKey());
                editor.apply();

                getImage();
            });

        }
    }

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

    @NonNull
    @Override
    public PositionItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_positions, parent, false);
        return new ModerPositionsAdapter.PositionItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PositionItem holder, int position) {
        if (position >= getItemCount()) {
            return;
        }

        holder.bind(items.get(position), name);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void getImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        parent.startActivityForResult(intent, 3);
    }

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
