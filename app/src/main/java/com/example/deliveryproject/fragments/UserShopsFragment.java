package com.example.deliveryproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.deliveryproject.Items;
import com.example.deliveryproject.R;
import com.example.deliveryproject.adapters.AdminShopsAdapter;
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
    UserShopsAdapter adapter;

    public UserShopsFragment(List<DataSnapshot> items) {
        this.items = (ArrayList<DataSnapshot>) items;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shops, container, false);

        ListView listView = view.findViewById(R.id.f_shops_scroll_view);
        this.adapter = new UserShopsAdapter(getContext(), items.toArray());
        listView.setAdapter(this.adapter);

        SearchView searchView = view.findViewById(R.id.f_shops_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                UserShopsFragment.this.adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                UserShopsFragment.this.adapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }
}
