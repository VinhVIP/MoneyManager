package com.vinh.moneymanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.repositories.CategoryRepository;
import com.vinh.moneymanager.room.entities.Category;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {

    private final CategoryRepository repository;

    private final LiveData<List<Category>> categories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);

        categories = repository.getCategories();
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

    public boolean isExists(String categoryName) {
        return repository.isExists(categoryName);
    }

    public Category getCategory(int categoryId) {
        return repository.getCategory(categoryId);
    }


    public LiveData<List<Category>> getCategories() {
        return categories;
    }
}
