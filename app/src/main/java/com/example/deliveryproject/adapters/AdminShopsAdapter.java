package com.example.deliveryproject.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.deliveryproject.R;
import com.example.deliveryproject.fragments.AdminEditShopFragment;
import com.example.deliveryproject.fragments.ModerEditPriceFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminShopsAdapter extends ArrayAdapter<Object> implements Filterable {
    public AdminShopsAdapter(@NonNull Context context,
                             @NonNull Object[] items,
                             androidx.fragment.app.FragmentManager fragmentManager,
                             Activity parentActivity) {
        super(context, 0, items);

        this.items = new ArrayList<>();
        this.allItems = new ArrayList<>();

        // Заполнение всех ресторанов и копии всех ресторанов
        for (Object ds : items) {
            this.items.add((DataSnapshot)ds);
            this.allItems.add((DataSnapshot)ds);
        }
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.parent = parentActivity;
    }

    Context context;
    List<DataSnapshot> items;
    List<DataSnapshot> allItems;
    FragmentManager fragmentManager;
    Activity parent;

    // Метод родительского класса для получения представления
    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Определяем надуватель разметок
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Инициализация элемента в списке
        View row = layoutInflater.inflate(R.layout.item_admin_shops, parent, false);
        TextView name = row.findViewById(R.id.i_admin_shops_name);
        ImageView icon = row.findViewById(R.id.i_admin_shops_icon);
        RadioButton openRadioButton = row.findViewById(R.id.i_admin_shops_open);
        RadioButton closedRadioButton = row.findViewById(R.id.i_admin_shops_closed);

        // Проверка на длину исходного массива элементов
        if (position >= this.items.size()) {
            // Массив элементов пустой. Возвращаем пустое поле
            row = layoutInflater.inflate(R.layout.fragment_empty, parent, false);
            row.setEnabled(false);
            return row;
        }

        // Получение снимка конкретного ресторана
        DataSnapshot dataSnapshot = this.items.get(position);
        if (dataSnapshot == null) {
            return row;
        }

        // Установка названия и картинки
        name.setText(dataSnapshot.child("Name").getValue().toString());
        setImage(icon, dataSnapshot.child("Photo path").getValue().toString());

        // Установка открытия и закрытия ресторана
        boolean open = dataSnapshot.child("isOpen").getValue().toString().equals("true");
        if (open) {
            openRadioButton.setChecked(true);
        } else {
            closedRadioButton.setChecked(true);
        }

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // Слушатель группы кнопок открытия и закрытия ресторана
        openRadioButton.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                firebaseDatabase.child("Shops").child(dataSnapshot.getKey()).child("isOpen").setValue("true");
            } else {
                firebaseDatabase.child("Shops").child(dataSnapshot.getKey()).child("isOpen").setValue("false");
            }
        });

        // Слушатель на долгое нажатие магазина
        row.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Удалить магазин?");
            builder.setPositiveButton("Да", (dialogInterface, i) -> {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                databaseReference.get().addOnCompleteListener(runnable -> {
                    if (!runnable.isSuccessful()) {
                        Toast.makeText(context, "Плохое соединение с базой", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DataSnapshot user : runnable.getResult().child("Users").getChildren()) {
                        if (!user.child("PrivilegedSettings").getValue().equals("")) {
                            if (user.child("PrivilegedSettings").child("Name").getValue().equals(dataSnapshot.getKey())) {
                                firebaseDatabase.child("Users")
                                        .child(user.getKey())
                                        .child("PrivilegedSettings")
                                        .setValue("");
                                firebaseDatabase.child("Users")
                                        .child(user.getKey())
                                        .child("Role")
                                        .setValue("Client");
                                break;
                            }
                        }
                    }

                    if (runnable.getResult().child("Shops").getChildrenCount() <= 1) {
                        databaseReference.child("Shops").setValue("");
                    } else {
                        databaseReference.child("Shops").child(dataSnapshot.getKey()).removeValue();
                    }

                    Toast.makeText(context, "Магазин успешно удален", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                });
            });

            builder.setNegativeButton("Отмена", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            return false;
        });


        // Слушатель нажатия на имя
        name.setOnClickListener(view -> {
            AdminEditShopFragment adminEditShopFragment = new AdminEditShopFragment(
                    dataSnapshot.getKey(),
                    dataSnapshot.child("Name").getValue().toString());
            adminEditShopFragment.show(fragmentManager, "");
        });

        // Слушатель нажатия на иконку
        icon.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Clicked", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Image", dataSnapshot.getKey());
            editor.apply();

            getImage();
        });

        return row;
    }

    // Метод для получения фильтра
    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    // Настройка фильтра
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

    private void getImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        parent.startActivityForResult(intent, 1);
    }
}
