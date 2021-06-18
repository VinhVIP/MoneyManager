package com.vinh.moneymanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.vinh.moneymanager.R;
import com.vinh.moneymanager.adapters.FragmentAccountStateAdapter;
import com.vinh.moneymanager.databinding.FragmentAccountBinding;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.viewmodels.AccountViewModel;

public class AccountFragment extends Fragment {

    private static AccountFragment instance;

    private AccountViewModel mViewModel;

    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private ChipGroup chipGroup;
    private Chip chipAccount, chipTransfer;

    private int currentPage = 0;

    public AccountFragment() {

    }

    public static AccountFragment getInstance() {
        if (instance == null) {
            instance = new AccountFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(AccountViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAccountBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        View view = binding.getRoot();
        binding.setViewModel(mViewModel);

        viewPager = view.findViewById(R.id.view_pager_account);
        pagerAdapter = new FragmentAccountStateAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ((Chip) (chipGroup.getChildAt(position))).setChecked(true);
                currentPage = position;
            }
        });


        chipGroup = view.findViewById(R.id.chip_group_account);
        chipAccount = view.findViewById(R.id.chip_list_account);
        chipTransfer = view.findViewById(R.id.chip_list_transfer);

        chipAccount.setOnClickListener(v -> {
            if (currentPage != 0) {
                currentPage = 0;
                viewPager.setCurrentItem(0, true);
            }
            chipAccount.setChecked(true);
        });
        chipTransfer.setOnClickListener(v -> {
            if (currentPage != 1) {
                currentPage = 1;
                viewPager.setCurrentItem(1, true);
            }
            chipTransfer.setChecked(true);
        });

        updateBalance();

        return view;
    }

    private void updateBalance() {
        mViewModel.getAccounts().observe(getActivity(), accounts -> {

            long totalBalance = 0;
            for (Account a : accounts) {
                totalBalance += a.getBalance();
            }

            mViewModel.totalBalance.set(totalBalance);
            System.out.println("Balance: " + mViewModel.totalBalance.get());
        });
    }


}