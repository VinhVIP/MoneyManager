package com.vinh.moneymanager.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.room.MoneyManagerDatabase;
import com.vinh.moneymanager.room.daos.AccountDao;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;

public class AccountRepository {

    private AccountDao accountDao;

    private LiveData<List<Finance>> accounts;

    public AccountRepository(Application application) {
        MoneyManagerDatabase database = MoneyManagerDatabase.getInstance(application);
        accountDao = database.accountDao();
    }

    public void insert(Account account) {
        accountDao.insert(account);
    }

    public void update(Account account) {
        accountDao.update(account);
    }

    public void delete(Account account) {
        accountDao.delete(account);
    }
}
