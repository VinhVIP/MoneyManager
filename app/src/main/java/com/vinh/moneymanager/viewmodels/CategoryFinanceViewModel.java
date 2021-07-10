package com.vinh.moneymanager.viewmodels;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableLong;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.room.entities.Transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CategoryFinanceViewModel extends ViewModel {

    public FinanceViewModel financeViewModel;
    public CategoryViewModel categoryViewModel;
    public AccountViewModel accountViewModel;
    public TransferViewModel transferViewModel;

    public ObservableField<DateRange> dateRange = new ObservableField<>();

    public ObservableLong totalCostIncome = new ObservableLong();
    public ObservableLong totalCostExpense = new ObservableLong();
    public ObservableInt switchExpenseIncome = new ObservableInt();

    public Map<Category, List<Finance>> getAllFinances() {
        return allFinances;
    }

    private List<Transfer> allTransfer;

    private Map<Category, List<Finance>> allFinances;
    private MutableLiveData<List<Category>> categories;
    private MutableLiveData<Map<Category, List<Finance>>> mapCategoryFinance;
    private MutableLiveData<List<DateRange.Date>> dates;
    private MutableLiveData<Map<String, List<Finance>>> mapTimeFinance;

    private MutableLiveData<List<Account>> accounts;

    public CategoryFinanceViewModel() {
    }

    public void initLiveData(ViewModelStoreOwner owner, LifecycleOwner lifecycleOwner) {
        financeViewModel = new ViewModelProvider(owner).get(FinanceViewModel.class);
        categoryViewModel = new ViewModelProvider(owner).get(CategoryViewModel.class);
        accountViewModel = new ViewModelProvider(owner).get(AccountViewModel.class);
        transferViewModel = new ViewModelProvider(owner).get(TransferViewModel.class);

        switchExpenseIncome.set(Helper.TYPE_EXPENSE);

        categories = new MutableLiveData<>();
        mapCategoryFinance = new MutableLiveData<>();
        accounts = new MutableLiveData<>();

        dates = new MutableLiveData<>();
        mapTimeFinance = new MutableLiveData<>();

        categoryViewModel.getCategories().observe(lifecycleOwner, allCategories -> {
            this.categories.setValue(allCategories);

            financeViewModel.getAllFinances().observe(lifecycleOwner, finances -> {
                Map<Category, List<Finance>> map = new TreeMap<>((c1, c2) -> c1.getCategoryId() - c2.getCategoryId());

                for (Category c : allCategories) {
                    map.put(c, new ArrayList<>());
                }

                for (Finance f : finances) {
                    for (Category c : map.keySet()) {
                        if (f.getCategoryId() == c.getCategoryId()) {
                            map.get(c).add(f);
                        }
                    }
                }

                for (Category c : map.keySet()) {
                    for (Finance f : map.get(c)) {
                        System.out.println(f.getFinanceId() + " " + f.getDetail());
                    }
                }


                // all finanes
                this.allFinances = map;

                // list finances of date range

//                this.mapFinance.setValue(map);
                update();
            });
        });

        accountViewModel.getAccounts().observe(lifecycleOwner, accounts -> {
            this.accounts.setValue(accounts);
        });

        transferViewModel.getTransfers().observe(lifecycleOwner, transfers -> {
            allTransfer = transfers;
        });
    }

    private void update() {
        DateRange rangeValue = dateRange.get();

        Map<Category, List<Finance>> mapRange = new TreeMap<>((c1, c2) -> c1.getCategoryId() - c2.getCategoryId());
        Map<String, List<Finance>> mapTime = new TreeMap<>((o1, o2) -> {
            DateRange.Date d1 = new DateRange.Date(o1);
            DateRange.Date d2 = new DateRange.Date(o2);
            return d1.compare(d2);
        });

        for (Category c : allFinances.keySet()) {
            mapRange.put(c, new ArrayList<>());

            for (Finance f : allFinances.get(c)) {
                String strDate = f.getDateTime().split("-")[0].trim();
                DateRange.Date date = new DateRange.Date(strDate);
                if (date.compare(rangeValue.getStartDate()) >= 0 && date.compare(rangeValue.getEndDate()) <= 0) {
                    if (!mapTime.containsKey(strDate))
                        mapTime.put(strDate, new ArrayList<>());

                    mapTime.get(strDate).add(f);
                    mapRange.get(c).add(f);
                }

            }
        }

        Log.d("MMM", "update on view model");

        this.mapCategoryFinance.setValue(mapRange);
        this.mapTimeFinance.setValue(mapTime);

    }

    public void setDateRangeValue(DateRange rangeValue) {
        dateRange.set(rangeValue);
        dateRange.notifyChange();
        update();
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

    public LiveData<Map<Category, List<Finance>>> getMapCategoryFinance() {
        return mapCategoryFinance;
    }

    public LiveData<Map<String, List<Finance>>> getMapTimeFinance() {
        return mapTimeFinance;
    }

    public LiveData<List<Account>> getAccounts() {
        return accounts;
    }

    public List<Transfer> getAllTransfer() {
        return allTransfer;
    }
}
