package com.vinh.moneymanager.room.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.room.entities.Transfer;

import java.util.List;

@Dao
public interface TransferDao {

    @Insert
    void insert(Transfer transfer);

    @Update
    void update(Transfer transfer);

    @Delete
    void delete(Transfer transfer);

    @Query("SELECT * FROM transfer ORDER BY t_id DESC")
    LiveData<List<Transfer>> getTransfers();

    @Query("SELECT * FROM transfer WHERE t_detail LIKE :keyword ORDER BY t_id ASC")
    LiveData<List<Transfer>> getTransfersSearch(String keyword);
}
