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
import androidx.fragment.app.FragmentManager;

import com.example.deliveryproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminEditShopFragment extends DialogFragment {
    String shopKey;
    String shopName;

    public AdminEditShopFragment(String shopKey, String shopName) {
        this.shopKey = shopKey;
        this.shopName = shopName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_shop, container, false);

        Button cancel = view.findViewById(R.id.d_edit_shop_cancel);
        Button ok = view.findViewById(R.id.d_edit_shop_ok);
        EditText editText = view.findViewById(R.id.d_edit_shop_edit);

        editText.setText(shopName);

        // Слушатель кнопки ОТМЕНА
        cancel.setOnClickListener(view1 -> {
            getDialog().dismiss();
        });

        // Слушатель кнопки ОК
        ok.setOnClickListener(view1 -> {
            if (editText.getText().toString().replace(" ", "").equals("")) {
                Toast.makeText(getContext(), "Поле должно быть заполнено", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editText.getText().toString().equals(shopName)) {
                this.dismiss();
                return;
            }

            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

            firebaseDatabase.child("Shops").child(shopKey).child("Name").setValue(editText.getText().toString());

            Toast.makeText(getContext(), "Название успешно изменено", Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        });

        return view;
    }
}
