package com.vinh.moneymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.Helper;

public class AddEditCategoryActivity extends AppCompatActivity {

    private RadioButton radioIncome, radioExpense;
    private EditText edName, edDescription;
    private Button btnSubmit;

    private int categoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_add_edit_category);

        radioIncome = findViewById(R.id.radio_income);
        radioExpense = findViewById(R.id.radio_expense);
        edName = findViewById(R.id.ed_category_name);
        edDescription = findViewById(R.id.ed_description);
        btnSubmit = findViewById(R.id.btn_submit_category);

        btnSubmit.setOnClickListener((v) -> {
            if (edName.getText().toString().trim().length() > 0) {
                Intent dataReturn = new Intent();
                dataReturn.putExtra(Helper.CATEGORY_ID, categoryId);
                dataReturn.putExtra(Helper.CATEGORY_TYPE, radioIncome.isChecked() ? 1 : 0);
                dataReturn.putExtra(Helper.CATEGORY_NAME, edName.getText().toString().trim());
                dataReturn.putExtra(Helper.CATEGORY_DESCRIPTION, edDescription.getText().toString().trim());
                setResult(RESULT_OK, dataReturn);
                finish();
            } else {
                Toast.makeText(this, "Tên danh mục không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        getData();
    }

    private void getData() {
        Intent data = getIntent();
        if (data.hasExtra(Helper.CATEGORY_ID)) {
            categoryId = data.getIntExtra(Helper.CATEGORY_ID, -1);
            edName.setText(data.getStringExtra(Helper.CATEGORY_NAME));
            edDescription.setText(data.getStringExtra(Helper.CATEGORY_DESCRIPTION));
            int type = data.getIntExtra(Helper.CATEGORY_TYPE, 0);
            if (type == 1) radioIncome.setChecked(true);
            else radioExpense.setChecked(true);

            getSupportActionBar().setTitle("Chỉnh sửa");
        } else {
            getSupportActionBar().setTitle("Thêm danh mục");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}