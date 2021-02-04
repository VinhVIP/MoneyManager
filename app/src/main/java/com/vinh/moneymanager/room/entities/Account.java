package com.vinh.moneymanager.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "account")
public class Account {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "account_id")
    private int id;

    @ColumnInfo(name = "name")
    private String accountName;

    private long balance;

    private String description;

    public Account(String accountName, long balance, String description) {
        this.accountName = accountName;
        this.balance = balance;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
