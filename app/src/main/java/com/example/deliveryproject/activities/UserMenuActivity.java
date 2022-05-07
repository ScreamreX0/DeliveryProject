package com.example.deliveryproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

        BottomNavigationView bottomNavigationMenuView = findViewById(R.id.a_user_bottom_menu);

        openShops();

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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.a_user_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void openProfile() {
        replaceFragment(new UserProfileFragment(getSupportFragmentManager()));
    }

    private void openShops() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference shopsRef = databaseReference.child("Shops");

        shopsRef.get().addOnCompleteListener(task -> {
            List<DataSnapshot> arr = new ArrayList<>();
            Iterable<DataSnapshot> items = task.getResult().getChildren();

            for (DataSnapshot dataSnapshot : items) {
                arr.add(dataSnapshot);
            }
            replaceFragment(new UserShopsFragment(arr));
        });
    }

    private void openRests() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference shopsRef = databaseReference.child("Restaurants");

        shopsRef.get().addOnCompleteListener(task -> {
            List<DataSnapshot> arr = new ArrayList<>();
            Iterable<DataSnapshot> items = task.getResult().getChildren();

            for (DataSnapshot dataSnapshot : items) {
                arr.add(dataSnapshot);
            }
            replaceFragment(new UserRestaurantsFragment(arr));
        });
    }

    private void openCart() {
        replaceFragment(new UserCartFragment(
            getApplicationContext().getSharedPreferences("Cart", Context.MODE_PRIVATE),
            getSupportFragmentManager(),
            new UserEmptyCartFragment()
        ));
    }
}