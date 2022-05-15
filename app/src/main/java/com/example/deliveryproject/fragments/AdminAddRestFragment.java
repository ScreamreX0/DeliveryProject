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

public class AdminAddRestFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_rest, container, false);

        view.findViewById(R.id.d_add_rest_cancel_button).setOnClickListener(view1 -> {
            this.dismiss();
        });

        view.findViewById(R.id.d_add_rest_ok_button).setOnClickListener(view1 -> {
            String name = ((EditText)view.findViewById(R.id.d_add_rest_name)).getText().toString();
            if (name.replace(" ", "").equals("")) {
                Toast.makeText(inflater.getContext(), "Поле должно быть заполнено", Toast.LENGTH_SHORT).show();
                this.dismiss();
                return;
            }

            DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();

            firebase.child("Restaurants").get().addOnCompleteListener(runnable -> {
                if (!runnable.isSuccessful()) {
                    Toast.makeText(inflater.getContext(), "Плохое соединение с базой", Toast.LENGTH_SHORT).show();
                    this.dismiss();
                    return;
                }

                for (DataSnapshot dataSnapshot : runnable.getResult().getChildren()) {
                    if (dataSnapshot.child("Name").getValue().equals(name)) {
                        Toast.makeText(inflater.getContext(), "Ресторан с таким имененем уже существует", Toast.LENGTH_SHORT).show();
                        this.dismiss();
                        return;
                    }
                }

                HashMap<String, String> rest = new HashMap<>();
                rest.put("Menu", "");
                rest.put("Name", name);
                rest.put("Photo path", "");
                rest.put("isOpen", "false");
                firebase.child("Restaurants").child(name).setValue(rest);

                Toast.makeText(inflater.getContext(), "Ресторан успешно добавлен", Toast.LENGTH_SHORT).show();

                this.dismiss();
            });
        });

        return view;
    }
}
