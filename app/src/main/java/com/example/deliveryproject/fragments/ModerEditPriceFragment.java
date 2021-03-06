package com.example.deliveryproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.deliveryproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ModerEditPriceFragment extends DialogFragment {
    DataSnapshot position;
    String type;
    String name;

    public ModerEditPriceFragment(DataSnapshot position, String type, String name) {
        this.position = position;
        this.type = type;
        this.name = name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_moder_edit_price, container, false);

        Button cancel = view.findViewById(R.id.d_moder_edit_price_cancel);
        Button ok = view.findViewById(R.id.d_moder_edit_price_ok);
        EditText editText = view.findViewById(R.id.d_moder_edit_price_edit);

        editText.setText(position.child("Price").getValue().toString());

        // Слушатель кнопки ОТМЕНА
        cancel.setOnClickListener(view1 -> {
            getDialog().dismiss();
        });

        // Слушатель кнопки ОК
        ok.setOnClickListener(view1 -> {
            // Проверка на пустые поля
            if (editText.getText().toString().replace(" ", "").equals("")) {
                Toast.makeText(getContext(), "Поле должно быть заполнено", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверка на неизмененную цену
            if (editText.getText().toString().equals(position.child("Price").getValue().toString())) {
                this.dismiss();
                return;
            }

            // Проверка введеной строки является ли она числом
            if (!isNumeric(editText.getText().toString())) {
                Toast.makeText(getContext(), "Введите число", Toast.LENGTH_SHORT).show();
                return;
            }

            String menuType = "Menu";
            if (type.equals("Shops")) {
                menuType = "Range";
            }

            // Изменение цены
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            firebaseDatabase
                    .child(type)
                    .child(name)
                    .child(menuType)
                    .child(position.getKey())
                    .child("Price")
                    .setValue(editText.getText().toString());

            Toast.makeText(getContext(), "Цена успешно изменена", Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        });

        return view;
    }

    // Метод для проверки строки является ли она числом
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
