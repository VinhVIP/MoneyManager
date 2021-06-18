package com.vinh.moneymanager.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.adapters.RecyclerSearchAdapter;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.viewmodels.FinanceViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ImageView imgBack;
    private EditText edSearch;
    private TextView tvMess;
    private RecyclerView recyclerView;
    private RecyclerSearchAdapter adapter;

    private FinanceViewModel financeViewModel;

    private List<Finance> searchFinance = new ArrayList<>();
    private List<Finance> allFinances = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);
        financeViewModel.getAllFinances().observe(this, finances -> {
            allFinances = finances;
            adapter.setFinances(finances);
        });

        tvMess = findViewById(R.id.tv_mess);

        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> finish());

        edSearch = findViewById(R.id.ed_search);
        recyclerView = findViewById(R.id.recycler_finance_search);
        adapter = new RecyclerSearchAdapter(this, new ArrayList<>(), null);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                if(searchFinance.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    tvMess.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    tvMess.setVisibility(View.GONE);
                }
            }
        });
    }
}