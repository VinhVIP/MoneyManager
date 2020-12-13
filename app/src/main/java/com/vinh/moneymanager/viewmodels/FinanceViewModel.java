package com.vinh.moneymanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.vinh.moneymanager.repositories.FinanceRepository;
import com.vinh.moneymanager.room.entities.Finance;

public class FinanceViewModel extends AndroidViewModel {

    private FinanceRepository repository;


    public FinanceViewModel(@NonNull Application application) {
        super(application);
        repository = new FinanceRepository(application);
    }

    public void insert(Finance finance) {
        repository.insert(finance);
    }

    public void update(Finance finance) {
        repository.update(finance);
    }

    public void delete(Finance finance) {
        repository.update(finance);
    }

}
