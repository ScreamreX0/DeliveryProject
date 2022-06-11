package com.example.deliveryproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryproject.R;
import com.example.deliveryproject.adapters.ModerPositionsAdapter;
import com.example.deliveryproject.adapters.UserProductsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ModerPositionsFragment extends Fragment {
    ArrayList<DataSnapshot> items;
    ModerPositionsAdapter adapter;

    FragmentManager fragmentManager;
    String type;
    String name;

    public ModerPositionsFragment(List<DataSnapshot> items,
                                  FragmentManager fragmentManager,
                                  String type,
                                  String name) {
        this.items = (ArrayList<DataSnapshot>) items;
        this.fragmentManager = fragmentManager;
        this.type = type;
        this.name = name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moder_positions, container, false);

        // Получение ссылки на заведение
        DatabaseReference menuRef;
        if (type.equals("Shop")) {
            menuRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(name).child("Range");
            this.adapter = new ModerPositionsAdapter(
                    items,
                    fragmentManager,
                    getContext(),
                    menuRef,
                    "Shops",
                    name,
                    getActivity());
        } else {
            menuRef = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(name).child("Menu");
            this.adapter = new ModerPositionsAdapter(
                    items,
                    fragmentManager,
                    getContext(),
                    menuRef,
                    "Restaurants",
                    name,
                    getActivity());
        }

        // Настройка recyclerView
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        RecyclerView recyclerView = view.findViewById(R.id.f_moder_positions_recycle_view);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Настройка поиска
        SearchView searchView = view.findViewById(R.id.f_moder_positions_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ModerPositionsFragment.this.adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ModerPositionsFragment.this.adapter.getFilter().filter(newText);
                return false;
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.f_moder_positions_floating_button);

        // Слушатель нажатия на кнопку добавления позиции
        fab.setOnClickListener(view1 -> {
            ModerAddPositionFragment moderAddPositionFragment = new ModerAddPositionFragment(
                    menuRef
            );
            moderAddPositionFragment.show(fragmentManager, "");
        });

        return view;
    }
}
