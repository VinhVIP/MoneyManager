package com.vinh.moneymanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.repositories.FinanceRepository;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;

public class FinanceViewModel extends AndroidViewModel {

    private final FinanceRepository repository;

    private final LiveData<List<Finance>> finances;


    public FinanceViewModel(@NonNull Application application) {
        super(application);
        repository = new FinanceRepository(application);

        finances = repository.getAllFinances();
    }

    public void insert(Finance finance) {
        repository.insert(finance);
    }

    public void update(Finance finance) {
        repository.update(finance);
    }

    public void delete(Finance finance) {
        repository.delete(finance);
    }

    public LiveData<List<Finance>> getAllFinances() {
        return finances;
    }

    public LiveData<List<Finance>> getFinances(int categoryId) {
        return repository.getFinances(categoryId);
    }

}
