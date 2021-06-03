package com.vinh.moneymanager.listeners;

import com.vinh.moneymanager.room.entities.Category;

public interface OnItemCategoryListener {

    void onCategoryClick(Category category, int position);

    void onCategoryLongClick(Category category, int position);

    void onCategoryAdd();
}
