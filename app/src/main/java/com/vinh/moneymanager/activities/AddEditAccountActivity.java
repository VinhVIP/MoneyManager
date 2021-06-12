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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.databinding.ActivityAddEditAccountBinding;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.viewmodels.AccountViewModel;
import com.vinh.moneymanager.viewmodels.AddEditAccountViewModel;

import java.util.List;

import static com.vinh.moneymanager.libs.Helper.iconsAccount;

public class AddEditAccountActivity extends AppCompatActivity {

    private AddEditAccountViewModel mViewModel;
    private AccountViewModel accountViewModel;

    private HandlerClick handler;

    private List<Account> allAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddEditAccountBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_account);

        setupToolbar();

        mViewModel = new AddEditAccountViewModel();
        accountViewModel = new AccountViewModel(getApplication());
        accountViewModel.getAccounts().observe(this, accounts -> allAccounts = accounts);

        // Get data from intent
        getData();

        binding.setViewModel(mViewModel);
        handler = new HandlerClick();
        binding.setHandler(handler);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.TitleFont);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
    }

    private void submit() {
        if (isAccountNameValid()) {
            Account account = mViewModel.getAccount();

            if (account.getAccountId() == 0) {
                // Thêm account mới
                accountViewModel.insert(account);
                System.out.println("Thêm account thành công!");
            } else {
                // Update account
                accountViewModel.update(account);
                System.out.println("Update account thành công!");
            }
            finish();
        }
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

            int accountId = data.getInt(Helper.ACCOUNT_ID, 0);
            String accountName = data.getString(Helper.ACCOUNT_NAME);
            long balance = data.getLong(Helper.ACCOUNT_BALANCE, 0);
            String desc = data.getString(Helper.ACCOUNT_DESCRIPTION);
            int iconIndex = data.getInt(Helper.ACCOUNT_ICON, 0);

            Account account = new Account(accountName, balance, desc, iconIndex);
            account.setAccountId(accountId);

            mViewModel.setAccount(account);

            getSupportActionBar().setTitle("Chỉnh sửa tài khoản");
            mViewModel.setButtonText("Chỉnh sửa");
        } else {
            getSupportActionBar().setTitle("Thêm tài khoản");
            mViewModel.setButtonText("Thêm");
        }
    }

    private boolean isAccountNameValid() {
        String name = mViewModel.getAccountName();
        if (name == null || name.isEmpty()) {
            Toast.makeText(this, "Tên tài khoản không được bỏ trống!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (isAccountNameExists(name, mViewModel.getAccount().getAccountId())) {
            Toast.makeText(this, "Tên tài khoản bị trùng!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Kiểm tra tên tài khoản đã tồn tại hay chưa
     *
     * @param accountName Tên tài khoản
     * @param ignoreId    ID tài khoản bỏ qua khi kiểm tra (bỏ qua trường hợp tự so sánh với chính nó)
     */
    private boolean isAccountNameExists(String accountName, int ignoreId) {
        if (allAccounts == null) return false;

        for (Account a : allAccounts) {
            if (a.getAccountId() != ignoreId && a.getAccountName().equals(accountName)) {
                return true;
            }
        }

        return false;
    }


    public class HandlerClick {
        private Dialog dialog;

        public HandlerClick() {
            dialog = new Dialog(AddEditAccountActivity.this);
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
                mViewModel.setIcon(position);
                dialog.cancel();
            });
        }

        public void submitAccount() {
            submit();
        }

        public void showDialogSelectIcon() {
            dialog.show();
        }
    }

}