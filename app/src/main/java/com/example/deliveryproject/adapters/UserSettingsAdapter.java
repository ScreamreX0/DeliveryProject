package com.example.deliveryproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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

public class UserSettingsAdapter extends ArrayAdapter<Items.Setting> {
    public UserSettingsAdapter(@NonNull Context context,
                               int resource,
                               @NonNull Items.Setting[] objects,
                               FragmentManager fragmentManager) {
        super(context, resource, objects);

        this.settings = objects;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    Items.Setting[] settings;
    Context context;
    FragmentManager fragmentManager;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.item_setting, parent, false);
        TextView name = row.findViewById(R.id.i_setting_label);

        name.setText(settings[position].getLabel());

        if (settings[position].getLabel().equals("История заказов")) {
            row.setOnClickListener(view -> {
                showHistory();
            });
        }
        if (settings[position].getLabel().equals("Изменить адрес")) {
            row.setOnClickListener(view -> {
                changeAddress();
            });
        } else if (settings[position].getLabel().equals("Изменить имя")) {
            row.setOnClickListener(view -> {
                changeName();
            });
        } else if (settings[position].getLabel().equals("Изменить пароль")) {
            row.setOnClickListener(view -> {
                changePassword();
            });
        }

        return row;
    }

    // Метод для открытия окна с историей покупок
    private void showHistory() {
        String firebaseAuth = FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference historyRef = databaseReference
                .child("Users")
                .child(firebaseAuth)
                .child("History");

        historyRef.get().addOnCompleteListener(task -> {
            List<DataSnapshot> arr = new ArrayList<>();
            Iterable<DataSnapshot> items = task.getResult().getChildren();

            for (DataSnapshot dataSnapshot : items) {
                arr.add(dataSnapshot);
            }

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.a_user_fragment, new UserHistoryFragment(arr));
            fragmentTransaction.commit();
        });
    }

    // Метод для открытия фрагмента со сменой адреса
    private void changeAddress() {
        UserChangeAddressDialogFragment userChangeAddressDialogFragment = new UserChangeAddressDialogFragment();
        userChangeAddressDialogFragment.show(fragmentManager, "");
    }

    private void changeName() {

    }

    private void changePassword() {

    }
}
