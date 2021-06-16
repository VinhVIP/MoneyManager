package com.vinh.moneymanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableLong;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.repositories.AccountRepository;
import com.vinh.moneymanager.room.entities.Account;

import java.util.List;

public class AccountViewModel extends AndroidViewModel {
    public ObservableLong totalBalance = new ObservableLong();
    private final AccountRepository repository;
    private final LiveData<List<Account>> accounts;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        repository = new AccountRepository(application);

        accounts = repository.getAccounts();
    }

    public void insert(Account account) {
        repository.insert(account);
    }

    public void update(Account account) {
        repository.update(account);
    }

    public void delete(Account account) {
        repository.delete(account);
    }

    public Account search(int accountId) {
        return repository.search(accountId);
    }

    public LiveData<List<Account>> getAccounts() {
        return accounts;
    }

}
