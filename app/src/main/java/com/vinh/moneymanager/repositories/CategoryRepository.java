package com.vinh.moneymanager.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.room.MoneyManagerDatabase;
import com.vinh.moneymanager.room.daos.CategoryDao;
import com.vinh.moneymanager.room.entities.Category;

import java.util.List;

public class CategoryRepository {

    private final CategoryDao categoryDao;

    private final LiveData<List<Category>> categories;

    private LiveData<List<Category>> incomeCategories;
    private LiveData<List<Category>> expenseCategories;

    public CategoryRepository(Application application) {
        MoneyManagerDatabase database = MoneyManagerDatabase.getInstance(application);
        categoryDao = database.categoryDao();

        categories = categoryDao.getCategories();
    }

    public void insert(Category category) {
        new InsertCategoryAsyncTask(categoryDao).execute(category);
    }

    public void update(Category category) {
        new UpdateCategoryAsyncTask(categoryDao).execute(category);
    }

    public void delete(Category category) {
        new DeleteCategoryAsyncTask(categoryDao).execute(category);
    }

    public boolean isExists(String categoryName) {
        return categoryDao.isExists(categoryName);
    }

    public Category getCategory(int categoryId) {
        return categoryDao.getCategory(categoryId);
    }


    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<Category>> getIncomeCategories() {
        return incomeCategories;
    }

    public LiveData<List<Category>> getExpenseCategories() {
        return expenseCategories;
    }


    // --------------- AsyncTask -------------------
    private static class InsertCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private final CategoryDao categoryDao;

        private InsertCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.insert(categories[0]);
            return null;
        }
    }

    private static class DeleteCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private final CategoryDao categoryDao;

        private DeleteCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.delete(categories[0]);
            return null;
        }
    }

    private static class UpdateCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private final CategoryDao categoryDao;

        private UpdateCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.update(categories[0]);
            return null;
        }
    }
}
