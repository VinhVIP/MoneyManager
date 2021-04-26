package com.vinh.moneymanager.room.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vinh.moneymanager.libs.Helper;
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

    @Query("SELECT * FROM category WHERE type_id = " + Helper.TYPE_INCOME + " ORDER BY c_id ASC")
    LiveData<List<Category>> getIncomeCategories();

    @Query("SELECT * FROM category WHERE type_id = " + Helper.TYPE_EXPENSE + " ORDER BY c_id ASC")
    LiveData<List<Category>> getExpenseCategories();

    @Query("SELECT * FROM category ORDER BY c_id ASC")
    LiveData<List<Category>> getCategories();

    @Query("SELECT EXISTS(SELECT * FROM category WHERE c_name = :categoryName)")
    boolean isExists(String categoryName);

    @Query("SELECT * FROM category WHERE c_id = :categoryId")
    Category getCategory(int categoryId);

}
