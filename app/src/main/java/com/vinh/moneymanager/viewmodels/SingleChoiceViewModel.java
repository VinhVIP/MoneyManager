package com.vinh.moneymanager.viewmodels;

import androidx.databinding.ObservableInt;

import com.vinh.moneymanager.components.SingleChoice;

public class SingleChoiceViewModel {
    public final ObservableInt selectedIndex = new ObservableInt();
    private final SingleChoice.OnChoiceSelectedListener listener;

    public SingleChoiceViewModel(SingleChoice.OnChoiceSelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedIndex(int index) {
        selectedIndex.set(index);
        listener.onChoiceClick(index);
    }
}
