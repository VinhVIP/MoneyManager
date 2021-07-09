package com.vinh.moneymanager.room.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "type",
        indices = {@Index(value = {"type_name"},
                unique = true)})
public class Type {

    @PrimaryKey
    @ColumnInfo(name = "type_id")
    int typeId;

    @NonNull
    @ColumnInfo(name = "type_name")
    String typeName;

    public Type(int typeId, @NonNull String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @NonNull
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(@NonNull String typeName) {
        this.typeName = typeName;
    }
}
