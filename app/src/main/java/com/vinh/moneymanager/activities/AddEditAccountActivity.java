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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.viewmodels.AccountViewModel;

import static com.vinh.moneymanager.libs.Helper.iconsAccount;
import static com.vinh.moneymanager.libs.Helper.iconsExpense;
import static com.vinh.moneymanager.libs.Helper.iconsIncome;

public class AddEditAccountActivity extends AppCompatActivity {

    private EditText edAccountName, edAccountBalance, edAccountDescription;
    private ImageView imgIcon;
    private Button btnSubmit;

    private AccountViewModel viewModel;

    private int accountId = 0;
    private int iconIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.TitleFont);


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
                        edAccountDescription.getText().toString().trim(), iconIndex);

                // Thêm account mới
                if (accountId == 0) {
                    viewModel.insert(account);
                    System.out.println("Thêm account thành công!");
                    finish();
                } else {
                    // Update account
                    account.setAccountId(accountId);
                    viewModel.update(account);
                    System.out.println("Update account thành công!");
                    finish();
                }
            }
        });

        imgIcon = findViewById(R.id.imgIcon);
        imgIcon.setOnClickListener(v -> showDialogSelectIcon());

        // Get data from intent
        getData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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

    private void getData() {
        if (getIntent().hasExtra(Helper.EDIT_ACCOUNT)) {
            Bundle data = getIntent().getBundleExtra(Helper.EDIT_ACCOUNT);
            accountId = data.getInt(Helper.ACCOUNT_ID, 0);
            edAccountName.setText(data.getString(Helper.ACCOUNT_NAME));
            edAccountBalance.setText(String.valueOf(data.getLong(Helper.ACCOUNT_BALANCE, 0)));
            edAccountDescription.setText(data.getString(Helper.ACCOUNT_DESCRIPTION));

            iconIndex = data.getInt(Helper.ACCOUNT_ICON);
            imgIcon.setImageResource(iconsAccount[iconIndex]);

            getSupportActionBar().setTitle("Chỉnh sửa tài khoản");
            btnSubmit.setText("Chỉnh sửa");
        } else {
            getSupportActionBar().setTitle("Thêm tài khoản");
            btnSubmit.setText("Thêm");
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

    private void showDialogSelectIcon() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_icon);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = dialog.findViewById(R.id.img_close_dialog);
        imgClose.setOnClickListener((v) -> dialog.cancel());

        GridView gridView = dialog.findViewById(R.id.grid_view_icon);
        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return iconsAccount.length;
            }

            @Override
            public Integer getItem(int position) {
                return iconsAccount[position];
            }

            @Override
            public long getItemId(int position) {
                return iconsAccount[position];
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(AddEditAccountActivity.this).inflate(R.layout.single_image, null);
                    ImageView imageView = convertView.findViewById(R.id.img_view_icon);
                    imageView.setImageResource(getItem(position));
                }
                return convertView;
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            iconIndex = position;
            imgIcon.setImageResource(iconsAccount[position]);
            dialog.cancel();
        });


        dialog.show();
    }


}