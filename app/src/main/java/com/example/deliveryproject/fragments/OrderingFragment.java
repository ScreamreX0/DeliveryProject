package com.example.deliveryproject.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.deliveryproject.R;
import com.example.deliveryproject.adapters.UserCartAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrderingFragment extends DialogFragment {
    public OrderingFragment(SharedPreferences preferences,
                            UserCartAdapter adapter,
                            Fragment fragment,
                            FragmentManager fragmentManager) {
        this.preferences = preferences;
        this.adapter = adapter;
        this.fragment = fragment;
        this.fragmentManager = fragmentManager;
    }

    SharedPreferences preferences;
    UserCartAdapter adapter;
    Fragment fragment;
    FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ordering, container, false);

        view.findViewById(R.id.d_ordering_cancel).setOnClickListener(view1 -> {
            this.dismiss();
        });

        view.findViewById(R.id.d_ordering_ok).setOnClickListener(view1 -> {
            String name = ((EditText)view.findViewById(R.id.d_ordering_address)).getText().toString();
            if (name.replace(" ", "").equals("")) {
                Toast.makeText(inflater.getContext(), "Поле должно быть заполнено", Toast.LENGTH_SHORT).show();
                this.dismiss();
                return;
            }

            insertToHistory();

            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            this.adapter.notifyItemRangeRemoved(0, this.adapter.getItemCount());

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.a_user_fragment, fragment);
            fragmentTransaction.commit();

            this.dismiss();
        });

        return view;
    }

    @SuppressLint("SimpleDateFormat")
    private void insertToHistory() {
        List<String> keys = new ArrayList<>(preferences.getAll().keySet());
        List<String> values = new ArrayList<String>((Collection<? extends String>) preferences.getAll().values());

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        HashMap<String, Object> historyMap = new HashMap<>();
        historyMap.put("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

        HashMap<String, HashMap<String, String>> productsMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            HashMap<String, String> productMap = new HashMap<>();
            productMap.put("Name", keys.get(i));
            productMap.put("Photo path", values.get(i).split(";")[0]);
            productMap.put("Price", values.get(i).split(";")[1]);
            productMap.put("Count", values.get(i).split(";")[2]);
            productMap.put("Shop", values.get(i).split(";")[3]);

            productsMap.put("Product" + i, productMap);
        }
        historyMap.put("Products", productsMap);

        firebaseDatabase.child("Users").child(firebaseAuth.getUid()).child("History").push().setValue(historyMap);
    }
}
