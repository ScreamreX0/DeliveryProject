package com.example.deliveryproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserOrderingActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText entranceEditText;
    EditText intercomEditText;
    EditText flatEditText;
    EditText floorEditText;
    EditText commentEditText;
    EditText phoneNumberEditText;
    TextView priceTextView;
    TextView deliveryPriceTextView;
    TextView addressTextView;
    Button confirmButton;

    SharedPreferences sharedPreferences;
    DatabaseReference firebaseDatabase;
    FirebaseAuth firebaseAuth;

    float totalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ordering);
        init();

        // Настройка toolbar'а
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Установка общей стоимости заказа
        priceTextView.setText(totalSum + " руб.");

        // Установка цены за доставку
        deliveryPriceTextView.setText("Бесплатно");

        // Получение адреса пользователя
        firebaseDatabase.child("Users").child(firebaseAuth.getUid()).child("Address").get().addOnSuccessListener(runnable -> {
            // Установка адреса пользователя
            addressTextView.setText(runnable.getValue().toString());
        });

        // Слушатель на кнопку подтверждения
        confirmButton.setOnClickListener(view -> {
            // Проверка полей
            String checkResult = checkValues();
            if (!checkResult.equals("ok")) {
                Toast.makeText(this, checkResult, Toast.LENGTH_SHORT).show();
                return;
            }

            // Получение баланса пользователя
            firebaseDatabase.child("Users")
                    .child(firebaseAuth.getUid())
                    .child("Balance")
                    .get()
                    .addOnSuccessListener(runnable -> {
                        float currentBalance = Float.parseFloat(runnable.getValue().toString());

                        // Проверка платежеспособности
                        if (currentBalance >= totalSum) {
                            // Ввод в базу истории заказов
                            insertToHistory(currentBalance);

                            // Очищение корзины
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();

                            Toast.makeText(this, "Заказ принят", Toast.LENGTH_SHORT).show();

                            // Запуск окна пользовательского меню
                            startActivity(new Intent(this, UserMenuActivity.class));
                        } else {
                            Toast.makeText(this, "Недостаточно средств", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void init() {
        toolbar = findViewById(R.id.a_user_ordering_tool_bar);
        entranceEditText = findViewById(R.id.a_user_ordering_entrance);
        intercomEditText = findViewById(R.id.a_user_ordering_intercom);
        flatEditText = findViewById(R.id.a_user_ordering_flat);
        floorEditText = findViewById(R.id.a_user_ordering_floor);
        commentEditText = findViewById(R.id.a_user_ordering_comment);
        phoneNumberEditText = findViewById(R.id.a_user_ordering_phone_number);
        priceTextView = findViewById(R.id.a_user_ordering_price);
        deliveryPriceTextView = findViewById(R.id.a_user_ordering_delivery_price);
        confirmButton = findViewById(R.id.a_user_ordering_confirm);
        addressTextView = findViewById(R.id.a_user_ordering_address);

        sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        totalSum = getTotalSum();
    }

    // Метод для проверки введенных полей
    private String checkValues() {
        // Entrance check
        String entrance = entranceEditText.getText().toString();
        if (entrance.isEmpty()) {
            return "Введите номер подъезда";
        }

        // Intercom check
        String intercom = intercomEditText.getText().toString();
        if (intercom.isEmpty()) {
            return "Введите домофон";
        }

        // Flat check
        String flat = flatEditText.getText().toString();
        if (flat.isEmpty()) {
            return "Введите номер квартиры/офиса";
        }

        // Floor check
        String floor = floorEditText.getText().toString();
        if (floor.isEmpty()) {
            return "Введите этаж";
        }

        // Phone check
        String phoneNumber = phoneNumberEditText.getText().toString();
        if (!phoneNumber.contains("+")) {
            return "Неверный формат номера телефона";
        }

        return "ok";
    }

    // Метод для получения суммы заказа
    private float getTotalSum() {
        return (float) getIntent().getExtras().get("TotalSum");
    }

    // Метод для записи истории заказов
    @SuppressLint("SimpleDateFormat")
    private void insertToHistory(float currentBalance) {
        List<String> keys = new ArrayList<>(sharedPreferences.getAll().keySet());
        List<String> values = new ArrayList<>((Collection<? extends String>) sharedPreferences.getAll().values());

        HashMap<String, Object> historyMap = new HashMap<>();
        historyMap.put("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        historyMap.put("Products", getHistoryProducts(keys, values));

        firebaseDatabase.child("Users")
                .child(firebaseAuth.getUid())
                .child("History")
                .push()
                .setValue(historyMap);

        balanceSubtract(currentBalance);
    }

    // Метод для получения hashmap'а истории продуктов для последующей записи в базу
    private HashMap<String, HashMap<String, String>> getHistoryProducts(List<String> keys, List<String> values) {
        HashMap<String, HashMap<String, String>> productsMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            HashMap<String, String> productMap = new HashMap<>();
            productMap.put("Name", keys.get(i));
            productMap.put("Photo path", values.get(i).split(";")[0]);
            productMap.put("Price", values.get(i).split(";")[1]);
            productMap.put("Count", values.get(i).split(";")[2]);
            productMap.put("Shop", values.get(i).split(";")[3]);

            productsMap.put("Product" + i, productMap);
        }
        return productsMap;
    }

    // Метод для вычета из баланса суммы заказа
    private void balanceSubtract(float currentBalance) {
        firebaseDatabase.child("Users")
                .child(firebaseAuth.getUid())
                .child("Balance")
                .setValue(currentBalance - totalSum);
    }
}