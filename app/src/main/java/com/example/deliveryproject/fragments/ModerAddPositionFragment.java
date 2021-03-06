package com.example.deliveryproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.deliveryproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ModerAddPositionFragment extends DialogFragment {
    DatabaseReference menuRef;

    public ModerAddPositionFragment(DatabaseReference menuRef) {
        this.menuRef = menuRef;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_position, container, false);

        // Слушатель кнопки ОТМЕНА
        view.findViewById(R.id.d_add_position_cancel_button).setOnClickListener(view1 -> {
            this.dismiss();
        });

        // Слушатель кнопки ОК
        view.findViewById(R.id.d_add_position_ok_button).setOnClickListener(view1 -> {
            String name = ((EditText)view.findViewById(R.id.d_add_position_name)).getText().toString();
            String price = ((EditText)view.findViewById(R.id.d_add_position_price)).getText().toString();

            // Проверка на заполненные поля
            if (name.replace(" ", "").equals("")
                    || price.replace(" ", "").equals("")) {
                Toast.makeText(inflater.getContext(), "Поле должно быть заполнено", Toast.LENGTH_SHORT).show();
                this.dismiss();
                return;
            }

            // Проверка введенной строки является ли она числом
            if (!isNumeric(price)) {
                Toast.makeText(inflater.getContext(), "В поле цена должно быть число", Toast.LENGTH_SHORT).show();
                this.dismiss();
                return;
            }

            // Получение базы данных
            menuRef.get().addOnCompleteListener(runnable -> {
                // Проверка на полученный ответ
                if (!runnable.isSuccessful()) {
                    Toast.makeText(inflater.getContext(), "Плохое соединение с базой", Toast.LENGTH_SHORT).show();
                    this.dismiss();
                    return;
                }

                // Проверка на совпадение имен
                for (DataSnapshot dataSnapshot : runnable.getResult().getChildren()) {
                    if (dataSnapshot.child("Name").getValue().equals(name)) {
                        Toast.makeText(inflater.getContext(), "Позиция с таким имененем уже существует", Toast.LENGTH_SHORT).show();
                        this.dismiss();
                        return;
                    }
                }

                // Добавление новой позиции
                HashMap<String, String> position = new HashMap<>();
                position.put("Name", name);
                position.put("Photo path", "");
                position.put("Price", price);
                menuRef.child(name).setValue(position);

                Toast.makeText(inflater.getContext(), "Позиция успешно добавлена", Toast.LENGTH_SHORT).show();
                this.dismiss();
            });
        });

        return view;
    }

    // Метод для проверки является ли стркоа числом
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
