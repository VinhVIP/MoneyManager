package com.vinh.moneymanager.room.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vinh.moneymanager.room.entities.Account;

import java.util.List;

@Dao
public interface AccountDao {
    @Insert
    void insert(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);

    @Query("SELECT * FROM account")
    LiveData<List<Account>> getAccounts();

    @Query("SELECT * FROM account WHERE a_id=:accountId")
    Account search(int accountId);

}
