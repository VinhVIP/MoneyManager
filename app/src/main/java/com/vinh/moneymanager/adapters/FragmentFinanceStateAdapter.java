package com.vinh.moneymanager.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vinh.moneymanager.fragments.ListExpenseFragment;
import com.vinh.moneymanager.fragments.ListIncomeFragment;

public class FragmentFinanceStateAdapter extends FragmentStateAdapter {

    private final int NUM_PAGES = 2;

    private Fragment parentFragment;

    private Fragment incomeFragment, expenseFragment;

    public FragmentFinanceStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
        this.parentFragment = fragment;

        incomeFragment = new ListIncomeFragment(parentFragment);
        expenseFragment = new ListExpenseFragment(parentFragment);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                Log.e("MMM", "create income fragment");
                return ListIncomeFragment.getInstance(parentFragment);
            case 1:
                Log.e("MMM", "create expense fragment");
                return ListExpenseFragment.getInstance(parentFragment);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}