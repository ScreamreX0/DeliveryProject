package com.example.deliveryproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.example.deliveryproject.R;
import com.example.deliveryproject.User;
import com.example.deliveryproject.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();

        findViewById(R.id.a_reg_b_registration).setOnClickListener(view -> {
            String firstName = ((EditText)findViewById(R.id.a_reg_firstName)).getText().toString();
            String secondName = ((EditText)findViewById(R.id.a_reg_secondName)).getText().toString();
            String lastName = ((EditText)findViewById(R.id.a_reg_lastName)).getText().toString();
            String login = ((EditText)findViewById(R.id.a_reg_login)).getText().toString();
            String password = ((EditText)findViewById(R.id.a_reg_password)).getText().toString();

            String answer = checkForARegistraion(firstName, secondName, lastName, login, password);

            if (!answer.equals("ok")) {
                Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(login, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("signup", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(firstName + " " + secondName + " " + lastName)
                            .build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d("signup", "updateProfile:success");

                            UserInfo.fUser = user;

                            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                        } else {
                            Log.d("signup", "updateProfile:failure");
                        }
                    });

                } else {
                    Log.d("signup", "createUserWithEmail:failure", task.getException());
                }
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
                                        String lastName,
                                        String login,
                                        String password) {
        if (firstName.length() == 0
                || secondName.length() == 0
                || lastName.length() == 0
                || login.length() == 0
                || password.length() == 0) {
            return "Заполните все поля";
        }
        return "ok";
    }
}