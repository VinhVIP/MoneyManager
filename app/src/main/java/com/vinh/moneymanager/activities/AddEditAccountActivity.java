package com.vinh.moneymanager.activities;

import android.app.AlertDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.databinding.ActivityAddEditAccountBinding;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.room.entities.Transfer;
import com.vinh.moneymanager.viewmodels.AccountViewModel;
import com.vinh.moneymanager.viewmodels.AddEditAccountViewModel;
import com.vinh.moneymanager.viewmodels.FinanceViewModel;
import com.vinh.moneymanager.viewmodels.TransferViewModel;

import java.util.List;

import static com.vinh.moneymanager.libs.Helper.iconsAccount;

public class AddEditAccountActivity extends AppCompatActivity {

    private AddEditAccountViewModel mViewModel;
    private AccountViewModel accountViewModel;
    private FinanceViewModel financeViewModel;
    private TransferViewModel transferViewModel;

    private HandlerClick handler;

    private List<Account> allAccounts;
    private List<Finance> allFinances;
    private List<Transfer> allTransfers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddEditAccountBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_account);

        setupToolbar();

        mViewModel = new AddEditAccountViewModel();

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);
        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);

        accountViewModel.getAccounts().observe(this, accounts -> allAccounts = accounts);
        financeViewModel.getAllFinances().observe(this, finances -> allFinances = finances);
        transferViewModel.getTransfers().observe(this, transfers -> allTransfers = transfers);

        // Get data from intent
        getData();

        // Binding dữ liệu đến layout để hiển thị
        binding.setViewModel(mViewModel);

        // Cài đặt click
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

    /**
     * Thực hiện hành động thêm/chỉnh sửa tài khoản
     */
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
            finish();   // Đóng activity
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.finance_activity_menu, menu);

        // ẩn nút delete khi
        if (!getIntent().hasExtra(Helper.EDIT_ACCOUNT)) {
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
                deleteAccount();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Hiển thị dialog xác nhận xóa tài khoản
     */
    private void deleteAccount() {
        Account account = mViewModel.getAccount();
        if (canDelete(account.getAccountId())) {

            // Tạo 1 dialog xác nhận
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có xác định muốn xóa tài khoản này?")
                    .setPositiveButton("XÓA", (dialog, which) -> {
                        accountViewModel.delete(account);
                        dialog.cancel();
                        Log.d("MMM", "Account Deleted");
                        finish();
                    }).setNegativeButton("HỦY", (dialog, which) -> {
                dialog.cancel();
            }).show();

        } else {
            Toast.makeText(this, "Tài khoản đã phát sinh giao dịch nên không thể xóa!", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Kiểm tra xem tài khoản hiện tại có thể xóa hay không
     *
     * @param id id tài khoản muốn kiểm tra
     * @return có thể xóa hay không
     */
    private boolean canDelete(int id) {
        for (Finance f : allFinances) {
            // Nếu tài khoản đã có phát sinh giao dịch thu/chi => Không thể xóa
            if (f.getAccountId() == id) return false;
        }
        for (Transfer t : allTransfers) {
            // Nếu tài khoản đã có phát sinh chuyển khoản => Không thể xóa
            if (t.getAccountInId() == id || t.getAccountOutId() == id) return false;
        }
        return true;
    }

    /**
     * Lấy data từ Intent được gửi từ MainActivity
     * Bao gồm các thông tin cần thiết của 1 tài khoản
     * Nhằm hiển thị dữ liệu lên màn hình
     */
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

    /**
     * Kiểm tra tên tài khoản có hợp lệ hay không?
     *
     * @return
     */
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
     * @return
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


    /**
     * Lớp cài đặt sự kiện click, bao gồm:
     * Hiển thị dialog chọn icon tài khoản
     * Xác nhận thêm/sửa tài khoản
     */
    public class HandlerClick {
        private final Dialog dialog;

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