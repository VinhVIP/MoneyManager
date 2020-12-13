package com.vinh.moneymanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.repositories.CategoryRepository;
import com.vinh.moneymanager.room.entities.Category;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {

    private CategoryRepository repository;

    private LiveData<List<Category>> incomeCategories;
    private LiveData<List<Category>> expenseCategories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
        incomeCategories = repository.getIncomeCategories();
        expenseCategories = repository.getExpenseCategories();
    }

    public void insert(Category category) {
        repository.insert(category);
    }

    public void update(Category category) {
        repository.update(category);
    }

    public void delete(Category category) {
        repository.delete(category);
    }

    public LiveData<List<Category>> getIncomeCategories() {
        return incomeCategories;
    }

    public LiveData<List<Category>> getExpenseCategories() {
        return expenseCategories;
    }
}
