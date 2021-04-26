package com.vinh.moneymanager.room.daos;

import androidx.room.Dao;
import androidx.room.Insert;

import com.vinh.moneymanager.room.entities.Type;

@Dao
public interface TypeDao {
    @Insert
    void insert(Type type);
}
