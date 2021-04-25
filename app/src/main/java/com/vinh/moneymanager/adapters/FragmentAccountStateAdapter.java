package com.vinh.moneymanager.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vinh.moneymanager.fragments.ListAccountFragment;
import com.vinh.moneymanager.fragments.ListTransferFragment;

public class FragmentAccountStateAdapter extends FragmentStateAdapter {

    private final int NUM_PAGES = 2;

    public FragmentAccountStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ListAccountFragment();
            case 1:
                return new ListTransferFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
