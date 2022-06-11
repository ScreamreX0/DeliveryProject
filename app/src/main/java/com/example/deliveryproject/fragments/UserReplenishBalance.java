package com.example.deliveryproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.deliveryproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserReplenishBalance extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_replenish_balance, container, false);

        // Слушатель кнопки ОТМЕНА
        view.findViewById(R.id.f_replenish_balance_cancel).setOnClickListener(view1 -> {
            dismiss();
        });

        // Слушатель кнопки ОК
        view.findViewById(R.id.f_replenish_balance_apply).setOnClickListener(view1 -> {
            TextView cartNumberTextView = view.findViewById(R.id.f_replenish_balance_cart_number);
            TextView sumTextView = view.findViewById(R.id.f_replenish_balance_sum);

            // Проверки на форматы
            if (cartNumberTextView.getText().toString().isEmpty()
                    || cartNumberTextView.getText().toString().length() != 16) {
                Toast.makeText(inflater.getContext(), "Неверный формат банковской карты", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isNumber(sumTextView.getText().toString())) {
                Toast.makeText(inflater.getContext(), "Неверный формат суммы пополнения", Toast.LENGTH_SHORT).show();
                return;
            }

            float sum = Float.parseFloat(sumTextView.getText().toString());

            if (sum < 0) {
                Toast.makeText(inflater.getContext(), "Сумма пополнения не должна быть меньше нуля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (sum > 1000000) {
                Toast.makeText(inflater.getContext(), "Сумма пополнения не должна быть больше 1 млн.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Получение текущего баланса пользователя
            DatabaseReference balanceRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child("Balance");

            // Добавление к текущему балансу введенной суммы
            balanceRef.get().addOnSuccessListener(runnable -> {
                balanceRef.setValue(Float.parseFloat(runnable.getValue().toString()) + sum);
            });

            dismiss();
        });

        return view;
    }

    // Метод для проверки строки является ли она числом
    private boolean isNumber(String number) {
        try {
            Float.parseFloat(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
