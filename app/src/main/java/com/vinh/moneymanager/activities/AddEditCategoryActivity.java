package com.vinh.moneymanager.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.viewmodels.CategoryViewModel;

public class AddEditCategoryActivity extends AppCompatActivity {

    private CategoryViewModel categoryViewModel;

    private RadioButton radioIncome, radioExpense;
    private EditText edName, edDescription;
    private Button btnSubmit;

    private int categoryId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        radioIncome = findViewById(R.id.radio_income);
        radioExpense = findViewById(R.id.radio_expense);
        edName = findViewById(R.id.ed_category_name);
        edDescription = findViewById(R.id.ed_description);
        btnSubmit = findViewById(R.id.btn_submit_category);

        btnSubmit.setOnClickListener((v) -> {
            addEditCategory();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        getData();
    }

    private void getData() {
        int type = 1;

        if (getIntent().hasExtra(Helper.EDIT_CATEGORY)) {
            Bundle data = getIntent().getBundleExtra(Helper.EDIT_CATEGORY);

            categoryId = data.getInt(Helper.CATEGORY_ID);
            edName.setText(data.getString(Helper.CATEGORY_NAME));
            edDescription.setText(data.getString(Helper.CATEGORY_DESCRIPTION));

            type = data.getInt(Helper.CATEGORY_TYPE);

            getSupportActionBar().setTitle("Chỉnh sửa");
        } else {
            Bundle data = getIntent().getBundleExtra(Helper.ADD_CATEGORY);
            type = data.getInt(Helper.CATEGORY_TYPE);

            getSupportActionBar().setTitle("Thêm danh mục");
        }

        if (type == Helper.TYPE_INCOME) radioIncome.setChecked(true);
        else radioExpense.setChecked(true);
    }

    private void addEditCategory() {
        String categoryName = edName.getText().toString().trim();

        if (categoryName.isEmpty()) {
            Toast.makeText(this, "Tên danh mục không được bỏ trống", Toast.LENGTH_SHORT).show();
            return;
        }

        Category newCategory = new Category(categoryName,
                radioIncome.isChecked() ? Helper.TYPE_INCOME : Helper.TYPE_EXPENSE,
                edDescription.getText().toString().trim());

        if (categoryId == 0) {
            // Thêm 1 danh mục mới
            if (categoryViewModel.isExists(categoryName)) {
                Toast.makeText(this, "Tên danh mục đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            } else {
                categoryViewModel.insert(newCategory);
            }
        } else {
            // Update 1 danh mục có sẵn theo ID

            Category oldCategory = categoryViewModel.getCategory(categoryId);

            if (categoryName.equals(oldCategory.getName())) {
                // Nếu giữ nguyên tên danh mục thì tiến hành update như bth
                newCategory.setCategoryId(categoryId);
                categoryViewModel.update(newCategory);
            } else if (categoryViewModel.isExists(categoryName)) {
                // Tên danh mục thay đổi nhưng trùng tên với 1 danh mục khác
                Toast.makeText(this, "Tên danh mục đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }else{
                newCategory.setCategoryId(categoryId);
                categoryViewModel.update(newCategory);
            }

        }

        finish();
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