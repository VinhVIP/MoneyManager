package com.vinh.moneymanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vinh.moneymanager.R;
import com.vinh.moneymanager.activities.AddEditFinanceActivity;
import com.vinh.moneymanager.adapters.RecyclerTransferAdapter;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Transfer;
import com.vinh.moneymanager.viewmodels.AccountViewModel;
import com.vinh.moneymanager.viewmodels.TransferViewModel;

import java.util.ArrayList;
import java.util.Collections;
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
        accountViewModel = new ViewModelProvider(getActivity()).get(AccountViewModel.class);
        transferViewModel = new ViewModelProvider(getActivity()).get(TransferViewModel.class);
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

        transferViewModel.getTransfers().observe(getActivity(), transfers -> {
            Log.d("MM", "observe list transfer: " + transfers.size());

            // Sắp xếp
            sortByTimeDesc(transfers);

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

    /**
     * Sắp xếp danh sách chuyển khoản giảm dần theo thời gian
     *
     * @param list danh sách muốn sắp xếp
     */
    private void sortByTimeDesc(List<Transfer> list) {
        Collections.sort(list, (t1, t2) -> {
            String[] date1 = t1.getDateTime().split("-")[0].split("/");
            String[] time1 = t1.getDateTime().split("-")[1].split(":");
            String[] date2 = t2.getDateTime().split("-")[0].split("/");
            String[] time2 = t2.getDateTime().split("-")[1].split(":");

            int[] a = new int[5];
            for (int i = 0; i < 3; i++) a[i] = Integer.parseInt(date1[2 - i].trim());
            for (int i = 3; i < 5; i++) a[i] = Integer.parseInt(time1[i - 3].trim());

            int[] b = new int[5];
            for (int i = 0; i < 3; i++) b[i] = Integer.parseInt(date2[2 - i].trim());
            for (int i = 3; i < 5; i++) b[i] = Integer.parseInt(time2[i - 3].trim());

            for (int i = 0; i < 5; i++) {
                if (a[i] < b[i]) return 1;
                else if (a[i] > b[i]) return -1;
            }
            return 0;
        });
    }

    @Override
    public void onItemTransferClick(Transfer transfer, int position) {
        Bundle bundle = new Bundle();

        Log.e("MM", transfer.getDateTime() + " - " + transfer.getMoney());

        bundle.putInt(Helper.TRANSFER_ID, transfer.getTransferId());
        bundle.putString(Helper.TRANSFER_DATETIME, transfer.getDateTime());
        bundle.putInt(Helper.TRANSFER_ACCOUNT_OUT_ID, transfer.getAccountOutId());
        bundle.putInt(Helper.TRANSFER_ACCOUNT_IN_ID, transfer.getAccountInId());
        bundle.putLong(Helper.TRANSFER_MONEY, transfer.getMoney());
        bundle.putLong(Helper.TRANSFER_FEE, transfer.getFee());
        bundle.putString(Helper.TRANSFER_DETAIL, transfer.getDetail());

        Intent intent = new Intent(this.getActivity(), AddEditFinanceActivity.class);
        intent.putExtra(Helper.EDIT_TRANSFER, bundle);
        startActivity(intent);
    }

    @Override
    public void onItemDelete(Transfer transfer, int position) {

        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có xác định muốn xóa chuyển khoản này?")
                .setPositiveButton("XÓA", (dialog, which) -> {
                    Log.d("MM", "Delete transfer: " + transfer.getTransferId());

                    Account accountOut = accountViewModel.search(transfer.getAccountOutId());
                    Account accountIn = accountViewModel.search(transfer.getAccountInId());

                    // Khôi phục lại số tiền trong tài khoản
                    accountOut.setBalance(accountOut.getBalance() + transfer.getMoney() + transfer.getFee());
                    accountIn.setBalance(accountIn.getBalance() - transfer.getMoney());

                    accountViewModel.update(accountOut);
                    accountViewModel.update(accountIn);

                    transferViewModel.delete(transfer);
                    adapter.deleteTransfer(position);
                }).setNegativeButton("HỦY", (dialog, which) -> dialog.cancel()).show();

    }

}
