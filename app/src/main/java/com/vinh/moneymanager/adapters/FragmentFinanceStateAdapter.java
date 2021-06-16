package com.vinh.moneymanager.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentFinanceStateAdapter extends FragmentStateAdapter {

    private final int NUM_PAGES = 2;

    private final Fragment parentFragment;

    private Fragment incomeFragment, expenseFragment;

    public FragmentFinanceStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
        this.parentFragment = fragment;
    }

    public void setFragment(Fragment incomeFragment, Fragment expenseFragment) {
        this.incomeFragment = incomeFragment;
        this.expenseFragment = expenseFragment;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return incomeFragment;
            case 1:
                return expenseFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}