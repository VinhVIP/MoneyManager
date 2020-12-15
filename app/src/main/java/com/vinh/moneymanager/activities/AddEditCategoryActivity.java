package com.vinh.moneymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vinh.moneymanager.R;

public class AddEditCategoryActivity extends AppCompatActivity {

    private RadioButton radioIncome, radioExpense;
    private EditText edName, edDescription;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        radioIncome = findViewById(R.id.radio_income);
        radioExpense = findViewById(R.id.radio_expense);
        edName = findViewById(R.id.ed_category_name);
        edDescription = findViewById(R.id.ed_description);
        btnSubmit = findViewById(R.id.btn_submit_category);

        btnSubmit.setOnClickListener((v) -> {
            if (edName.getText().toString().trim().length() > 0) {
                Intent dataReturn = new Intent();
                dataReturn.putExtra("type", radioIncome.isChecked() ? 1 : 0);
                dataReturn.putExtra("name", edName.getText().toString().trim());
                dataReturn.putExtra("description", edDescription.getText().toString().trim());
                setResult(RESULT_OK, dataReturn);
                finish();
            } else {
                Toast.makeText(this, "Tên danh mục không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}