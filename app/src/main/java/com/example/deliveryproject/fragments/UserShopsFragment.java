package com.example.deliveryproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.deliveryproject.Items;
import com.example.deliveryproject.R;
import com.example.deliveryproject.adapters.UserRestaurantsAdapter;
import com.example.deliveryproject.adapters.UserShopsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserShopsFragment extends Fragment {
    ArrayList<DataSnapshot> items;

    public UserShopsFragment(List<DataSnapshot> items) {
        this.items = (ArrayList<DataSnapshot>) items;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shops, container, false);

        ListView listView = view.findViewById(R.id.f_shops_scroll_view);
        listView.setAdapter(new UserRestaurantsAdapter(getContext(), items.toArray()));

        return view;
    }
}
