package com.example.deliveryproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.deliveryproject.R;
import com.example.deliveryproject.UserInfo;

public class MenuActivity extends AppCompatActivity {

    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init();

        System.out.println(UID);
    }

    private void init() {
        UID = UserInfo.fUser.getUid();
    }
}