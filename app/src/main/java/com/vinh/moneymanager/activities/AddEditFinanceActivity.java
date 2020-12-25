package com.vinh.moneymanager.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import androidx.lifecycle.ViewModelProvider;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.viewmodels.AccountViewModel;
import com.vinh.moneymanager.viewmodels.CategoryViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEditFinanceActivity extends AppCompatActivity {

    private TextView tvCategory, tvDay, tvTime, tvAccount;
    private EditText edCost, edDetail;
    private Button btnSubmit;

    private CategoryViewModel categoryViewModel;
    private List<Category> mCategories = new ArrayList<>();

    private AccountViewModel accountViewModel;
    private List<Account> mAccounts = new ArrayList<>();


    private int selectedCategoryId, selectedAccountId;
    private int financeIdUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_add_edit_finance);

        tvCategory = findViewById(R.id.tv_category_name);
        tvDay = findViewById(R.id.tv_day_finance);
        tvTime = findViewById(R.id.tv_time_finance);
        tvAccount = findViewById(R.id.tv_account_finance);
        edCost = findViewById(R.id.ed_cost_finance);
        edDetail = findViewById(R.id.ed_note_finance);
        btnSubmit = findViewById(R.id.btn_submit_finance);

        tvDay.setOnClickListener((v) -> showDialogChooseDay(new DateRange.Date(tvDay.getText().toString())));
        tvTime.setOnClickListener((v) -> showDialogChooseTime(tvTime.getText().toString()));

        btnSubmit.setOnClickListener((v) -> {
            if (edCost.getText().toString().length() > 0) {
                Intent dataReturn = new Intent();
                dataReturn.putExtra(Helper.CATEGORY_ID, selectedCategoryId);
                dataReturn.putExtra(Helper.FINANCE_DATETIME, tvDay.getText() + " - " + tvTime.getText());
                dataReturn.putExtra(Helper.ACCOUNT_ID, selectedAccountId);
                dataReturn.putExtra(Helper.FINANCE_COST, Long.parseLong(edCost.getText().toString()));
                dataReturn.putExtra(Helper.FINANCE_DETAIL, edDetail.getText().toString().trim());
                dataReturn.putExtra(Helper.FINANCE_ID, financeIdUpdate);

                setResult(RESULT_OK, dataReturn);
                finish();
            } else {
                Toast.makeText(this, "Chưa nhập số tiền", Toast.LENGTH_SHORT).show();
            }
        });


        selectCategory();
        selectAccount();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getData();
    }

    private void getData() {
        Intent data = getIntent();
        Calendar calendar = Calendar.getInstance();

        selectedCategoryId = data.getIntExtra(Helper.CATEGORY_ID, 0);
        selectedAccountId = data.getIntExtra(Helper.ACCOUNT_ID, 0);
        financeIdUpdate = data.getIntExtra(Helper.FINANCE_ID, -1);

        tvCategory.setText(data.getStringExtra(Helper.CATEGORY_NAME));
        tvAccount.setText(data.getStringExtra(Helper.ACCOUNT_NAME));

        if (data.hasExtra(Helper.FINANCE_DATETIME)) {
            String[] datetime = data.getStringExtra(Helper.FINANCE_DATETIME).split("-");
            tvDay.setText(datetime[0].trim());
            tvTime.setText(datetime[1].trim());
            edCost.setText(String.valueOf(data.getLongExtra(Helper.FINANCE_COST, 0)));
            edDetail.setText(data.getStringExtra(Helper.FINANCE_DETAIL));

            getSupportActionBar().setTitle("Thêm khoản chi tiêu");
        } else {
            tvDay.setText(String.format("%02d/%02d/%02d", calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)));
            tvTime.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));

            getSupportActionBar().setTitle("Chỉnh sửa");
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
        categoryViewModel.getExpenseCategories().observe(this, categories -> mCategories = categories);

        tvCategory.setOnClickListener((v) -> showDialogSelectCategory());
    }

    private void showDialogSelectCategory() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_list);
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
            selectedCategoryId = mCategories.get(position).getId();
            tvCategory.setText(mCategories.get(position).getName());
            dialog.cancel();
        });
        dialog.show();
    }

    private void selectAccount() {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        accountViewModel.getAccounts().observe(this, accounts -> mAccounts = accounts);

        tvAccount.setOnClickListener((v) -> showDialogSelectAccount());
    }

    private void showDialogSelectAccount() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_list);
        ListView listView = dialog.findViewById(R.id.list_category_dialog);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mAccounts.size();
            }

            @Override
            public Object getItem(int position) {
                return mAccounts.get(position);
            }

            @Override
            public long getItemId(int position) {
                return mAccounts.get(position).getId();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(AddEditFinanceActivity.this).inflate(R.layout.list_image_text, null);
                    ImageView imageView = convertView.findViewById(R.id.image_view);
                    TextView tvName = convertView.findViewById(R.id.text_view);

                    tvName.setText(mAccounts.get(position).getAccountName());
                }
                return convertView;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedAccountId = mAccounts.get(position).getId();
            tvAccount.setText(mAccounts.get(position).getAccountName());
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
}