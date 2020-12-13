package com.vinh.moneymanager.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.room.daos.CategoryDao;
import com.vinh.moneymanager.room.MoneyManagerDatabase;
import com.vinh.moneymanager.room.entities.Category;

import java.util.List;

public class CategoryRepository {

    private CategoryDao categoryDao;

    private LiveData<List<Category>> incomeCategories;
    private LiveData<List<Category>> expenseCategories;

    public CategoryRepository(Application application) {
        MoneyManagerDatabase database = MoneyManagerDatabase.getInstance(application);
        categoryDao = database.categoryDao();

        incomeCategories = categoryDao.getIncomeCategories();
        expenseCategories = categoryDao.getExpenseCategories();
    }

    public void insert(Category category) {
        categoryDao.insert(category);
    }

    public void update(Category category) {
        categoryDao.update(category);
    }

    public void delete(Category category) {
        categoryDao.delete(category);
    }

    public LiveData<List<Category>> getIncomeCategories() {
        return incomeCategories;
    }

    public LiveData<List<Category>> getExpenseCategories() {
        return expenseCategories;
    }
}
