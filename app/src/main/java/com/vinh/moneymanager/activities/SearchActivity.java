package com.vinh.moneymanager.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.adapters.RecyclerSearchAdapter;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.listeners.OnItemSearchListener;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.room.entities.Transfer;
import com.vinh.moneymanager.viewmodels.AccountViewModel;
import com.vinh.moneymanager.viewmodels.CategoryViewModel;
import com.vinh.moneymanager.viewmodels.FinanceViewModel;
import com.vinh.moneymanager.viewmodels.TransferViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnItemSearchListener {

    private ImageView imgBack, imgSetting;
    private EditText edSearch;
    private TextView tvMess;
    private RecyclerView recyclerView;
    private RecyclerSearchAdapter adapter;

    private FinanceViewModel financeViewModel;
    private TransferViewModel transferViewModel;
    private CategoryViewModel categoryViewModel;
    private AccountViewModel accountViewModel;

    private ArrayList items = new ArrayList<>();
    private List<Finance> allFinances = new ArrayList<>();
    private List<Transfer> allTransfers = new ArrayList<>();

    private List<Category> allCategories = new ArrayList<>();

    private Dialog dialog;
    private DateRange.Date startDate, endDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edSearch = findViewById(R.id.ed_search);
        recyclerView = findViewById(R.id.recycler_finance_search);
        adapter = new RecyclerSearchAdapter(this, new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvMess = findViewById(R.id.tv_mess);

        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> finish());

        imgSetting = findViewById(R.id.img_setting);
        imgSetting.setOnClickListener(v -> {
            dialog.show();
        });

        observeData();
        handlerSearch();

        startDate = new DateRange.Date(1, 1, 2021);
        endDate = new DateRange.Date(1, 12, 2021);
        settingDialog();
    }

    private void observeData() {
        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);
        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

//        financeViewModel.getAllFinances().observe(this, finances -> {
//            allFinances = finances;
//            updateDataList(edSearch.getText().toString().trim().toLowerCase());
//        });
//        transferViewModel.getTransfers().observe(this, transfers -> {
//            allTransfers = transfers;
//            updateDataList(edSearch.getText().toString().trim().toLowerCase());
//        });
        categoryViewModel.getCategories().observe(this, categories -> {
            allCategories = categories;
            adapter.setCategories(categories);
        });
        accountViewModel.getAccounts().observe(this, accounts -> adapter.setAccounts(accounts));
        // Search
        financeViewModel.search().observe(this, finances -> {
            if (finances != null) items.addAll(finances);
            updateDataList();
        });
        transferViewModel.search().observe(this, transfers -> {
            if (transfers != null) items.addAll(transfers);
            updateDataList();
        });
    }

    private void handlerSearch() {
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().toLowerCase();
                adapter.setKeyword(keyword.trim());
                items.clear();
                if (keyword.trim().length() > 0) {
                    financeViewModel.search("%" + keyword + "%", SearchActivity.this);
                    transferViewModel.search("%" + keyword + "%", SearchActivity.this);
                } else {
                    adapter.setData(items);
                    updateDataList();
                }
            }
        });

    }

    private void updateDataList() {
        ArrayList list = new ArrayList();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof Finance) {
                if (canAddToList((Finance) items.get(i))) list.add(items.get(i));
            } else if (items.get(i) instanceof Transfer) {
                if (isFilterTransfer && checkTransferTime((Transfer) items.get(i)))
                    list.add(items.get(i));
            }
        }
        items.clear();
        items.addAll(list);
        adapter.setData(items);
        Log.e("MMM", "data size: " + items.size());

        if (items.isEmpty()) {
            showMessage();
        } else {
            hideMessage();
        }
    }

    private Category getCategory(int categoryId) {
        for (Category c : allCategories) {
            if (c.getCategoryId() == categoryId) return c;
        }
        return null;
    }

    private void showMessage() {
        recyclerView.setVisibility(View.GONE);
        tvMess.setVisibility(View.VISIBLE);
        if (edSearch.getText().toString().isEmpty()) {
            tvMess.setText("Hãy nhập nội dung muốn tìm kiếm!");
        } else {
            tvMess.setText("Không tìm thấy khoản giao dịch nào!");
        }
    }

    private void hideMessage() {
        recyclerView.setVisibility(View.VISIBLE);
        tvMess.setVisibility(View.GONE);
    }

    private boolean canAddToList(Finance finance) {
        return checkFinanceType(finance) && checkFinanceTime(finance);
    }

    private boolean checkFinanceType(Finance finance) {
        Category c = getCategory(finance.getCategoryId());
        if (isFilterIncome && c.getType() == Helper.TYPE_INCOME) return true;
        return isFilterExpense && c.getType() == Helper.TYPE_EXPENSE;
    }

    private boolean checkFinanceTime(Finance finance) {
        if (isFilterAllTime) return true;

        String strDate = finance.getDateTime().split("-")[0].trim();
        DateRange.Date date = new DateRange.Date(strDate);
        if (date.compare(startDate) >= 0 && date.compare(endDate) <= 0) {
            return true;
        }
        if (date.compare(endDate) >= 0 && date.compare(startDate) <= 0) {
            return true;
        }
        return false;
    }

    private boolean checkTransferTime(Transfer transfer) {
        if (isFilterAllTime) return true;

        String strDate = transfer.getDateTime().split("-")[0].trim();
        DateRange.Date date = new DateRange.Date(strDate);
        if (date.compare(startDate) >= 0 && date.compare(endDate) <= 0) {
            return true;
        }
        if (date.compare(endDate) >= 0 && date.compare(startDate) <= 0) {
            return true;
        }
        return false;
    }

    private boolean isFilterIncome = true;
    private boolean isFilterExpense = true;
    private boolean isFilterTransfer = true;

    private boolean isFilterAllTime = true;

    private void settingDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_search_filter);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = dialog.findViewById(R.id.img_close_dialog);
        imgClose.setOnClickListener((v) -> dialog.cancel());

        CheckBox cbIncome = dialog.findViewById(R.id.cb_filter_income);
        CheckBox cbExpense = dialog.findViewById(R.id.cb_filter_expense);
        CheckBox cbTransfer = dialog.findViewById(R.id.cb_filter_transfer);

        RadioButton radioAllTime = dialog.findViewById(R.id.radio_all_time);
        RadioButton radioFixedTime = dialog.findViewById(R.id.radio_fixed_time);

        cbIncome.setOnCheckedChangeListener((buttonView, isChecked) -> isFilterIncome = isChecked);
        cbExpense.setOnCheckedChangeListener((buttonView, isChecked) -> isFilterExpense = isChecked);
        cbTransfer.setOnCheckedChangeListener((buttonView, isChecked) -> isFilterTransfer = isChecked);

        LinearLayout layoutFilterTime = dialog.findViewById(R.id.layout_filter_time);

        radioAllTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (radioAllTime.isChecked()) {
                layoutFilterTime.setVisibility(View.GONE);
            }
            isFilterAllTime = radioAllTime.isChecked();
        });

        radioFixedTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (radioFixedTime.isChecked()) {
                layoutFilterTime.setVisibility(View.VISIBLE);
            }
            isFilterAllTime = radioAllTime.isChecked();
        });

        Button btnApply = dialog.findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(v -> {
            String keyword = edSearch.getText().toString();
            items.clear();
            if (keyword.trim().length() > 0) {
                financeViewModel.search("%" + keyword + "%", SearchActivity.this);
                transferViewModel.search("%" + keyword + "%", SearchActivity.this);
            } else {
                adapter.setData(items);
                showMessage();
            }
            dialog.dismiss();
        });

        TextView tvStartDate = dialog.findViewById(R.id.tv_start_date);
        TextView tvEndDate = dialog.findViewById(R.id.tv_end_date);

        tvStartDate.setText(String.format("%02d/%02d/%d", startDate.getDay(), startDate.getMonth(), startDate.getYear()));
        tvEndDate.setText(String.format("%02d/%02d/%d", endDate.getDay(), endDate.getMonth(), endDate.getYear()));

        tvStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(SearchActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        tvStartDate.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
                        startDate = new DateRange.Date(dayOfMonth, month + 1, year);
                    },
                    startDate.getYear(),
                    startDate.getMonth() - 1,
                    startDate.getDay());
            datePickerDialog.show();
        });
        tvEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(SearchActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        tvEndDate.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
                        endDate = new DateRange.Date(dayOfMonth, month + 1, year);
                    },
                    endDate.getYear(),
                    endDate.getMonth() - 1,
                    endDate.getDay());
            datePickerDialog.show();
        });
    }

    @Override
    public void onFinanceClick(Finance finance, Category category) {
        Bundle dataSend = new Bundle();

        dataSend.putInt(Helper.FINANCE_ID, finance.getFinanceId());
        dataSend.putInt(Helper.CATEGORY_ID, finance.getCategoryId());
//        dataSend.putString(Helper.CATEGORY_NAME, category.getName());
        dataSend.putInt(Helper.CATEGORY_TYPE, category.getType());
        dataSend.putInt(Helper.ACCOUNT_ID, finance.getAccountId());
        dataSend.putString(Helper.FINANCE_DATETIME, finance.getDateTime());
        dataSend.putLong(Helper.FINANCE_COST, finance.getMoney());
        dataSend.putString(Helper.FINANCE_DETAIL, finance.getDetail());

        Intent intent = new Intent(this, AddEditFinanceActivity.class);
        intent.putExtra(Helper.EDIT_FINANCE, dataSend);

        startActivity(intent);
    }

    @Override
    public void onTransferClick(Transfer transfer) {
        Bundle bundle = new Bundle();

        bundle.putInt(Helper.TRANSFER_ID, transfer.getTransferId());
        bundle.putString(Helper.TRANSFER_DATETIME, transfer.getDateTime());
        bundle.putInt(Helper.TRANSFER_ACCOUNT_OUT_ID, transfer.getAccountOutId());
        bundle.putInt(Helper.TRANSFER_ACCOUNT_IN_ID, transfer.getAccountInId());
        bundle.putLong(Helper.TRANSFER_MONEY, transfer.getMoney());
        bundle.putLong(Helper.TRANSFER_FEE, transfer.getFee());
        bundle.putString(Helper.TRANSFER_DETAIL, transfer.getDetail());

        Intent intent = new Intent(this, AddEditFinanceActivity.class);
        intent.putExtra(Helper.EDIT_TRANSFER, bundle);
        startActivity(intent);
    }
}