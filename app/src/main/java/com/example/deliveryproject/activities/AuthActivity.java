package com.example.deliveryproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.deliveryproject.R;
import com.example.deliveryproject.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Init();

        findViewById(R.id.a_auth_b_enter).setOnClickListener(view -> {
            String email = ((EditText)findViewById(R.id.a_auth_login)).getText().toString();
            String password = ((EditText)findViewById(R.id.a_auth_password)).getText().toString();

            String answer = checkForEmailAndPassword(email, password);

            if (!answer.equals("ok")) {
                Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("signin", "signInWithEmail:success");
                    UserInfo.fUser = mAuth.getCurrentUser();

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
                } else {
                    Toast.makeText(getApplicationContext(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                    Log.d("signin", "signInWithEmail:failure", task.getException());
                }
            });
        });

        findViewById(R.id.a_auth_tv_registration).setOnClickListener(view -> {
            startActivity(new Intent(this, RegistrationActivity.class));
        });
    }

    private void Init() {
        mAuth = FirebaseAuth.getInstance();
    }

    private String checkForEmailAndPassword(String email, String password) {
        if (email.length() == 0 && password.length() == 0) {
            return "Неверный формат";
        }
        return "ok";
    }
}