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
import com.example.deliveryproject.fragments.AdminEditRestFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminRestaurantsAdapter extends ArrayAdapter<Object> implements Filterable {
    public AdminRestaurantsAdapter(@NonNull Context context,
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
        View row = layoutInflater.inflate(R.layout.item_admin_rests, parent, false);
        TextView name = row.findViewById(R.id.i_admin_rests_name);
        ImageView icon = row.findViewById(R.id.i_admin_rests_icon);
        RadioButton openRadioButton = row.findViewById(R.id.i_admin_rests_open);
        RadioButton closedRadioButton = row.findViewById(R.id.i_admin_rests_closed);

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

        // Получение ссылки базы данных
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // Слушаетль кнопки открытия и закрытия ресторана
        openRadioButton.setOnCheckedChangeListener((compoundButton, b) -> {
            // Установка открытия и закрытия ресторана при нажатии кнопки
            if (b) {
                firebaseDatabase.child("Restaurants").child(dataSnapshot.getKey()).child("isOpen").setValue("true");
            } else {
                firebaseDatabase.child("Restaurants").child(dataSnapshot.getKey()).child("isOpen").setValue("false");
            }
        });

        // Слушатель долгого нажатия на ресторан
        row.setOnLongClickListener(view -> {
            // Создание диалогового окна
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Удалить ресторан?");

            // Слушатель положительной кнопки
            builder.setPositiveButton("Да", (dialogInterface, i) -> {
                // Получение конкретного ресторана. Слушатель ответа от базы
                firebaseDatabase.child("Restaurants").get().addOnCompleteListener(runnable -> {
                    if (!runnable.isSuccessful()) {
                        // Не удалось получить ответ от базы
                        Toast.makeText(context, "Плохое соединение с базой", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Проверка на количество ресторанов
                    if (runnable.getResult().getChildrenCount() <= 1) {
                        // Ресторанов больше не осталось. Устанавливается пустое значение
                        firebaseDatabase.child("Restaurants").setValue("");
                    } else {
                        // Рестораны есть. Удаляется ресторан
                        firebaseDatabase.child("Restaurants").child(dataSnapshot.getKey()).removeValue();
                    }

                    Toast.makeText(context, "Ресторан успешно удален", Toast.LENGTH_SHORT).show();

                    // Закрытие диалогового окна
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

        // Слушатель нажатия на название
        name.setOnClickListener(view -> {
            // Открывается фрагмент (диалоговое окно) для изменения названия ресторана
            AdminEditRestFragment adminEditRestFragment = new AdminEditRestFragment(
                    dataSnapshot.getKey(),
                    dataSnapshot.child("Name").getValue().toString());
            adminEditRestFragment.show(fragmentManager, "");
        });

        // Слушатель нажатия на фото ресторана
        icon.setOnClickListener(view -> {
            // Загрузка данных во внутреннее хранилище программы
            SharedPreferences sharedPreferences = context.getSharedPreferences("Clicked", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ImageRest", dataSnapshot.getKey());
            editor.apply();

            getImage();
        });

        return row;
    }

    // Метод получения фильтра
    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    // Создание фильтра и установка настроек
    Filter filter = new Filter() {
        // Метод для фильтрации
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<DataSnapshot> filteredList = new ArrayList<>();

            // Проверка данных которые ввел пользователь в поиск
            if (charSequence.toString().isEmpty()) {
                // Поле поиска пустое. Устанавливаем ВСЕ значения
                filteredList.addAll(allItems);
            } else {
                // Поле поиска не пустое. Фильтруем значения по имени
                for (DataSnapshot ds : allItems) {
                    if (ds.child("Name")
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

        // Метод для обновления данных и вывода списка
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            items = (ArrayList<DataSnapshot>) filterResults.values;
            notifyDataSetChanged();
        }
    };

    // Метод для установки картинки
    private void setImage(ImageView imageView, String photoName) {
        // Проверка на пустоту пути к картинке
        if (photoName.equals("")) {
            return;
        }

        // Получение ссылки базы данных
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        // Получение ссылки на картинку ресторана
        StorageReference imageRef = firebaseStorage.getReference()
                .child("Restaurants")
                .child(photoName + ".jpg");

        // Получение картинки с базы данных и установка ее на imageView
        imageRef.getBytes(256 * 256).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bitmap);
        });
    }

    // Метод для получения картинки
    private void getImage() {
        // Открытие окна галереи
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Запуск слушателя на результат окна и установка кода запроса (2)
        parent.startActivityForResult(intent, 2);
    }
}
