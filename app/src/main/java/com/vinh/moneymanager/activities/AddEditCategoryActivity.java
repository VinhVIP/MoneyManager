package com.vinh.moneymanager.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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

import static com.vinh.moneymanager.libs.Helper.iconsExpense;
import static com.vinh.moneymanager.libs.Helper.iconsIncome;

public class AddEditCategoryActivity extends AppCompatActivity {

    private CategoryViewModel categoryViewModel;

    private RadioButton radioIncome, radioExpense;
    private EditText edName, edDescription;
    private Button btnSubmit;
    private ImageView imgIcon;

    private int categoryId = 0;
    private int iconIndex = 0;

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
        imgIcon = findViewById(R.id.imgIcon);

        btnSubmit.setOnClickListener((v) -> addEditCategory());

        imgIcon.setOnClickListener(v -> showDialogSelectIcon());

        radioExpense.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                imgIcon.setImageResource(iconsExpense[0]);
            } else {
                imgIcon.setImageResource(iconsIncome[0]);
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.TitleFont);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            finish();
            onBackPressed();
        });

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
            iconIndex = data.getInt(Helper.CATEGORY_ICON);

            getSupportActionBar().setTitle("Chỉnh sửa");
            btnSubmit.setText("Chỉnh sửa");
        } else {
            Bundle data = getIntent().getBundleExtra(Helper.ADD_CATEGORY);
            type = data.getInt(Helper.CATEGORY_TYPE);

            getSupportActionBar().setTitle("Thêm danh mục");
            btnSubmit.setText("Thêm");
        }

        if (type == Helper.TYPE_INCOME) radioIncome.setChecked(true);
        else radioExpense.setChecked(true);
        imgIcon.setImageResource(type == Helper.TYPE_EXPENSE ? iconsExpense[iconIndex] : iconsIncome[iconIndex]);
    }

    private void addEditCategory() {
        String categoryName = edName.getText().toString().trim();

        if (categoryName.isEmpty()) {
            Toast.makeText(this, "Tên danh mục không được bỏ trống", Toast.LENGTH_SHORT).show();
            return;
        }

        Category newCategory = new Category(categoryName,
                radioIncome.isChecked() ? Helper.TYPE_INCOME : Helper.TYPE_EXPENSE,
                edDescription.getText().toString().trim(), iconIndex);

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
            } else {
                newCategory.setCategoryId(categoryId);
                categoryViewModel.update(newCategory);
            }

        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                Toast.makeText(this, "DM", Toast.LENGTH_SHORT).show();
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


    private void showDialogSelectIcon() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_icon);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = dialog.findViewById(R.id.img_close_dialog);
        imgClose.setOnClickListener((v) -> dialog.cancel());

        boolean isExpense = radioExpense.isChecked();

        GridView gridView = dialog.findViewById(R.id.grid_view_icon);
        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return isExpense ? iconsExpense.length : iconsIncome.length;
            }

            @Override
            public Integer getItem(int position) {
                if (isExpense) return iconsExpense[position];
                else return iconsIncome[position];
            }

            @Override
            public long getItemId(int position) {
                if (isExpense) return iconsExpense[position];
                else return iconsIncome[position];
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(AddEditCategoryActivity.this).inflate(R.layout.single_image, null);
                    ImageView imageView = convertView.findViewById(R.id.img_view_icon);
                    imageView.setImageResource(getItem(position));
                }
                return convertView;
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            iconIndex = position;
            imgIcon.setImageResource(isExpense ? iconsExpense[position] : iconsIncome[position]);
            dialog.cancel();
        });


        dialog.show();
    }
}