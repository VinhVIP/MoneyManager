package com.vinh.moneymanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vinh.moneymanager.R;
import com.vinh.moneymanager.activities.AddEditAccountActivity;
import com.vinh.moneymanager.adapters.RecyclerAccountAdapter;
import com.vinh.moneymanager.databinding.FragmentAccountBinding;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.viewmodels.AccountViewModel;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment implements RecyclerAccountAdapter.OnItemAccountClickListener {

    private static AccountFragment instance;

    private AccountViewModel mViewModel;

    private List<Account> accounts;
    private RecyclerView recyclerAccount;
    private RecyclerAccountAdapter accountAdapter;


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
        mViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAccountBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        View view = binding.getRoot();
        binding.setViewModel(mViewModel);

        initRecyclerAccount(view);

        initFloatingActionButton(view);

        return view;
    }

    private void initRecyclerAccount(View view) {
        recyclerAccount = view.findViewById(R.id.recycler_account);
        accounts = new ArrayList<>();
        accountAdapter = new RecyclerAccountAdapter(new ArrayList<>(), this);
        recyclerAccount.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerAccount.setAdapter(accountAdapter);

        mViewModel.getAccounts().observe(getViewLifecycleOwner(), accounts -> {
            accountAdapter.setAccounts(accounts);

            long totalBalance = 0;
            for(Account a:accounts){
                totalBalance += a.getBalance();
            }

            mViewModel.totalBalance.set(totalBalance);
            System.out.println("Balance: "+mViewModel.totalBalance.get());
        });
    }

    private void initFloatingActionButton(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fab_account);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this.getActivity(), AddEditAccountActivity.class);
            startActivity(intent);
        });
    }


    @Override
    public void onItemAccountClick(Account account) {
        Intent intent = new Intent(this.getActivity(), AddEditAccountActivity.class);

        intent.putExtra("account_id", account.getId());
        intent.putExtra("account_name", account.getAccountName());
        intent.putExtra("account_balance", account.getBalance());
        intent.putExtra("account_description", account.getDescription());

        startActivity(intent);
    }
}