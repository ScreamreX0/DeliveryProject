package com.example.deliveryproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryproject.R;
import com.example.deliveryproject.adapters.UserDishesAdapter;
import com.example.deliveryproject.adapters.UserProductsAdapter;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserDishesFragment extends Fragment {
    ArrayList<DataSnapshot> items;
    UserDishesAdapter adapter;
    String shopName;

    public UserDishesFragment(List<DataSnapshot> items, String shopName) {
        this.items = (ArrayList<DataSnapshot>) items;
        this.shopName = shopName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dishes, container, false);

        adapter = new UserDishesAdapter(items, getContext(), shopName);

        // Настройка recyclerView
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        RecyclerView recyclerView = view.findViewById(R.id.f_dishes_recycle_view);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Настройка поиска
        SearchView searchView = view.findViewById(R.id.f_dishes_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                UserDishesFragment.this.adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                UserDishesFragment.this.adapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }
}
