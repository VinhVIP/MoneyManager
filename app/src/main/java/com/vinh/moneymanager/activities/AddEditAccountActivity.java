package com.vinh.moneymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.viewmodels.AccountViewModel;

public class AddEditAccountActivity extends AppCompatActivity {

    private EditText edAccountName, edAccountBalance, edAccountDescription;

    private Button btnSubmit;

    private AccountViewModel viewModel;

    private int accountId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);

        viewModel = new AccountViewModel(this.getApplication());

        edAccountName = findViewById(R.id.ed_account_name);
        edAccountBalance = findViewById(R.id.ed_account_balance);
        edAccountDescription = findViewById(R.id.ed_account_description);

        btnSubmit = findViewById(R.id.btn_submit_account);
        btnSubmit.setOnClickListener(v -> {

            if (checkAccountName()) {
                Account account = new Account(edAccountName.getText().toString().trim(),
                        getBalanceInput(),
                        edAccountDescription.getText().toString().trim());

                // Thêm account mới
                if (accountId == -1) {
                    viewModel.insert(account);
                    System.out.println("Thêm account thành công!");
                    finish();
                } else {
                    // Update account
                    account.setId(accountId);
                    viewModel.update(account);
                    System.out.println("Update account thành công!");
                    finish();
                }
            }
        });

        // Get data from intent
        getData();

    }

    private void getData() {
        Intent data = getIntent();
        accountId = -1;

        if (data != null && data.hasExtra("account_id")) {
            accountId = data.getIntExtra("account_id", -1);
            edAccountName.setText(data.getStringExtra("account_name"));
            edAccountBalance.setText(String.valueOf(data.getLongExtra("account_balance", 0)));
            edAccountDescription.setText(data.getStringExtra("account_description"));

            getSupportActionBar().setTitle("Chỉnh sửa tài khoản");
        }else{
            getSupportActionBar().setTitle("Thêm tài khoản");
        }
    }

    private boolean checkAccountName() {
        String name = edAccountName.getText().toString();
        if (name == null || name.isEmpty()) {
            Toast.makeText(this, "Tên tài khoản không được bỏ trống!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private long getBalanceInput() {
        String str = edAccountBalance.getText().toString();
        if (str == null || str.isEmpty()) return 0;
        else return Long.parseLong(str);
    }

}