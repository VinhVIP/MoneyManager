package com.vinh.moneymanager.room.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "account",
        indices = {@Index(value = {"a_name"},
                unique = true)})
public class Account {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "a_id")
    private int accountId;

    @NonNull
    @ColumnInfo(name = "a_name")
    private String accountName;

    @ColumnInfo(name = "a_balance")
    private long balance;

    @ColumnInfo(name = "a_description")
    private String description;

    @ColumnInfo(name = "a_icon")
    private int icon;

    public Account(@NonNull String accountName, long balance, String description, int icon) {
        this.accountName = accountName;
        this.balance = balance;
        this.description = description;
        this.icon = icon;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @NonNull
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(@NonNull String accountName) {
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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
