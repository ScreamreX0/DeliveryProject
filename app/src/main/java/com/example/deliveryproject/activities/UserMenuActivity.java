package com.example.deliveryproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.deliveryproject.R;
import com.example.deliveryproject.UserInfo;
import com.example.deliveryproject.fragments.UserCartFragment;
import com.example.deliveryproject.fragments.UserRestaurantsFragment;
import com.example.deliveryproject.fragments.UserShopsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        BottomNavigationView bottomNavigationMenuView = findViewById(R.id.a_user_bottom_menu);

        bottomNavigationMenuView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_shops) {
                replaceFragment(new UserShopsFragment());
            } else if (item.getItemId() == R.id.bottom_restaurants) {
                replaceFragment(new UserRestaurantsFragment());
            } else if (item.getItemId() == R.id.bottom_cart) {
                replaceFragment(new UserCartFragment());
            }

            return true;
        });
    }

    private void init() {

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.a_user_fragment, fragment);
        fragmentTransaction.commit();
    }
}