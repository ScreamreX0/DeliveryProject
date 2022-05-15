package com.example.deliveryproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deliveryproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Init();

        // Слушатель кнопки "Войти"
        findViewById(R.id.a_auth_b_enter).setOnClickListener(view -> {
            String email = ((EditText)findViewById(R.id.a_auth_login)).getText().toString();
            String password = ((EditText)findViewById(R.id.a_auth_password)).getText().toString();

            // Проверка полей логина и пароля
            String answer = checkForEmailAndPassword(email, password);

            if (!answer.equals("ok")) {
                // Обнаружен неверный формат ввода в поля. Вывод ошибки пользователю
                Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_SHORT).show();
                return;
            }

            // Авторизация с помощью базы данных. Слушатель ответа от базы
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful()) {
                    // Неверный логин или пароль
                    Toast.makeText(getApplicationContext(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Получение ссылки базы данных
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

                // Получение информации о пользователе. Слушатель ответа от базы
                firebaseDatabase.child("Users").child(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(task2 -> {
                    if (!task2.isSuccessful()) {
                        // Не получилось получить информацию.
                        Toast.makeText(getApplicationContext(), "Не получилось получить информацию о пользователе", Toast.LENGTH_SHORT).show();
                        System.out.println(task2.getException());
                        return;
                    }

                    // Получаем снимок пользователя со всеми данными
                    DataSnapshot user = task2.getResult();

                    // Получаем роль пользователя
                    String role = user.child("Role").getValue().toString();

                    // Проверка на роль
                    if (role.equals("Admin")) {
                        // Запуск окна администратора
                        startActivity(new Intent(this, AdminMenuActivity.class));
                    } else if (role.equals("Moderator")) {
                        // Запуск окна модератора
                        startActivity(new Intent(this, ModeratorMenuActivity.class));
                    } else {
                        // Запуск окна пользователя
                        startActivity(new Intent(this, UserMenuActivity.class));
                    }
                });
            });
        });

        // Слушатель кнопки "Регистрация"
        findViewById(R.id.a_auth_tv_registration).setOnClickListener(view -> {
            // Открытие окна регистрации
            startActivity(new Intent(this, RegistrationActivity.class));
        });
    }

    private void Init() {
        // Получение ссылки на базу данных
        mAuth = FirebaseAuth.getInstance();
    }

    // Метод для проверки правильности ввода данных в поля
    private String checkForEmailAndPassword(String email, String password) {
        // Проверка на пустоту полей
        if (email.length() == 0 && password.length() == 0) {
            return "Неверный формат";
        }
        return "ok";
    }

    @Override
    public void onBackPressed() {

    }
}