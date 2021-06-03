package com.vinh.moneymanager.listeners;

import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

public interface OnItemFinanceListener {
    void onFinanceClick(Finance finance, Category category);
}
