package com.example.deliveryproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.deliveryproject.R;
import com.example.deliveryproject.adapters.UserShopsAdapter;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRestaurantsFragment extends Fragment {
    ArrayList<DataSnapshot> items;

    public UserRestaurantsFragment(List<DataSnapshot> items) {
        this.items = (ArrayList<DataSnapshot>) items;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants, container, false);

        ListView listView = view.findViewById(R.id.f_restaurants_scroll_view);
        listView.setAdapter(new UserShopsAdapter(getContext(), items.toArray()));

        return view;
    }
}
