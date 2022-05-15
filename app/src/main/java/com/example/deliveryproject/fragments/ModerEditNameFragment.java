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

public class ModerEditNameFragment extends DialogFragment {
    public ModerEditNameFragment(DataSnapshot position, String type, String name) {
        this.position = position;
        this.type = type;
        this.name = name;
    }

    DataSnapshot position;
    String type;
    String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_moder_edit_name, container, false);

        Button cancel = view.findViewById(R.id.d_moder_edit_name_cancel);
        Button ok = view.findViewById(R.id.d_moder_edit_name_ok);
        EditText editText = view.findViewById(R.id.d_moder_edit_name_edit);

        editText.setText(position.child("Name").getValue().toString());

        cancel.setOnClickListener(view1 -> {
            getDialog().dismiss();
        });

        ok.setOnClickListener(view1 -> {
            if (editText.getText().toString().replace(" ", "").equals("")) {
                Toast.makeText(getContext(), "Поле должно быть заполнено", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editText.getText().toString().equals(position.child("Name").getValue().toString())) {
                this.dismiss();
                return;
            }

            String menuType = "Menu";
            if (type.equals("Shops")) {
                menuType = "Range";
            }

            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            firebaseDatabase
                    .child(type)
                    .child(name)
                    .child(menuType)
                    .child(position.getKey())
                    .child("Name")
                    .setValue(editText.getText().toString());

            Toast.makeText(getContext(), "Название успешно изменено", Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        });

        return view;
    }
}
