package com.vinh.moneymanager.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vinh.moneymanager.repositories.TransferRepository;
import com.vinh.moneymanager.room.entities.Transfer;

import java.util.List;

public class TransferViewModel extends AndroidViewModel {

    private final TransferRepository repository;

    private final LiveData<List<Transfer>> transfers;
    private final MutableLiveData<List<Transfer>> transfersSearch = new MutableLiveData<>();


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

    public LiveData<List<Transfer>> search() {
        return transfersSearch;
    }

    public void search(String keyword, LifecycleOwner owner) {
        repository.getTransfersSearch(keyword).observe(owner, transfers1 ->
        {
            transfersSearch.setValue(transfers1);
            Log.e("MMM", "size finances search: " + transfers1.size());
        });
    }

}
