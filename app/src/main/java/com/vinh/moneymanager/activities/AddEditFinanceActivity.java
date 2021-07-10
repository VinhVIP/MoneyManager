package com.vinh.moneymanager.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.vinh.moneymanager.room.entities.Transfer;
import com.vinh.moneymanager.viewmodels.AccountViewModel;
import com.vinh.moneymanager.viewmodels.AddEditFinanceViewModel;
import com.vinh.moneymanager.viewmodels.CategoryViewModel;
import com.vinh.moneymanager.viewmodels.FinanceViewModel;
import com.vinh.moneymanager.viewmodels.TransferViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEditFinanceActivity extends AppCompatActivity implements View.OnClickListener {

    private AddEditFinanceViewModel mViewModel;

    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;
    private FinanceViewModel financeViewModel;
    private TransferViewModel transferViewModel;


    private TextView tvCategory, tvDay, tvTime, tvAccount, tvAccountIn;
    private EditText edCost, edTransferFee, edDetail;
    private Button btnSubmit;
    private TextView swIncome, swExpense, swTransfer;

    private final List<Category> mCategories = new ArrayList<>();
    private List<Category> allCategories = new ArrayList<>();
    private List<Account> allAccounts = new ArrayList<>();

    private Finance currentFinance;
    private Transfer currentTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAddEditFinanceBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_finance);

        mViewModel = new AddEditFinanceViewModel();

        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);


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
            if (mViewModel.categoryType.get() == Helper.TYPE_TRANSFER) {
                transfer();
            } else {
                finance();
            }
        });

        selectCategory();
        selectAccount();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.TitleFont);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);

        getData();

        binding.setViewModel(mViewModel);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.finance_activity_menu, menu);

        // ẩn nút delete khi
        if (!getIntent().hasExtra(Helper.EDIT_FINANCE)) {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                deleteFinance();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteFinance() {
        if (currentFinance != null && currentFinance.getFinanceId() != -1) {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có xác định muốn xóa khoản này?")
                    .setPositiveButton("XÓA", (dialog, which) -> {
                        long money = currentFinance.getMoney();
                        Account account = getAccount(currentFinance.getAccountId());

                        Category category = getCategory(currentFinance.getCategoryId());
                        if(category.getType() == Helper.TYPE_INCOME){
                            account.setBalance(account.getBalance() - money);
                        }else if(category.getType() == Helper.TYPE_EXPENSE){
                            account.setBalance(account.getBalance() + money);
                        }

                        accountViewModel.update(account);

                        financeViewModel.delete(currentFinance);
                        Log.d("MM", "Finance Deleted: " + currentFinance.getFinanceId());
                        finish();
                    }).setNegativeButton("HỦY", (dialog, which) -> dialog.cancel()).show();

        }
    }

    private void getData() {

        if (getIntent().hasExtra(Helper.EDIT_FINANCE)) {
            // Chỉnh sửa finance
            Log.d("MM", "Edit Finance");

            Bundle data = getIntent().getBundleExtra(Helper.EDIT_FINANCE);

            int financeId = data.getInt(Helper.FINANCE_ID, 0);
            int categoryType = data.getInt(Helper.CATEGORY_TYPE, 0);
            int categoryId = data.getInt(Helper.CATEGORY_ID, 0);
            int accountId = data.getInt(Helper.ACCOUNT_ID, 0);
            String dateTime = data.getString(Helper.FINANCE_DATETIME);
            long cost = data.getLong(Helper.FINANCE_COST, 0);
            String detail = data.getString(Helper.FINANCE_DETAIL);

            currentFinance = new Finance(cost, dateTime, detail, categoryId, accountId);
            currentFinance.setFinanceId(financeId);


            // Cập nhật chế độ Danh mục
            mViewModel.categoryType.set(categoryType);
            if (cost > 0)
                mViewModel.setBalance(String.valueOf(cost));

        } else if (getIntent().hasExtra(Helper.ADD_FINANCE)) {
            // Thêm mới finance

            Bundle data = getIntent().getBundleExtra(Helper.ADD_FINANCE);
            int categoryType = data.getInt(Helper.CATEGORY_TYPE, 0);
            int categoryId = data.getInt(Helper.CATEGORY_ID, 0);
            String categoryName = data.getString(Helper.CATEGORY_NAME);

            Category category = new Category(categoryName, categoryType, "", 0);
            category.setCategoryId(categoryId);

            Log.d("MMM", "Add Finance Bef: " + categoryId + categoryName);

            // Cập nhật chế độ Danh mục
            mViewModel.categoryType.set(categoryType);

        } else if (getIntent().hasExtra(Helper.EDIT_TRANSFER)) {
            // Chỉnh sửa transfer
            Log.d("MM", "Edit Transfer");
            mViewModel.categoryType.set(Helper.TYPE_TRANSFER);

            Bundle data = getIntent().getBundleExtra(Helper.EDIT_TRANSFER);

            int transferId = data.getInt(Helper.TRANSFER_ID);
            String datetime = data.getString(Helper.TRANSFER_DATETIME);
            int accountOutId = data.getInt(Helper.TRANSFER_ACCOUNT_OUT_ID);
            int accountInId = data.getInt(Helper.TRANSFER_ACCOUNT_IN_ID);
            long money = data.getLong(Helper.TRANSFER_MONEY);
            long fee = data.getLong(Helper.TRANSFER_FEE);
            String detail = data.getString(Helper.TRANSFER_DETAIL);

            currentTransfer = new Transfer(money, fee, datetime, detail, accountOutId, accountInId);
            currentTransfer.setTransferId(transferId);

        } else if (getIntent().hasExtra(Helper.ADD_TRANSFER)) {
            Log.d("MM", "Add Transfer");
            mViewModel.categoryType.set(Helper.TYPE_TRANSFER);
        }

        previewData();

    }

    private void previewData() {
        Calendar calendar = Calendar.getInstance();

        if (currentFinance == null && currentTransfer == null) {
            getSupportActionBar().setTitle("Thêm");

            tvDay.setText(String.format("%02d/%02d/%d",
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)));
            tvTime.setText(String.format("%02d:%02d",
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)));

            return;
        }
        if (currentFinance != null) {
            getSupportActionBar().setTitle("Chỉnh sửa");

            String[] dateTimeArray = currentFinance.getDateTime().split("-");
            tvDay.setText(dateTimeArray[0].trim());
            tvTime.setText(dateTimeArray[1].trim());
            edDetail.setText(currentFinance.getDetail());

            // Cập nhật số tiền theo định dạng
            mViewModel.setBalance(String.valueOf(currentFinance.getMoney()));
            return;
        }
        if (currentTransfer != null) {
            getSupportActionBar().setTitle("Chỉnh sửa");
            Log.e("MM", "Load transfer");

            String[] dateTimeArray = currentTransfer.getDateTime().split("-");
            tvDay.setText(dateTimeArray[0]);
            tvTime.setText(dateTimeArray[1]);
            edDetail.setText(currentTransfer.getDetail());

            // Cập nhật số tiền theo định dạng
            mViewModel.setBalance(String.valueOf(currentTransfer.getMoney()));
            mViewModel.setTransferFee(String.valueOf(currentTransfer.getFee()));
        }

    }

    private void transfer() {
        Account accountOut = mViewModel.account.get();
        Account accountIn = mViewModel.accountIn.get();

        if (accountIn != null && accountOut != null) {
            if (accountIn.getAccountId() == accountOut.getAccountId()) {
                Toast.makeText(this, "Không thể chuyển khoản cho chính nó", Toast.LENGTH_SHORT).show();
            } else if (edCost.getText().toString().isEmpty()) {
                Toast.makeText(this, "Chưa nhập số tiền", Toast.LENGTH_SHORT).show();
            } else {
                String dateTime = tvDay.getText() + "-" + tvTime.getText();
                String detail = edDetail.getText().toString().trim();
                long cost = Long.parseLong(Helper.clearDotInText(edCost.getText().toString()));
                long fee = 0;

                if (!edTransferFee.getText().toString().isEmpty()) {
                    fee = Long.parseLong(Helper.clearDotInText(edTransferFee.getText().toString()));
                }

                if (currentTransfer == null) {
                    // Them moi transfer
                    accountOut.setBalance(accountOut.getBalance() - cost - fee);
                    accountIn.setBalance(accountIn.getBalance() + cost);

                    accountViewModel.update(accountOut);
                    accountViewModel.update(accountIn);

                    Transfer transfer = new Transfer(cost, fee, dateTime, detail, accountOut.getAccountId(), accountIn.getAccountId());
                    transferViewModel.insert(transfer);
                } else {
                    // Update transfer
                    Account oldAccountOut = getAccount(currentTransfer.getAccountOutId());
                    Account oldAccountIn = getAccount(currentTransfer.getAccountInId());

                    // Khoi phuc lai so tien giao dich cho 2 tai khoan
                    oldAccountOut.setBalance(oldAccountOut.getBalance() + currentTransfer.getMoney() + currentTransfer.getFee());
                    oldAccountIn.setBalance(oldAccountIn.getBalance() - currentTransfer.getMoney());

                    accountOut.setBalance(accountOut.getBalance() - cost - fee);
                    accountIn.setBalance(accountIn.getBalance() + cost);

                    accountViewModel.update(oldAccountOut);
                    accountViewModel.update(oldAccountIn);

                    Transfer transfer = new Transfer(cost, fee, dateTime, detail, accountOut.getAccountId(), accountIn.getAccountId());
                    transfer.setTransferId(currentTransfer.getTransferId());
                    transferViewModel.update(transfer);
                }


                finish();
            }

        } else {
            Toast.makeText(this, "Chưa chọn tài khoản xuất và nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void finance() {
        if (edCost.getText().toString().isEmpty()) {
            Toast.makeText(this, "Chưa nhập số tiền", Toast.LENGTH_SHORT).show();
        } else if (mViewModel.account.get() == null) {
            Toast.makeText(this, "Chưa chọn tài khoản", Toast.LENGTH_SHORT).show();
        } else {

            long cost = Long.parseLong(Helper.clearDotInText(edCost.getText().toString()));
            String dateTime = tvDay.getText() + " - " + tvTime.getText();
            String detail = edDetail.getText().toString().trim();
            int categoryId = mViewModel.category.get().getCategoryId();
            int accountId = mViewModel.account.get().getAccountId();

            Finance finance = new Finance(cost, dateTime, detail, categoryId, accountId);

            if (currentFinance != null) {
                // Chỉnh sửa finance có sẵn

                finance.setFinanceId(currentFinance.getFinanceId());
                financeViewModel.update(finance);

                // accountBefore là tài khoản sử dụng trong đợt chi tiêu trước đó, mà bây giờ muốn chỉnh sửa
                Account accountBefore = getAccount(currentFinance.getAccountId());
                assert (accountBefore != null);

                Category categoryBefore = getCategory(currentFinance.getCategoryId());
                if (categoryBefore.getType() == Helper.TYPE_INCOME) {
                    // Thu => Trừ đi số tiền đã thu
                    accountBefore.setBalance(accountBefore.getBalance() - currentFinance.getMoney());
                } else if (categoryBefore.getType() == Helper.TYPE_EXPENSE) {
                    // Chi => Cộng (hồi phục) lại số tiền đã chi tiêu
                    accountBefore.setBalance(accountBefore.getBalance() + currentFinance.getMoney());
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
            if (categoryNow.getType() == Helper.TYPE_INCOME) {
                // Thu => Cộng vào số tiền thu nhập
                accountNow.setBalance(accountNow.getBalance() + finance.getMoney());
            } else if (categoryNow.getType() == Helper.TYPE_EXPENSE) {
                // Chi => Trừ đi số tiền đã chi tiêu
                accountNow.setBalance(accountNow.getBalance() - finance.getMoney());
            }

            // Cập nhật lại accountNow
            accountViewModel.update(accountNow);

            finish();
        }
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void selectCategory() {
        categoryViewModel.getCategories().observe(this, categories -> {
            allCategories = categories;

            int selectedCategoryId = 1;
            if (getIntent().hasExtra(Helper.EDIT_FINANCE)) {
                selectedCategoryId = getIntent().getBundleExtra(Helper.EDIT_FINANCE).getInt(Helper.CATEGORY_ID);
            } else if (getIntent().hasExtra(Helper.ADD_FINANCE)) {
                selectedCategoryId = getIntent().getBundleExtra(Helper.ADD_FINANCE).getInt(Helper.CATEGORY_ID);
            }

            System.out.println("Select category");
            mViewModel.category.set(getCategory(selectedCategoryId));

            updateCategoriesSelect();
        });

        tvCategory.setOnClickListener((v) -> showDialogSelectCategory());
    }

    /**
     * Cập nhật lại danh sách danh mục theo menu "Thu nhập" hoặc "Chi tiêu"
     */
    private void updateCategoriesSelect() {
        // Thay đổi tên danh mục khi mở thêm finance mới
        mCategories.clear();
        for (Category c : allCategories) {
            if (c.getType() == mViewModel.categoryType.get()) {
                mCategories.add(c);
            }
        }

        Category currentCategorySelected = mViewModel.category.get();

        if (currentCategorySelected != null && currentCategorySelected.getType() != mViewModel.categoryType.get()) {
            if (!mCategories.isEmpty()) {
                mViewModel.category.set(mCategories.get(0));
            }
        }
    }

    private Category getCategory(int categoryId) {
        for (Category c : allCategories) {
            if (c.getCategoryId() == categoryId) return c;
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
                return mCategories.get(position).getCategoryId();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(AddEditFinanceActivity.this).inflate(R.layout.list_image_text, null);
                    ImageView imageView = convertView.findViewById(R.id.image_view);
                    TextView tvName = convertView.findViewById(R.id.text_view);

                    Category category = mCategories.get(position);
                    tvName.setText(category.getName());
                    imageView.setImageResource(category.getType() == Helper.TYPE_EXPENSE ? Helper.iconsExpense[category.getIcon()] : Helper.iconsIncome[category.getIcon()]);
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
        accountViewModel.getAccounts().observe(this, accounts -> {
            allAccounts = accounts;

            if (getIntent().hasExtra(Helper.EDIT_FINANCE)) {
                int selectedAccountId = getIntent().getBundleExtra(Helper.EDIT_FINANCE).getInt(Helper.ACCOUNT_ID);
                mViewModel.account.set(getAccount(selectedAccountId));
            } else if (getIntent().hasExtra(Helper.ADD_FINANCE)) {
//                mViewModel.account.set(getAccount(selectedAccountId));
            } else if (getIntent().hasExtra(Helper.EDIT_TRANSFER)) {
                int selectedAccountOutId = getIntent().getBundleExtra(Helper.EDIT_TRANSFER).getInt(Helper.TRANSFER_ACCOUNT_OUT_ID);
                int selectedAccountInId = getIntent().getBundleExtra(Helper.EDIT_TRANSFER).getInt(Helper.TRANSFER_ACCOUNT_IN_ID);
                mViewModel.account.set(getAccount(selectedAccountOutId));
                mViewModel.accountIn.set(getAccount(selectedAccountInId));

                Log.e("MM", "select account: " + selectedAccountOutId + " - " + selectedAccountInId);

            } else if (getIntent().hasExtra(Helper.ADD_TRANSFER)) {
//                selectedAccountId = getIntent().getBundleExtra(Helper.ADD_TRANSFER).getInt(Helper.ACCOUNT_ID);
            }

        });

        tvAccount.setOnClickListener((v) -> showDialogSelectAccount(false));
        tvAccountIn.setOnClickListener((v) -> showDialogSelectAccount(true));
    }

    private Account getAccount(int accountId) {
        for (Account a : allAccounts) {
            if (a.getAccountId() == accountId) return a;
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
                return allAccounts.get(position).getAccountId();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(AddEditFinanceActivity.this).inflate(R.layout.list_image_text, null);
                    ImageView imageView = convertView.findViewById(R.id.image_view);
                    TextView tvName = convertView.findViewById(R.id.text_view);

                    Account account = allAccounts.get(position);

                    tvName.setText(account.getAccountName());
                    imageView.setImageResource(Helper.iconsAccount[account.getIcon()]);
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
                    if (Helper.compareDateNow(new int[]{year, month, dayOfMonth}) <= 0) {
                        tvDay.setText(String.format("%02d/%02d/%02d", dayOfMonth, month + 1, year));
                    } else {
                        Toast.makeText(this, "Không thể chọn ngày trong tương lai!", Toast.LENGTH_SHORT).show();
                    }
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
                if (getIntent().hasExtra(Helper.EDIT_TRANSFER)) break;

                if (mViewModel.categoryType.get() != Helper.TYPE_INCOME) {
                    mViewModel.categoryType.set(Helper.TYPE_INCOME);
                    updateCategoriesSelect();
                }

                break;
            case R.id.sw_finance_expense:
                if (getIntent().hasExtra(Helper.EDIT_TRANSFER)) break;

                if (mViewModel.categoryType.get() != Helper.TYPE_EXPENSE) {
                    mViewModel.categoryType.set(Helper.TYPE_EXPENSE);
                    updateCategoriesSelect();
                }

                break;
            case R.id.sw_finance_transfer:
                if (getIntent().hasExtra(Helper.EDIT_FINANCE)) break;

                if (mViewModel.categoryType.get() != Helper.TYPE_TRANSFER) {
                    mViewModel.categoryType.set(Helper.TYPE_TRANSFER);
                }

                break;
        }


    }
}