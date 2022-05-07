package com.example.deliveryproject.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.deliveryproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserChangeAddressDialogFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_address, container, false);

        EditText editText = view.findViewById(R.id.d_change_address_field);

        view.findViewById(R.id.d_change_address_cancel).setOnClickListener(view1 -> {
            getDialog().dismiss();
        });

        view.findViewById(R.id.d_change_address_change).setOnClickListener(view1 -> {
            if (editText.getText().toString().replace(" ", "").equals("")) {
                Toast.makeText(getContext(), "Поле должно быть заполнено", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            firebaseDatabase.child("Users").child(firebaseAuth.getUid()).child("Address").setValue(editText.getText().toString());

            Toast.makeText(getContext(), "Адрес успешно изменен", Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        });

        return view;
    }
}
