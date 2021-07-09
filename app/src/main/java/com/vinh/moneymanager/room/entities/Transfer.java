package com.vinh.moneymanager.room.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "transfer",
        foreignKeys = {
                @ForeignKey(entity = Account.class,
                        parentColumns = "a_id",
                        childColumns = "a_in_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Account.class,
                        parentColumns = "a_id",
                        childColumns = "a_out_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class Transfer {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "t_id")
    private int transferId;

    @ColumnInfo(name = "t_money")
    private long money;

    @ColumnInfo(name = "t_fee")
    private long fee;

    @NonNull
    @ColumnInfo(name = "t_date_time")
    private String dateTime;

    @ColumnInfo(name = "t_detail")
    private String detail;

    @ColumnInfo(name = "a_out_id")
    private int accountOutId;

    @ColumnInfo(name = "a_in_id")
    private int accountInId;

    public Transfer(long money, long fee, @NonNull String dateTime, String detail, int accountOutId, int accountInId) {
        this.money = money;
        this.fee = fee;
        this.dateTime = dateTime;
        this.detail = detail;
        this.accountOutId = accountOutId;
        this.accountInId = accountInId;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    @NonNull
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(@NonNull String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getAccountOutId() {
        return accountOutId;
    }

    public void setAccountOutId(int accountOutId) {
        this.accountOutId = accountOutId;
    }

    public int getAccountInId() {
        return accountInId;
    }

    public void setAccountInId(int accountInId) {
        this.accountInId = accountInId;
    }
}
