package com.vinh.moneymanager.viewmodels;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableLong;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CategoryFinanceViewModel {

    public FinanceViewModel financeViewModel;
    public CategoryViewModel categoryViewModel;

    private MutableLiveData<List<Category>> categories;
    private MutableLiveData<Map<Category, List<Finance>>> mapFinance;

    public ObservableField<DateRange> dateRange = new ObservableField<>();
    public ObservableLong totalCost = new ObservableLong();

    public CategoryFinanceViewModel(ViewModelStoreOwner owner, LifecycleOwner lifecycleOwner) {
        financeViewModel = new ViewModelProvider(owner).get(FinanceViewModel.class);
        categoryViewModel = new ViewModelProvider(owner).get(CategoryViewModel.class);

        categories = new MutableLiveData<>();
        mapFinance = new MutableLiveData<>();

        categoryViewModel.getExpenseCategories().observe(lifecycleOwner, allCategories -> {
            this.categories.setValue(allCategories);


            financeViewModel.getAllFinances().observe(lifecycleOwner, finances -> {
                Map<Category, List<Finance>> map = new TreeMap<>((c1, c2) -> c1.getId() - c2.getId());

                for (Category c : allCategories) {
                    map.put(c, new ArrayList<>());
                }

                for (Finance f : finances) {
                    for (Category c : map.keySet()) {
                        if (f.getCategoryId() == c.getId()) {
                            map.get(c).add(f);
                        }
                    }
                }

                for (Category c : map.keySet()) {
                    for (Finance f : map.get(c)) {
                        System.out.println(f.getId() + " " + f.getDetail());
                    }
                }

                long total = 0;
                for (Finance f : finances) {
                    total += f.getCost();
                }

                this.totalCost.set(total);

                this.mapFinance.setValue(map);
            });
        });

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

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<Map<Category, List<Finance>>> getMapFinance() {
        return mapFinance;
    }
}
