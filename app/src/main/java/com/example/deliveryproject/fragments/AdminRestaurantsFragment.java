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

import com.example.deliveryproject.R;
import com.example.deliveryproject.adapters.AdminRestaurantsAdapter;
import com.example.deliveryproject.adapters.AdminShopsAdapter;
import com.example.deliveryproject.adapters.UserRestaurantsAdapter;
import com.example.deliveryproject.adapters.UserShopsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminRestaurantsFragment extends Fragment {
    ArrayList<DataSnapshot> items;
    FragmentManager fragmentManager;
    AdminRestaurantsAdapter adapter;

    public AdminRestaurantsFragment(List<DataSnapshot> items,
                              FragmentManager fragmentManager) {
        this.items = (ArrayList<DataSnapshot>) items;
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_restaurants, container, false);

        ListView listView = view.findViewById(R.id.f_admin_restaurants_scroll_view);

        this.adapter = new AdminRestaurantsAdapter(
                getContext(),
                items.toArray(),
                fragmentManager,
                getActivity()
        );
        listView.setAdapter(this.adapter);

        SearchView searchView = view.findViewById(R.id.f_admin_restaurants_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                AdminRestaurantsFragment.this.adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                AdminRestaurantsFragment.this.adapter.getFilter().filter(newText);
                return false;
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.f_admin_rests_floating_button);
        fab.setOnClickListener(view1 -> {
            AdminAddRestFragment adminEditRestFragment = new AdminAddRestFragment();
            adminEditRestFragment.show(fragmentManager, "");
        });

        return view;
    }

}
