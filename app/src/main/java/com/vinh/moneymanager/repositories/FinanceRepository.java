package com.vinh.moneymanager.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.room.daos.FinanceDao;
import com.vinh.moneymanager.room.MoneyManagerDatabase;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;

public class FinanceRepository {

    private FinanceDao financeDao;

    private LiveData<List<Finance>> finances;

    public FinanceRepository(Application application) {
        MoneyManagerDatabase database = MoneyManagerDatabase.getInstance(application);
        financeDao = database.financeDao();

        finances = financeDao.getAllFinances();
    }

    public void insert(Finance finance){
        financeDao.insert(finance);
    }

    public void update(Finance finance){
        financeDao.update(finance);
    }

    public void delete(Finance finance){
        financeDao.delete(finance);
    }

    public LiveData<List<Finance>> getAllFinances(){
        return finances;
    }
}
