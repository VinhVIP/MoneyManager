package com.vinh.moneymanager.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(tableName = "finance",
        foreignKeys = {
                @ForeignKey(entity = Category.class,
                        parentColumns = "category_id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Account.class,
                        parentColumns = "account_id",
                        childColumns = "account_id",
                        onDelete = ForeignKey.CASCADE)
        })

public class Finance {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "finance_id")
    private int id;

    @ColumnInfo(name = "cost")
    private long cost;

    @ColumnInfo(name = "date_time")
    private String dateTime;

    @ColumnInfo(name = "detail")
    private String detail;

    @ColumnInfo(name = "category_id")
    private int categoryId;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @ColumnInfo(name = "account_id")
    private int accountId;

    public Finance(long cost, String dateTime, String detail, int categoryId, int accountId) {
        this.cost = cost;
        this.dateTime = dateTime;
        this.detail = detail;
        this.categoryId = categoryId;
        this.accountId = accountId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getTime(){
        return dateTime.split("-")[1].trim();
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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
}
