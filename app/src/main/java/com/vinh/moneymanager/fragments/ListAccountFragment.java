package com.vinh.moneymanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vinh.moneymanager.R;
import com.vinh.moneymanager.activities.AddEditAccountActivity;
import com.vinh.moneymanager.adapters.RecyclerAccountAdapter;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.viewmodels.AccountViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListAccountFragment extends Fragment implements RecyclerAccountAdapter.OnItemAccountClickListener {

    private RecyclerView recyclerAccounts;
    private FloatingActionButton fab;

    private List<Account> accounts;
    private RecyclerAccountAdapter adapter;

    private AccountViewModel accountViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(getActivity()).get(AccountViewModel.class);
        return inflater.inflate(R.layout.fragment_list_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerAccounts = view.findViewById(R.id.recycler_account);
        recyclerAccounts.setAdapter(adapter);

        fab = view.findViewById(R.id.fab_account);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this.getActivity(), AddEditAccountActivity.class);
            startActivity(intent);
        });

        accounts = new ArrayList<>();
        adapter = new RecyclerAccountAdapter(new ArrayList<>(), this);
        recyclerAccounts.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerAccounts.setAdapter(adapter);

        accountViewModel.getAccounts().observe(getActivity(), accounts -> {
            adapter.setAccounts(accounts);
        });
    }

    @Override
    public void onItemAccountClick(Account account) {
        Bundle bundle = new Bundle();
        bundle.putInt(Helper.ACCOUNT_ID, account.getAccountId());
        bundle.putString(Helper.ACCOUNT_NAME, account.getAccountName());
        bundle.putLong(Helper.ACCOUNT_BALANCE, account.getBalance());
        bundle.putString(Helper.ACCOUNT_DESCRIPTION, account.getDescription());
        bundle.putInt(Helper.ACCOUNT_ICON, account.getIcon());

        Intent intent = new Intent(this.getActivity(), AddEditAccountActivity.class);
        intent.putExtra(Helper.EDIT_ACCOUNT, bundle);

        startActivity(intent);
    }

}
