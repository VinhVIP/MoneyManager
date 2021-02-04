package com.vinh.moneymanager.room.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vinh.moneymanager.room.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM category WHERE type = 0 ORDER BY category_id ASC")
    LiveData<List<Category>> getIncomeCategories();

    @Query("SELECT * FROM category ORDER BY category_id ASC")
    LiveData<List<Category>> getCategories();


}
