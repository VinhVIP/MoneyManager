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

    @Query("DELETE FROM finance WHERE f_id = :financeId")
    void delete(int financeId);

    @Query("SELECT * FROM finance WHERE c_id = :categoryId")
    LiveData<List<Finance>> getFinances(int categoryId);

    @Query("SELECT * FROM finance WHERE f_detail LIKE :keyword ORDER BY f_id ASC")
    LiveData<List<Finance>> getFinancesSearch(String keyword);

    @Query("SELECT * FROM finance")
    LiveData<List<Finance>> getAllFinances();

}
