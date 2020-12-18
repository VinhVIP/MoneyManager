package com.vinh.moneymanager.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.Define;
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
        setContentView(R.layout.activity_add_edit_finance);

        tvCategory = findViewById(R.id.tv_category_name);
        tvDay = findViewById(R.id.tv_day_finance);
        tvTime = findViewById(R.id.tv_time_finance);
        tvAccount = findViewById(R.id.tv_account_finance);
        edCost = findViewById(R.id.ed_cost_finance);
        edDetail = findViewById(R.id.ed_note_finance);
        btnSubmit = findViewById(R.id.btn_submit_finance);

        btnSubmit.setOnClickListener((v) -> {
            Intent dataReturn = new Intent();
            dataReturn.putExtra(Define.CATEGORY_ID, selectedCategoryId);
            dataReturn.putExtra(Define.FINANCE_DATETIME, tvDay.getText() + " - " + tvTime.getText());
            dataReturn.putExtra(Define.ACCOUNT_ID, selectedAccountId);
            dataReturn.putExtra(Define.FINANCE_COST, Long.parseLong(edCost.getText().toString()));
            dataReturn.putExtra(Define.FINANCE_DETAIL, edDetail.getText().toString().trim());
            dataReturn.putExtra(Define.FINANCE_ID, financeIdUpdate);

            setResult(RESULT_OK, dataReturn);
            finish();
        });


        selectCategory();
        selectAccount();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getData();
    }

    private void getData() {
        Intent data = getIntent();
        Calendar calendar = Calendar.getInstance();

        selectedCategoryId = data.getIntExtra(Define.CATEGORY_ID, 0);
        selectedAccountId = data.getIntExtra(Define.ACCOUNT_ID, 0);
        financeIdUpdate = data.getIntExtra(Define.FINANCE_ID, -1);

        tvCategory.setText(data.getStringExtra(Define.CATEGORY_NAME));
        tvAccount.setText(data.getStringExtra(Define.ACCOUNT_NAME));

        if (data.hasExtra(Define.FINANCE_DATETIME)) {
            String[] datetime = data.getStringExtra(Define.FINANCE_DATETIME).split("-");
            tvDay.setText(datetime[0].trim());
            tvTime.setText(datetime[1].trim());
            edCost.setText(String.valueOf(data.getLongExtra(Define.FINANCE_COST, 0)));
            edDetail.setText(data.getStringExtra(Define.FINANCE_DETAIL));
        } else {
            tvDay.setText(String.format("%02d/%02d/%02d", calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)));
            tvTime.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        }

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
}