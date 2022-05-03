package com.example.deliveryproject.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.deliveryproject.Items;

public class UserCartAdapter extends ArrayAdapter<Items.IListItems> {
    Context context;
    Items.Shop[] items;

    public UserCartAdapter(@NonNull Context context, @NonNull Items.IListItems[] objects) {
        super(context, 0, objects);


    }
}
