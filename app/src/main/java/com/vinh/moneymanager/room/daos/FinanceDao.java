package com.vinh.moneymanager.room.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;

@Dao
public interface FinanceDao {
    @Insert
    void insert(Finance finance);

    @Update
    void update(Finance finance);

    @Delete
    void delete(Finance finance);

    @Query("SELECT * FROM finance WHERE category_id = :categoryId")
    LiveData<List<Finance>> getFinancesByCategory(int categoryId);

    @Query("SELECT * FROM finance")
    LiveData<List<Finance>> getAllFinances();

}
