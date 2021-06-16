package com.vinh.moneymanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.adapters.RecyclerCategoryAdapter;
import com.vinh.moneymanager.listeners.OnItemCategoryListener;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListCategoryFragment extends Fragment {

    private TextView tvTitle;
    private String title;

    private RecyclerView recyclerView;
    private final RecyclerCategoryAdapter adapter;

    private final List<Category> categories;
    private Map<Category, List<Finance>> mapCategoryFinance;

    private final Fragment parentFragment;

    public ListCategoryFragment(Fragment parentFragment, OnItemCategoryListener listener) {
        this.parentFragment = parentFragment;

        categories = new ArrayList<>();
        mapCategoryFinance = new HashMap<>();

        adapter = new RecyclerCategoryAdapter(categories, mapCategoryFinance, listener);
    }

    public void notifyData(Map<Category, List<Finance>> mapCategoryFinance) {
        categories.clear();
        categories.addAll(mapCategoryFinance.keySet());
        for (Category c : categories) {
            System.out.println(c.getCategoryId() + " : " + c.getName());
        }
        this.mapCategoryFinance = mapCategoryFinance;
        adapter.setAdapter(categories, mapCategoryFinance);
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle = view.findViewById(R.id.tv_title_category);
        tvTitle.setText(title);

        recyclerView = view.findViewById(R.id.recycler_expense);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));

    }

}