package com.example.deliveryproject.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.deliveryproject.R;
import com.example.deliveryproject.fragments.UserCartFragment;
import com.example.deliveryproject.fragments.UserEmptyCartFragment;
import com.example.deliveryproject.fragments.UserProfileFragment;
import com.example.deliveryproject.fragments.UserRestaurantsFragment;
import com.example.deliveryproject.fragments.UserShopsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        // Получение нижней панели навигации
        BottomNavigationView bottomNavigationMenuView = findViewById(R.id.a_user_bottom_menu);

        openShops();

        // Слушатель нижней панели навигации
        bottomNavigationMenuView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_shops) {
                openShops();
            } else if (item.getItemId() == R.id.bottom_restaurants) {
                openRests();
            } else if (item.getItemId() == R.id.bottom_cart) {
                openCart();
            } else if (item.getItemId() == R.id.bottom_account) {
                openProfile();
            }
            return true;
        });
    }

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // Метод для установки фрагментов
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.a_user_fragment, fragment);
        fragmentTransaction.commit();
    }

    // Метод для открытия профиля
    private void openProfile() {
        replaceFragment(new UserProfileFragment(getSupportFragmentManager()));
    }

    // Метод для открытия магазинов
    private void openShops() {
        // Получение ссылки магазинов из базы
        DatabaseReference shopsRef = databaseReference.child("Shops");

        // Получение результатов. Слушатель ответа от базы
        shopsRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                // Не удалось загрузить список магазинов
                Toast.makeText(this, "Не удалось загрузить список магазинов", Toast.LENGTH_SHORT).show();
                return;
            }

            List<DataSnapshot> arr = new ArrayList<>();
            Iterable<DataSnapshot> items = task.getResult().getChildren();

            // Конвертация списка магазинов из Iterable<DataSnapshot> в List<DataSnapshot>
            for (DataSnapshot dataSnapshot : items) {
                arr.add(dataSnapshot);
            }

            // Запуск фрагмента магазинов
            replaceFragment(new UserShopsFragment(arr));
        });
    }

    // Метод для открытия ресторанов
    private void openRests() {
        // Получение ссылки ресторанов из базы
        DatabaseReference shopsRef = databaseReference.child("Restaurants");

        // Получение результатов. Слушатель ответа от базы
        shopsRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                // Не удалось загрузить список ресторанов
                Toast.makeText(this, "Не удалось загрузить список ресторанов", Toast.LENGTH_SHORT).show();
                return;
            }

            List<DataSnapshot> arr = new ArrayList<>();
            Iterable<DataSnapshot> items = task.getResult().getChildren();

            // Конвертация списка ресторанов из Iterable<DataSnapshot> в List<DataSnapshot>
            for (DataSnapshot dataSnapshot : items) {
                arr.add(dataSnapshot);
            }

            // Запуск фрагмента ресторанов
            replaceFragment(new UserRestaurantsFragment(arr));
        });
    }

    // Метод для открытия корзины
    private void openCart() {
        replaceFragment(new UserCartFragment(
            getApplicationContext().getSharedPreferences("Cart", Context.MODE_PRIVATE),
            getSupportFragmentManager(),
            new UserEmptyCartFragment()
        ));
    }
}