package com.vinh.moneymanager.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.databinding.ActivityAddEditCategoryBinding;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.viewmodels.AddEditCategoryViewModel;
import com.vinh.moneymanager.viewmodels.CategoryViewModel;
import com.vinh.moneymanager.viewmodels.FinanceViewModel;

import java.util.List;

import static com.vinh.moneymanager.libs.Helper.iconsExpense;
import static com.vinh.moneymanager.libs.Helper.iconsIncome;

public class AddEditCategoryActivity extends AppCompatActivity {

    private AddEditCategoryViewModel mViewModel;

    private CategoryViewModel categoryViewModel;
    private FinanceViewModel financeViewModel;

    private HandlerClick handler;

    private List<Category> allCategories;
    private List<Finance> allFinances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddEditCategoryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_category);
        setupToolbar();

        mViewModel = new AddEditCategoryViewModel();
        handler = new HandlerClick();

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);

        categoryViewModel.getCategories().observe(this, categories -> allCategories = categories);
        financeViewModel.getAllFinances().observe(this, finances -> allFinances = finances);

        getData();

        binding.setViewModel(mViewModel);
        binding.setHandler(handler);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.TitleFont);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            finish();
            onBackPressed();
        });
    }

    private void getData() {
        if (getIntent().hasExtra(Helper.EDIT_CATEGORY)) {
            Bundle data = getIntent().getBundleExtra(Helper.EDIT_CATEGORY);

            int categoryId = data.getInt(Helper.CATEGORY_ID, 0);
            String name = data.getString(Helper.CATEGORY_NAME);
            String desc = data.getString(Helper.CATEGORY_DESCRIPTION);
            int type = data.getInt(Helper.CATEGORY_TYPE, 0);
            int iconIndex = data.getInt(Helper.CATEGORY_ICON, 0);

            Category category = new Category(name, type, desc, iconIndex);
            category.setCategoryId(categoryId);

            mViewModel.setCategory(category);

            getSupportActionBar().setTitle("Chỉnh sửa");
            mViewModel.setButtonText("Chỉnh sửa");
        } else {
            Bundle data = getIntent().getBundleExtra(Helper.ADD_CATEGORY);
            int type = data.getInt(Helper.CATEGORY_TYPE);

            mViewModel.setType(type);
            mViewModel.setIcon(0);

            getSupportActionBar().setTitle("Thêm danh mục");
            mViewModel.setButtonText("Thêm");
        }

//        if (type == Helper.TYPE_INCOME) radioIncome.setChecked(true);
//        else radioExpense.setChecked(true);
//        imgIcon.setImageResource(type == Helper.TYPE_EXPENSE ? iconsExpense[iconIndex] : iconsIncome[iconIndex]);
    }

    private void submitCategory() {
        if (isCategoryNameValid()) {
            Category category = mViewModel.getCategory();

            if (category.getCategoryId() == 0) {
                categoryViewModel.insert(category);
            } else {
                categoryViewModel.update(category);
            }
            finish();
        }
    }

    private boolean isCategoryNameValid() {
        String name = mViewModel.getName();

        if (name.isEmpty()) {
            Toast.makeText(this, "Tên danh mục không được bỏ trống!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (isCategoryNameExists(name, mViewModel.getCategory().getCategoryId())) {
            Toast.makeText(this, "Tên danh mục đã tồn tại!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Kiểm tra tên danh mục đã tồn tại hay chưa
     *
     * @param name     Tên danh mục
     * @param ignoreId ID danh mục bỏ qua khi kiểm tra (bỏ qua trường hợp tự so sánh với chính nó)
     */
    private boolean isCategoryNameExists(String name, int ignoreId) {
        if (allCategories == null || allCategories.isEmpty()) return false;

        for (Category c : allCategories) {
            if (c.getCategoryId() != ignoreId && c.getName().equals(name)) return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.finance_activity_menu, menu);

        // ẩn nút delete khi
        if (!getIntent().hasExtra(Helper.EDIT_CATEGORY)) {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                deleteCategory();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCategory() {
        Category category = mViewModel.getCategory();
        if (canDeleteCategory(category.getCategoryId())) {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có xác định muốn xóa danh mục này?")
                    .setPositiveButton("XÓA", (dialog, which) -> {
                        categoryViewModel.delete(category);
                        dialog.cancel();
                        Log.d("MMM", "Category Deleted");
                        finish();
                    }).setNegativeButton("HỦY", (dialog, which) -> dialog.cancel()).show();

        } else {
            Toast.makeText(this, "Danh mục đã được sử dụng trong các giao dịch nên không thể xóa!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean canDeleteCategory(int id) {
        for (Finance f : allFinances) {
            if (f.getCategoryId() == id) return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }


    public class HandlerClick {
        Dialog dialog;

        public HandlerClick() {
            dialog = new Dialog(AddEditCategoryActivity.this);
            dialog.setContentView(R.layout.dialog_icon);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView imgClose = dialog.findViewById(R.id.img_close_dialog);
            imgClose.setOnClickListener((v) -> dialog.cancel());

            boolean isExpense = mViewModel.getType() == Helper.TYPE_EXPENSE;

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
                mViewModel.setIcon(position);
                dialog.cancel();
            });
        }

        public void submit() {
            submitCategory();
        }

        public void showDialogSelectIcon() {
            dialog.show();
        }

        public void onTypeChanged(RadioGroup group, int id) {
            System.out.println("ADM");
            RadioButton radio = (RadioButton) group.getChildAt(0);
            mViewModel.setType(radio.isChecked() ? Helper.TYPE_INCOME : Helper.TYPE_EXPENSE);
        }
    }

}