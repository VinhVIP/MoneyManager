package com.vinh.moneymanager.models;

import androidx.databinding.ObservableInt;

import com.vinh.moneymanager.components.SingleChoice;

public class SingleChoiceModel {

    public final ObservableInt selectedIndex = new ObservableInt();
    private SingleChoice.OnChoiceSelectedListener listener;

    public SingleChoiceModel(SingleChoice.OnChoiceSelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedIndex(int index) {
        selectedIndex.set(index);
        listener.onChoiceClick(index);
    }

}
