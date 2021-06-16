package com.vinh.moneymanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.repositories.TransferRepository;
import com.vinh.moneymanager.room.entities.Transfer;

import java.util.List;

public class TransferViewModel extends AndroidViewModel {

    private final TransferRepository repository;

    private final LiveData<List<Transfer>> transfers;

    public TransferViewModel(@NonNull Application application) {
        super(application);
        repository = new TransferRepository(application);

        transfers = repository.getTransfers();
    }

    public void insert(Transfer transfer) {
        repository.insert(transfer);
    }

    public void update(Transfer transfer) {
        repository.update(transfer);
    }

    public void delete(Transfer transfer) {
        repository.delete(transfer);
    }

    public LiveData<List<Transfer>> getTransfers() {
        return transfers;
    }
}
