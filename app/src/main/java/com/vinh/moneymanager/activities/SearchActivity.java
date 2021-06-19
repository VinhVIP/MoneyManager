package com.vinh.moneymanager.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.viewmodels.AccountViewModel;
import com.vinh.moneymanager.viewmodels.CategoryViewModel;
import com.vinh.moneymanager.viewmodels.FinanceViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ImageView imgBack, imgSetting;
    private EditText edSearch;
    private TextView tvMess;
    private RecyclerView recyclerView;
    private RecyclerSearchAdapter adapter;

    private FinanceViewModel financeViewModel;
    private CategoryViewModel categoryViewModel;
    private AccountViewModel accountViewModel;

    private List<Finance> searchFinance = new ArrayList<>();
    private List<Finance> allFinances = new ArrayList<>();

    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edSearch = findViewById(R.id.ed_search);
        recyclerView = findViewById(R.id.recycler_finance_search);
        adapter = new RecyclerSearchAdapter(this, new ArrayList<>(), null);
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
        settingDialog();
        handlerSearch();
    }

    private void observeData() {
        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        financeViewModel.getAllFinances().observe(this, finances -> allFinances = finances);
        categoryViewModel.getCategories().observe(this, categories -> adapter.setCategories(categories));
        accountViewModel.getAccounts().observe(this, accounts -> adapter.setAccounts(accounts));
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
                searchFinance.clear();
                String keyword = s.toString().trim().toLowerCase();
                if (keyword.length() != 0) {
                    for (Finance f : allFinances) {
                        if (f.getDetail().toLowerCase().contains(keyword)) {
                            searchFinance.add(f);
                        }
                    }
                }
                adapter.setFinances(searchFinance);
                if (searchFinance.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    tvMess.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvMess.setVisibility(View.GONE);
                }
            }
        });

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
        });

        radioFixedTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (radioFixedTime.isChecked()) {
                layoutFilterTime.setVisibility(View.VISIBLE);
            }
        });

        Button btnApply = dialog.findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(v -> {
            dialog.dismiss();
        });

        TextView tvStartDate = dialog.findViewById(R.id.tv_start_date);
        TextView tvEndDate = dialog.findViewById(R.id.tv_end_date);
        tvStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(SearchActivity.this,
                    (view, year, month, dayOfMonth) -> tvStartDate.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)),
                    2021,
                    0,
                    1);
            datePickerDialog.show();
        });
        tvEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(SearchActivity.this,
                    (view, year, month, dayOfMonth) -> tvEndDate.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)),
                    2021,
                    0,
                    1);
            datePickerDialog.show();
        });
    }
}