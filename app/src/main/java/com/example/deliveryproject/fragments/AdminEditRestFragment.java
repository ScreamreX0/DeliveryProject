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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminEditRestFragment extends DialogFragment {
    public AdminEditRestFragment(String restKey, String restName) {
        this.restKey = restKey;
        this.restName = restName;
    }

    String restKey;
    String restName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_rest, container, false);

        Button cancel = view.findViewById(R.id.d_edit_rest_cancel);
        Button ok = view.findViewById(R.id.d_edit_rest_ok);
        EditText editText = view.findViewById(R.id.d_edit_rest_edit);

        editText.setText(restName);

        cancel.setOnClickListener(view1 -> {
            getDialog().dismiss();
        });

        ok.setOnClickListener(view1 -> {
            if (editText.getText().toString().replace(" ", "").equals("")) {
                Toast.makeText(getContext(), "Поле должно быть заполнено", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editText.getText().toString().equals(restName)) {
                this.dismiss();
                return;
            }

            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

            firebaseDatabase.child("Restaurants").child(restKey).child("Name").setValue(editText.getText().toString());

            Toast.makeText(getContext(), "Название успешно изменено", Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        });

        return view;
    }
}
