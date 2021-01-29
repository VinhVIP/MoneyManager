package com.vinh.moneymanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vinh.moneymanager.R;
import com.vinh.moneymanager.activities.AddEditAccountActivity;
import com.vinh.moneymanager.adapters.RecyclerAccountAdapter;
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
        View view = inflater.inflate(R.layout.fragment_account, container, false);

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
        Toast.makeText(this.getContext(), "Account Clicked", Toast.LENGTH_SHORT).show();
    }
}