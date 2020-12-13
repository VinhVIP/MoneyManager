package com.vinh.moneymanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.vinh.moneymanager.repositories.AccountRepository;
import com.vinh.moneymanager.room.entities.Account;

public class AccountViewModel extends AndroidViewModel {
    private AccountRepository repository;


    public AccountViewModel(@NonNull Application application) {
        super(application);
        repository = new AccountRepository(application);
    }

    public void insert(Account account) {
        repository.insert(account);
    }

    public void update(Account account) {
        repository.update(account);
    }

    public void delete(Account account) {
        repository.update(account);
    }
}
