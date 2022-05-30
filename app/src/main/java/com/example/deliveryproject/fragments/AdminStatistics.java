package com.example.deliveryproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.deliveryproject.R;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminStatistics extends Fragment {
    ArrayList<DataSnapshot> items;
    FragmentManager fragmentManager;
    HashMap<String, Float> shopTurnover;

    public AdminStatistics(List<DataSnapshot> items, FragmentManager fragmentManager) {
        this.items = (ArrayList<DataSnapshot>) items;
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_statistics, container, false);

        initShopTurnover();

        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        for (String name : shopTurnover.keySet()) {
            data.add(new ValueDataEntry(name, shopTurnover.get(name)));
        }

        pie.data(data);

        AnyChartView anyChartView = view.findViewById(R.id.f_admin_statistics_view);
        anyChartView.setChart(pie);

        return view;
    }

    private void initShopTurnover() {
        shopTurnover = new HashMap<>();

        // Проходимся по всем пользователям
        for (DataSnapshot user : items) {
            // Проверяем на наличие заказов у пользователя
            if (!user.child("History").hasChildren()) {
                continue;
            }
            // Проходимся по всем заказам
            for (DataSnapshot order : user.child("History").getChildren()) {
                // Проходимся по всем продуктам в заказе
                for (DataSnapshot product : order.child("Products").getChildren()) {
                    int count = Integer.parseInt(product.child("Count").getValue().toString());
                    float price = Float.parseFloat(product.child("Price").getValue().toString());
                    String shopName = product.child("Shop").getValue().toString();

                    if (shopTurnover.containsKey(shopName)) {
                        float turnover = shopTurnover.get(shopName);
                        shopTurnover.put(shopName, turnover + count * price);
                    } else {
                        shopTurnover.put(shopName, count * price);
                    }
                }
            }
        }
    }
}

