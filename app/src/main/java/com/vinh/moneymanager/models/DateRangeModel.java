package com.vinh.moneymanager.models;

import androidx.databinding.ObservableField;

import com.vinh.moneymanager.libs.DateRange;

public class DateRangeModel {

    public ObservableField<DateRange> dateRange = new ObservableField<>();

    public DateRangeModel() {
    }

    public void setDateRangeValue(DateRange rangeValue) {
        dateRange.set(rangeValue);
        dateRange.notifyChange();
    }

    public void next() {
        DateRange range = dateRange.get();
        range.next();
        setDateRangeValue(range);
    }

    public void previous() {
        DateRange range = dateRange.get();
        range.previous();
        setDateRangeValue(range);

    }

}
