package com.example.deliveryproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryproject.Items;
import com.example.deliveryproject.R;
import com.example.deliveryproject.adapters.UserProductsAdapter;
import com.example.deliveryproject.adapters.UserRestaurantsAdapter;
import com.example.deliveryproject.adapters.UserSettingsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserProfileFragment extends Fragment {
    FragmentManager fragmentManager;
    FirebaseDatabase firebaseDatabase;

    public UserProfileFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String fullName = firebaseAuth.getCurrentUser().getDisplayName();

        String name = "";
        String abbrev = "";

        // Установка аббревиатуры
        if (fullName.split(" ").length == 1) {
            name = fullName.split(" ")[0];
            abbrev = String.valueOf(fullName.split(" ")[0].charAt(0));
        } else if (fullName.split(" ").length > 1) {
            name = firebaseAuth.getCurrentUser().getDisplayName().split(" ")[0]
                    + " "
                    + firebaseAuth.getCurrentUser().getDisplayName().split(" ")[1];

            abbrev = String.valueOf(fullName.split(" ")[0].charAt(0))
                    + String.valueOf(fullName.split(" ")[1].charAt(0));
        }

        ((TextView)view.findViewById(R.id.f_profile_abbrev_tv)).setText(abbrev);
        ((TextView)view.findViewById(R.id.f_profile_full_name)).setText(name);

        // Настройка listView
        ListView listView = view.findViewById(R.id.f_profile_settings_list);
        listView.setAdapter(new UserSettingsAdapter(container.getContext(), 0, new Items.Setting[] {
                new Items.Setting("История заказов"),
                new Items.Setting("Изменить адрес"),
                new Items.Setting("Выход")
        }, fragmentManager));

        // Установка баланса
        firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid())
                .child("Balance").get().addOnSuccessListener(runnable -> {
                    ((TextView)view.findViewById(R.id.f_profile_balance)).setText(
                            runnable.getValue().toString() + " руб.");
                });

        view.findViewById(R.id.f_profile_replenish).setOnClickListener(view1 -> {
            new UserReplenishBalance().show(fragmentManager, "");
        });

        return view;
    }
}
