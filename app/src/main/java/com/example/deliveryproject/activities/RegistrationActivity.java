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

        // Слушатель на кнопку регистрации
        findViewById(R.id.a_reg_b_registration).setOnClickListener(view -> {
            // Инициализация всех полей
            String firstName = ((EditText) findViewById(R.id.a_reg_firstName)).getText().toString();
            String secondName = ((EditText) findViewById(R.id.a_reg_secondName)).getText().toString();
            String login = ((EditText) findViewById(R.id.a_reg_login)).getText().toString();
            String password = ((EditText) findViewById(R.id.a_reg_password)).getText().toString();
            String address = ((EditText) findViewById(R.id.a_reg_address)).getText().toString();

            // Вызов метода проверки полей и получение ответа
            String answer = checkForARegistraion(firstName, secondName, login, password, address);

            if (!answer.equals("ok")) {
                // Ошибка в форматировании. Вывод ошибки пользователю
                Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_SHORT).show();
                return;
            }

            // Создание пользователя в базе данных
            mAuth.createUserWithEmailAndPassword(login, password).addOnCompleteListener(this, task -> {
                // Проверка на существующего пользователя
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Такой пользователь уже существует", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Добавление пользователю ФИО
                FirebaseUser user = mAuth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(firstName + " " + secondName)
                        .build();

                // Проверка, что пользователь найден. В противном случае вывод ошибки
                assert user != null;

                // Обновляем профиль только что созданного пользователя и слушаем ответ от базы
                user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        // Не удалось обновить профиль пользователя
                        Toast.makeText(getApplicationContext(), "Не удалось добавить ФИО пользователю", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UserInfo.fUser = user;

                    // Получение ссылки на всех пользователей из базы
                    DatabaseReference usersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");

                    // Создание коллекции ключ-значение пользователя
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Role", "Client");
                    map.put("Cart", "");
                    map.put("History", "");
                    map.put("Address", address);
                    map.put("PrivilegedSettings", "");

                    // Установка значений пользователю
                    usersDbRef.child(user.getUid()).setValue(map);

                    // Получение ссылки базы данных
                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

                    // Получению роли пользователя. Слушатель ответа от базы
                    firebaseDatabase.child("Users").child(UserInfo.fUser.getUid()).child("Role").get().addOnCompleteListener(task2 -> {
                        if (!task2.isSuccessful()) {
                            // Не удалось получить роль пользователя
                            Toast.makeText(getApplicationContext(), "Не удалось получить роль пользователя", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Проверка на роли
                        if (task2.getResult().getValue().equals("Admin")) {
                            // Запуск окна админа
                            startActivity(new Intent(this, AdminMenuActivity.class));
                        } else if (task2.getResult().getValue().equals("Moderator")) {
                            // Запуск окна модератора
                            startActivity(new Intent(this, ModeratorMenuActivity.class));
                        } else {
                            // ЗАпуск окна пользователя
                            startActivity(new Intent(this, UserMenuActivity.class));
                        }
                    });
                });
            });
        });

        // Слушатель на кнопку авторизации
        findViewById(R.id.a_reg_tv_auth).setOnClickListener(view -> {
            // Открытие окна авторизации
            startActivity(new Intent(this, AuthActivity.class));
        });
    }

    private void init() {
        // Получение базы данных
        mAuth = FirebaseAuth.getInstance();
    }

    // Метод для проверки правильности ввода в поля
    private String checkForARegistraion(String firstName, String secondName,
                                        String login, String password,
                                        String address) {
        // Проверка на пустоту полей
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