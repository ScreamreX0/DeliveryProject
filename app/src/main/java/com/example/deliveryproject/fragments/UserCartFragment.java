package com.example.deliveryproject.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryproject.R;
import com.example.deliveryproject.adapters.UserCartAdapter;
import com.example.deliveryproject.adapters.UserDishesAdapter;

import java.util.Map;

public class UserCartFragment extends Fragment {
    SharedPreferences preferences;
    FragmentManager fragmentManager;
    Fragment fragment;

    public UserCartFragment(SharedPreferences preferences, FragmentManager fragmentManager, Fragment fragment) {
        this.preferences = preferences;
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;

        clearSharedPreferences(preferences);

    }

    private void clearSharedPreferences(SharedPreferences sharedPreferences) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            String[] splitedString = entry.getValue().toString().split(";");
            if (splitedString[splitedString.length - 1].equals("0")) {
                editor.remove(entry.getKey());
            }
        }
        editor.apply();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        if (preferences.getAll().size() == 0) {
            ((ConstraintLayout)view.findViewById(R.id.f_cart_constr_layout)).removeAllViews();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.a_user_fragment, fragment);
            fragmentTransaction.commit();
        }

        try {
            ((TextView)view.findViewById(R.id.f_cart_total_sum)).setText(getTotalSum() + " руб.");
        } catch (Exception npe) {

        }

        UserCartAdapter adapter = new UserCartAdapter(
                getContext(),
                preferences,
                container,
                view,
                fragmentManager,
                fragment);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.f_cart_recycle_view);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private float getTotalSum() {
        float sum = 0;

        for (Object s : preferences.getAll().values()) {
            sum += Float.parseFloat(s.toString().split(";")[1]) * Integer.parseInt(s.toString().split(";")[2]);
        }

        return sum;
    }
}
