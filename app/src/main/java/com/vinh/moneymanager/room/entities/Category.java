package com.vinh.moneymanager.room.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "category",
        foreignKeys = {
                @ForeignKey(entity = Type.class,
                        parentColumns = "type_id",
                        childColumns = "type_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = {"c_name"},
                unique = true)})
public class Category {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "c_id")
    private int categoryId;

    @NonNull
    @ColumnInfo(name = "c_name")
    private String name;

    @ColumnInfo(name = "type_id")
    private int type;

    @ColumnInfo(name = "c_description")
    private String description;

    @ColumnInfo(name = "c_icon")
    private int icon;

    @Ignore
    private long totalCost;

    public Category(@NonNull String name, int type, String description, int icon) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.icon = icon;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

}
