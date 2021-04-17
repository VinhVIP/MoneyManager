package com.vinh.moneymanager.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.databinding.ActivityAddEditFinanceBinding;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.viewmodels.AccountViewModel;
import com.vinh.moneymanager.viewmodels.AddEditFinanceViewModel;
import com.vinh.moneymanager.viewmodels.CategoryViewModel;
import com.vinh.moneymanager.viewmodels.FinanceViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEditFinanceActivity extends AppCompatActivity implements View.OnClickListener {

    private AddEditFinanceViewModel mViewModel;

    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;
    private FinanceViewModel financeViewModel;


    private TextView tvCategory, tvDay, tvTime, tvAccount, tvAccountIn;
    private EditText edCost, edTransferFee, edDetail;
    private Button btnSubmit;
    private TextView swIncome, swExpense, swTransfer;

    private List<Category> mCategories = new ArrayList<>(), allCategories = new ArrayList<>();
    private List<Account> allAccounts = new ArrayList<>();

    private Finance currentFinance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAddEditFinanceBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_finance);

        mViewModel = new AddEditFinanceViewModel();
        binding.setViewModel(mViewModel);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        tvCategory = findViewById(R.id.tv_category_name);
        tvDay = findViewById(R.id.tv_day_finance);
        tvTime = findViewById(R.id.tv_time_finance);
        tvAccount = findViewById(R.id.tv_account_finance);
        tvAccountIn = findViewById(R.id.tv_account_finance_in);
        edCost = findViewById(R.id.ed_cost_finance);
        edTransferFee = findViewById(R.id.ed_transfer_fee);
        edDetail = findViewById(R.id.ed_note_finance);
        btnSubmit = findViewById(R.id.btn_submit_finance);
        swIncome = findViewById(R.id.sw_finance_income);
        swExpense = findViewById(R.id.sw_finance_expense);
        swTransfer = findViewById(R.id.sw_finance_transfer);

        swIncome.setOnClickListener(this);
        swExpense.setOnClickListener(this);
        swTransfer.setOnClickListener(this);

        tvDay.setOnClickListener((v) -> showDialogChooseDay(new DateRange.Date(tvDay.getText().toString())));
        tvTime.setOnClickListener((v) -> showDialogChooseTime(tvTime.getText().toString()));

        btnSubmit.setOnClickListener((v) -> {
            if (mViewModel.categoryType.get() == 2) {
                transfer();
            } else {
                finance();
            }
        });


        selectCategory();
        selectAccount();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);

        getData();

    }

    private void getData() {
        Intent data = getIntent();
        Calendar calendar = Calendar.getInstance();

        int financeId = data.getIntExtra(Helper.FINANCE_ID, -1);
        int categoryId = data.getIntExtra(Helper.CATEGORY_TYPE, -1);
        int accountId = -1;
        String dateTime = "", detail = "";
        long cost = 0;


        if (data.hasExtra(Helper.FINANCE_DATETIME)) {
            accountId = data.getIntExtra(Helper.ACCOUNT_ID, -1);

            dateTime = data.getStringExtra(Helper.FINANCE_DATETIME);
            cost = data.getLongExtra(Helper.FINANCE_COST, 0);
            detail = data.getStringExtra(Helper.FINANCE_DETAIL);

            getSupportActionBar().setTitle("Chỉnh sửa");
        } else {
            tvDay.setText(String.format("%02d/%02d/%d", calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)));
            tvTime.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));

            getSupportActionBar().setTitle("Thêm");
        }

        currentFinance = new Finance(cost, dateTime, detail, categoryId, accountId);
        currentFinance.setId(financeId);


        setupDataPreview();

    }

    private void setupDataPreview() {
        mViewModel.categoryType.set(getIntent().getIntExtra(Helper.CATEGORY_TYPE, 0));

//        tvCategory.setText(getIntent().getStringExtra(Helper.CATEGORY_NAME));
//        tvAccount.setText(getIntent().getStringExtra(Helper.ACCOUNT_NAME));

        if (currentFinance.getDateTime().length() > 0) {
            String[] dateTimeArray = currentFinance.getDateTime().split("-");
            tvDay.setText(dateTimeArray[0].trim());
            tvTime.setText(dateTimeArray[1].trim());
        }


        if (currentFinance.getCost() > 0)
            mViewModel.setBalance(String.valueOf(currentFinance.getCost()));

        edDetail.setText(currentFinance.getDetail());

    }

    private void transfer() {
        Account accountOut = mViewModel.account.get();
        Account accountIn = mViewModel.accountIn.get();

        if (accountIn != null && accountOut != null) {
            if (edCost.getText().toString().isEmpty()) {
                Toast.makeText(this, "Chưa nhập số tiền", Toast.LENGTH_SHORT).show();
            } else {
                long cost = Long.parseLong(Helper.clearDotInText(edCost.getText().toString()));
                long fee = 0;

                if (!edTransferFee.getText().toString().isEmpty()) {
                    fee = Long.parseLong(Helper.clearDotInText(edTransferFee.getText().toString()));
                }

                accountOut.setBalance(accountOut.getBalance() - cost - fee);
                accountIn.setBalance(accountIn.getBalance() + cost);

                accountViewModel.update(accountOut);
                accountViewModel.update(accountIn);

                finish();
            }

        } else {
            Toast.makeText(this, "Vui lòng chọn tài khoản xuất và nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void finance() {
        if (edCost.getText().toString().isEmpty()) {
            Toast.makeText(this, "Chưa nhập số tiền", Toast.LENGTH_SHORT).show();
        } else {
            financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);

            long cost = Long.parseLong(Helper.clearDotInText(edCost.getText().toString()));
            String dateTime = tvDay.getText() + " - " + tvTime.getText();
            String detail = edDetail.getText().toString().trim();
            int categoryId = mViewModel.category.get().getId();
            int accountId = mViewModel.account.get().getId();

            Finance finance = new Finance(cost, dateTime, detail, categoryId, accountId);

            if (currentFinance.getId() != -1) {
                // Chỉnh sửa finance có sẵn

                finance.setId(currentFinance.getId());
                financeViewModel.update(finance);

                // accountBefore là tài khoản sử dụng trong đợt chi tiêu trước đó, mà bây giờ muốn chỉnh sửa
                Account accountBefore = getAccount(currentFinance.getAccountId());
                assert (accountBefore != null);

                Category categoryBefore = getCategory(currentFinance.getCategoryId());
                if (categoryBefore.getType() == 0) {
                    // Thu => Trừ đi số tiền đã thu
                    accountBefore.setBalance(accountBefore.getBalance() - currentFinance.getCost());
                } else if (categoryBefore.getType() == 1) {
                    // Chi => Cộng (hồi phục) lại số tiền đã chi tiêu
                    accountBefore.setBalance(accountBefore.getBalance() + currentFinance.getCost());
                }

                // Cập nhật lại accountBefore
                accountViewModel.update(accountBefore);

            } else {
                // Thêm mởi finance
                financeViewModel.insert(finance);
            }

            Account accountNow = mViewModel.account.get();
            assert (accountNow != null);

            Category categoryNow = getCategory(finance.getCategoryId());
            if (categoryNow.getType() == 0) {
                // Thu => Cộng vào số tiền thu nhập
                accountNow.setBalance(accountNow.getBalance() + finance.getCost());
            } else if (categoryNow.getType() == 1) {
                // Chi => Trừ đi số tiền đã chi tiêu
                accountNow.setBalance(accountNow.getBalance() - finance.getCost());
            }

            // Cập nhật lại accountNow
            accountViewModel.update(accountNow);

            finish();
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

    private void selectCategory() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getCategories().observe(this, categories -> {
            allCategories = categories;

            int selectedCategoryId = getIntent().getIntExtra(Helper.CATEGORY_ID, 0);
            mViewModel.category.set(getCategory(selectedCategoryId));

            updateCategoriesSelect();
        });


        tvCategory.setOnClickListener((v) -> showDialogSelectCategory());
    }

    /*
    Cập nhật lại danh sách danh mục theo menu "Thu nhập" hoặc "Chi tiêu"
     */
    private void updateCategoriesSelect() {
        mCategories.clear();
        for (Category c : allCategories) {
            if (c.getType() == mViewModel.categoryType.get()) {
                mCategories.add(c);
            }
        }

        Category currentCategorySelected = mViewModel.category.get();
        if (currentCategorySelected == null) return;

        assert (currentCategorySelected != null);

        if (currentCategorySelected.getType() != mViewModel.categoryType.get()) {
            if (!mCategories.isEmpty()) {
                mViewModel.category.set(mCategories.get(0));

//                selectedCategoryId = mCategories.get(0).getId();
//                tvCategory.setText(mCategories.get(0).getName());
            }
        }
    }

    private Category getCategory(int categoryId) {
        for (Category c : allCategories) {
            if (c.getId() == categoryId) return c;
        }
        return null;
    }

    private void showDialogSelectCategory() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_list);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = dialog.findViewById(R.id.img_close_dialog);
        imgClose.setOnClickListener(v -> dialog.cancel());

        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_title);
        tvTitle.setText("Danh mục");

        ListView listView = dialog.findViewById(R.id.list_category_dialog);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mCategories.size();
            }

            @Override
            public Object getItem(int position) {
                return mCategories.get(position);
            }

            @Override
            public long getItemId(int position) {
                return mCategories.get(position).getId();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(AddEditFinanceActivity.this).inflate(R.layout.list_image_text, null);
                    ImageView imageView = convertView.findViewById(R.id.image_view);
                    TextView tvName = convertView.findViewById(R.id.text_view);

                    tvName.setText(mCategories.get(position).getName());
                }
                return convertView;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            mViewModel.category.set(mCategories.get(position));

//            selectedCategoryId = mCategories.get(position).getId();
//            tvCategory.setText(mCategories.get(position).getName());
            dialog.cancel();
        });
        dialog.show();
    }

    private void selectAccount() {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        accountViewModel.getAccounts().observe(this, accounts -> {
            allAccounts = accounts;

            int selectedAccountId = getIntent().getIntExtra(Helper.ACCOUNT_ID, -1);
            if (selectedAccountId != -1) mViewModel.account.set(getAccount(selectedAccountId));
        });

        tvAccount.setOnClickListener((v) -> showDialogSelectAccount(false));
        tvAccountIn.setOnClickListener((v) -> showDialogSelectAccount(true));
    }

    private Account getAccount(int accountId) {
        for (Account a : allAccounts) {
            if (a.getId() == accountId) return a;
        }
        return null;
    }

    private void showDialogSelectAccount(boolean isAccountIn) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_list);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = dialog.findViewById(R.id.img_close_dialog);
        imgClose.setOnClickListener((v) -> dialog.cancel());

        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_title);
        tvTitle.setText("Tài khoản");

        ListView listView = dialog.findViewById(R.id.list_category_dialog);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return allAccounts.size();
            }

            @Override
            public Object getItem(int position) {
                return allAccounts.get(position);
            }

            @Override
            public long getItemId(int position) {
                return allAccounts.get(position).getId();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(AddEditFinanceActivity.this).inflate(R.layout.list_image_text, null);
                    ImageView imageView = convertView.findViewById(R.id.image_view);
                    TextView tvName = convertView.findViewById(R.id.text_view);

                    tvName.setText(allAccounts.get(position).getAccountName());
                }
                return convertView;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (isAccountIn) mViewModel.accountIn.set(allAccounts.get(position));
            else mViewModel.account.set(allAccounts.get(position));

            dialog.cancel();
        });
        dialog.show();
    }

    private void showDialogChooseDay(DateRange.Date date) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    tvDay.setText(String.format("%02d/%02d/%02d", dayOfMonth, month + 1, year));
                },
                date.getYear(),
                date.getMonth() - 1,
                date.getDay());
        datePickerDialog.show();
    }

    private void showDialogChooseTime(String time) {
        String[] s = time.split(":");
        int h = Integer.parseInt(s[0]);
        int m = Integer.parseInt(s[1]);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tvTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            }

        }, h, m, true);
        timePickerDialog.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sw_finance_income:
                if (mViewModel.categoryType.get() != 0) {
                    mViewModel.categoryType.set(0);
                    updateCategoriesSelect();
                }

                break;
            case R.id.sw_finance_expense:
                if (mViewModel.categoryType.get() != 1) {
                    mViewModel.categoryType.set(1);
                    updateCategoriesSelect();
                }

                break;
            case R.id.sw_finance_transfer:
                if (mViewModel.categoryType.get() != 2) {
                    mViewModel.categoryType.set(2);
                }

                break;
        }


    }
}