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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

            firebase.get().addOnCompleteListener(runnable -> {
                if (!runnable.isSuccessful()) {
                    Toast.makeText(inflater.getContext(), "Плохое соединение с базой", Toast.LENGTH_SHORT).show();
                    this.dismiss();
                    return;
                }

                for (DataSnapshot dataSnapshot : runnable.getResult().child("Restaurants").getChildren()) {
                    if (dataSnapshot.child("Name").getValue().equals(name)) {
                        Toast.makeText(inflater.getContext(), "Ресторан с таким имененем уже существует", Toast.LENGTH_SHORT).show();
                        this.dismiss();
                        return;
                    }
                }

                String moderEmail = ((EditText)view.findViewById(R.id.d_add_rest_email)).getText().toString();

                if (moderEmail.isEmpty()) {
                    Toast.makeText(inflater.getContext(), "Почта модератора не должна быть пустой", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userUid = null;
                for (DataSnapshot dataSnapshot : runnable.getResult().child("Users").getChildren()) {
                    if (dataSnapshot.child("Email").getValue().toString().equals(moderEmail)) {
                        userUid = dataSnapshot.getKey();
                        if (dataSnapshot.child("PrivilegedSettings").getValue().equals("")) {

                            // Создание и добавление ресторана
                            addRest(name, firebase);

                            // Добавление модератора
                            settingClient(name, userUid);

                            Toast.makeText(inflater.getContext(), "Ресторан успешно добавлен", Toast.LENGTH_SHORT).show();
                            this.dismiss();
                        } else {
                            Toast.makeText(inflater.getContext(), "Этот пользователь модерирует другое заведение", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    }
                }

                if (userUid == null) {
                    Toast.makeText(inflater.getContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show();
                    return;
                }
            });
        });

        return view;
    }

    private void addRest(String name, DatabaseReference firebase) {
        HashMap<String, String> rest = new HashMap<>();
        rest.put("Menu", "");
        rest.put("Name", name);
        rest.put("Photo path", "");
        rest.put("isOpen", "false");
        firebase.child("Restaurants").child(name).setValue(rest);
    }

    private void settingClient(String name, String userUid) {
        HashMap<String, String> privilegedSettings = new HashMap<>();
        privilegedSettings.put("Name", name);
        privilegedSettings.put("Type", "Rest");

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userUid);
        userRef.child("PrivilegedSettings").setValue(privilegedSettings);
        userRef.child("Role").setValue("Moderator");
    }
}
