package com.vinh.moneymanager.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.adapters.RecyclerExpenseAdapter;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.viewmodels.CategoryFinanceViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListIncomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerExpenseAdapter adapter;

    private List<Category> categories;

    private Map<Category, List<Finance>> mapCategoryFinance;


    private CategoryFinanceViewModel viewModel;

    private Fragment parentFragment;

    public static ListIncomeFragment instance = null;

    public static ListIncomeFragment getInstance(Fragment parentFragment) {
        if(instance == null){
            instance = new ListIncomeFragment(parentFragment);
        }
        return instance;
    }

    public ListIncomeFragment(Fragment parentFragment) {
        Log.e("MMM", "init income fragment");
        this.parentFragment = parentFragment;

        viewModel = new ViewModelProvider(parentFragment).get(CategoryFinanceViewModel.class);
        viewModel.initLiveData(parentFragment, parentFragment);

        categories = new ArrayList<>();
        mapCategoryFinance = new HashMap<>();

        adapter = new RecyclerExpenseAdapter(categories, mapCategoryFinance);

        viewModel.getMapCategoryFinance().observe(parentFragment, categoryListMap -> {
            Log.e("MMM", "observe income: "+viewModel.dateRange.get().getDateString());

            categories.clear();
            for (Category c : categoryListMap.keySet()) {
                if (c.getType() == Helper.TYPE_INCOME) {
                    categories.add(c);
                }
            }

            mapCategoryFinance = categoryListMap;

            adapter.setAdapter(categories, mapCategoryFinance);

        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_income);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

    }
}
