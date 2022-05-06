package com.example.deliveryproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryproject.R;
import com.example.deliveryproject.adapters.UserProductsAdapter;
import com.example.deliveryproject.adapters.UserShopsAdapter;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserProductsFragment extends Fragment {
    ArrayList<DataSnapshot> items;

    public UserProductsFragment(List<DataSnapshot> items) {
        this.items = (ArrayList<DataSnapshot>) items;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        UserProductsAdapter adapter = new UserProductsAdapter(items, getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        RecyclerView recyclerView = view.findViewById(R.id.f_products_recycle_view);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
