package com.vinh.moneymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vinh.moneymanager.R;

import java.util.Calendar;

public class AddEditFinanceActivity extends AppCompatActivity {

    private TextView tvCategory, tvDay, tvTime, tvAccount;
    private EditText edCost, edNote;

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_finance);

        tvCategory = findViewById(R.id.tv_category_name);
        tvDay = findViewById(R.id.tv_day_finance);
        tvTime = findViewById(R.id.tv_time_finance);
        tvAccount = findViewById(R.id.tv_account_finance);
        edCost = findViewById(R.id.ed_cost_finance);
        edNote = findViewById(R.id.ed_note_finance);
        btnSubmit = findViewById(R.id.btn_submit_finance);

        btnSubmit.setOnClickListener((v) -> {
            Intent dataReturn = new Intent();
            dataReturn.putExtra("category_id", 1);
            dataReturn.putExtra("datetime", tvDay.getText() + " - " + tvTime.getText());
            dataReturn.putExtra("account_id", 1);
            dataReturn.putExtra("cost", Integer.parseInt(edCost.getText().toString()));
            dataReturn.putExtra("detail", edNote.getText().toString().trim());

            setResult(RESULT_OK, dataReturn);
            finish();
        });

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

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}