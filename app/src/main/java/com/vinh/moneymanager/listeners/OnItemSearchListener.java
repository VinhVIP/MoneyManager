package com.vinh.moneymanager.listeners;

import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.room.entities.Transfer;

public interface OnItemSearchListener {

    void onFinanceClick(Finance finance, Category category);

    void onTransferClick(Transfer transfer);
}
