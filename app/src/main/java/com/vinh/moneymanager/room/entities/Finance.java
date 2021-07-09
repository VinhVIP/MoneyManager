package com.vinh.moneymanager.room.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(tableName = "finance",
        foreignKeys = {
                @ForeignKey(entity = Category.class,
                        parentColumns = "c_id",
                        childColumns = "c_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Account.class,
                        parentColumns = "a_id",
                        childColumns = "a_id",
                        onDelete = ForeignKey.CASCADE)
        })

public class Finance {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "f_id")
    private int financeId;

    @ColumnInfo(name = "f_money")
    private long money;

    @NonNull
    @ColumnInfo(name = "f_date_time")
    private String dateTime;

    @ColumnInfo(name = "f_detail")
    private String detail;

    @ColumnInfo(name = "c_id")
    private int categoryId;

    @ColumnInfo(name = "a_id")
    private int accountId;

    public Finance(long money, @NonNull String dateTime, String detail, int categoryId, int accountId) {
        this.money = money;
        this.dateTime = dateTime;
        this.detail = detail;
        this.categoryId = categoryId;
        this.accountId = accountId;
    }


    public int getFinanceId() {
        return financeId;
    }

    public void setFinanceId(int financeId) {
        this.financeId = financeId;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    @NonNull
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(@NonNull String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTime() {
        return dateTime.split("-")[1].trim();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
