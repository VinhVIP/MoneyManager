package com.vinh.moneymanager.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vinh.moneymanager.repositories.FinanceRepository;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;
import java.util.logging.Logger;

public class FinanceViewModel extends AndroidViewModel {

    private final FinanceRepository repository;

    private final LiveData<List<Finance>> finances;

    private MutableLiveData<List<Finance>> financesSearch = new MutableLiveData<>();


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

    public LiveData<List<Finance>> search() {
        return financesSearch;
    }

    public void search(String keyword, LifecycleOwner owner) {
        repository.getFinancesSearch(keyword).observe(owner, finances1 -> {
            financesSearch.setValue(finances1);
            Log.e("MMM", "size finances search: "+finances1.size());
        });
    }

}
