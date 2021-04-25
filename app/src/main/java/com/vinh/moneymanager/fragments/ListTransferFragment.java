package com.vinh.moneymanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vinh.moneymanager.R;
import com.vinh.moneymanager.activities.AddEditFinanceActivity;
import com.vinh.moneymanager.adapters.RecyclerTransferAdapter;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Transfer;
import com.vinh.moneymanager.viewmodels.AccountViewModel;
import com.vinh.moneymanager.viewmodels.TransferViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListTransferFragment extends Fragment implements RecyclerTransferAdapter.OnItemTransferListener {

    private RecyclerView recyclerTransfers;

    private List<Transfer> transfers;
    private RecyclerTransferAdapter adapter;

    private TransferViewModel transferViewModel;
    private AccountViewModel accountViewModel;

    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);
        return inflater.inflate(R.layout.fragment_list_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerTransfers = view.findViewById(R.id.recycler_transfer);
        transfers = new ArrayList<>();
        adapter = new RecyclerTransferAdapter(transfers, this, accountViewModel);
        recyclerTransfers.setAdapter(adapter);
        recyclerTransfers.setLayoutManager(new LinearLayoutManager(this.getContext()));

        transferViewModel.getTransfers().observe(getViewLifecycleOwner(), transfers -> {
            Log.d("MM", "observe list transfer: " + transfers.size());
            this.transfers = transfers;
            adapter.setAdapter(transfers);
        });

        fab = view.findViewById(R.id.fab_add_transfer);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this.getActivity(), AddEditFinanceActivity.class);
            intent.putExtra(Helper.ADD_TRANSFER, new Bundle());
            startActivity(intent);
        });
    }

    @Override
    public void onItemTransferClick(Transfer transfer, int position) {
        Toast.makeText(getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
    }
}
