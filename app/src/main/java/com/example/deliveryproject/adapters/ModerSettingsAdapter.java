package com.example.deliveryproject.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.deliveryproject.Items;
import com.example.deliveryproject.R;
import com.example.deliveryproject.activities.AuthActivity;
import com.example.deliveryproject.fragments.UserChangeAddressDialogFragment;
import com.example.deliveryproject.fragments.UserDishesFragment;
import com.example.deliveryproject.fragments.UserHistoryFragment;
import com.example.deliveryproject.fragments.UserRestaurantsFragment;
import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ModerSettingsAdapter extends ArrayAdapter<Items.Setting> {
    Items.Setting[] settings;
    Context context;
    FragmentManager fragmentManager;

    public ModerSettingsAdapter(@NonNull Context context,
                                int resource,
                                @NonNull Items.Setting[] objects,
                                FragmentManager fragmentManager) {
        super(context, resource, objects);

        this.settings = objects;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    // Метод для получения представления
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.item_setting, parent, false);
        TextView name = row.findViewById(R.id.i_setting_label);

        name.setText(settings[position].getLabel());

        // Слушатели на настройки
        if (settings[position].getLabel().equals("Изменить адрес")) {
            row.setOnClickListener(view -> {
                changeAddress();
            });
        } else if (settings[position].getLabel().equals("Выход")) {
            row.setOnClickListener(view -> {
                context.startActivity(new Intent(context, AuthActivity.class));
                FirebaseAuth.getInstance().signOut();
            });
        }

        return row;
    }

    // Метод для смены адреса
    private void changeAddress() {
        UserChangeAddressDialogFragment userChangeAddressDialogFragment = new UserChangeAddressDialogFragment();
        userChangeAddressDialogFragment.show(fragmentManager, "");
    }
}
