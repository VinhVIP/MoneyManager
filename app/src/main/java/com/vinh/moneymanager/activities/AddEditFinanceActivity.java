package com.vinh.moneymanager.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vinh.moneymanager.R;

import java.util.Calendar;

public class AddEditFinanceActivity extends AppCompatActivity {

    private TextView tvDay, tvTime, tvAccount;
    private EditText edCost, edNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_finance);

        tvDay = findViewById(R.id.tv_day_finance);
        tvTime = findViewById(R.id.tv_time_finance);

        initDateTime();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        tvDay.setText(String.format("%02d/%02d/%02d", calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)));
        tvTime.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
    }
}