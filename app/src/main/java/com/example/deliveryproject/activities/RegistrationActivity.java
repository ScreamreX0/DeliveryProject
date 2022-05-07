package com.example.deliveryproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.deliveryproject.R;
import com.example.deliveryproject.User;
import com.example.deliveryproject.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();

        findViewById(R.id.a_reg_b_registration).setOnClickListener(view -> {
            String firstName = ((EditText) findViewById(R.id.a_reg_firstName)).getText().toString();
            String secondName = ((EditText) findViewById(R.id.a_reg_secondName)).getText().toString();
            String login = ((EditText) findViewById(R.id.a_reg_login)).getText().toString();
            String password = ((EditText) findViewById(R.id.a_reg_password)).getText().toString();
            String address = ((EditText) findViewById(R.id.a_reg_address)).getText().toString();

            String answer = checkForARegistraion(firstName, secondName, login, password, address);

            if (!answer.equals("ok")) {
                Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_SHORT).show();
                return;
            }

            // Создаем пользователя
            mAuth.createUserWithEmailAndPassword(login, password).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful()) {
                    Log.d("signup", "createUserWithEmail:failure", task.getException());
                    return;
                }
                Log.d("signup", "createUserWithEmail:success");

                // Добавляем пользователю имя, фамилию, отчество
                FirebaseUser user = mAuth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(firstName + " " + secondName)
                        .build();

                assert user != null;
                user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        Log.d("signup", "updateProfile:failure");
                        return;
                    }

                    Log.d("signup", "updateProfile:success");
                    UserInfo.fUser = user;

                    // Добавляем пользователю роль
                    DatabaseReference usersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");

                    HashMap<String, String> map = new HashMap<>();
                    map.put("Role", "Client");
                    map.put("Shop", "");
                    map.put("History", "");
                    map.put("Address", address);
                    usersDbRef.child(user.getUid()).setValue(map);

                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
                    firebaseDatabase.child("Users").child(UserInfo.fUser.getUid()).child("Role").get().addOnCompleteListener(task2 -> {
                        if (!task2.isSuccessful()) {
                            System.out.println(task2.getException());
                            return;
                        }

                        if (task2.getResult().getValue().equals("Admin")) {
                            startActivity(new Intent(this, AdminMenuActivity.class));
                        } else if (task2.getResult().getValue().equals("Moderator")) {
                            startActivity(new Intent(this, ModeratorMenuActivity.class));
                        } else {
                            startActivity(new Intent(this, UserMenuActivity.class));
                        }
                    });
                });
            });
        });

        findViewById(R.id.a_reg_tv_auth).setOnClickListener(view -> {
            startActivity(new Intent(this, AuthActivity.class));
        });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
    }

    private String checkForARegistraion(String firstName,
                                        String secondName,
                                        String login,
                                        String password,
                                        String address) {
        if (firstName.length() == 0
                || secondName.length() == 0
                || login.length() == 0
                || address.length() == 0
                || password.length() == 0) {
            return "Заполните все поля";
        }
        return "ok";
    }
}