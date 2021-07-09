package com.vinh.moneymanager.viewmodels;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.vinh.moneymanager.BR;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;

public class AddEditAccountViewModel extends BaseObservable {

    private Account account;

    private String balance;

    private String buttonText;

    public AddEditAccountViewModel() {
        account = new Account("", 0, "", 0);
        account.setAccountId(0);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Bindable
    public String getBalance() {
        return Helper.formatCurrencyWithoutSymbol(account.getBalance());
    }

    /**
     * Thêm các dấu chấm vào giá trị tiền tệ cho dễ nhìn
     *
     * @param balance
     */
    public void setBalance(String balance) {
        if (!balance.equals(this.balance)) {
            this.balance = Helper.formatCurrencyWithoutSymbol(balance);
            if (this.balance.length() > 0)
                account.setBalance(Long.parseLong(Helper.clearDotInText(this.balance)));
            else
                account.setBalance(0);
            notifyPropertyChanged(BR.balance);
        }
    }

    public String getAccountName() {
        return Helper.validName(account.getAccountName());
    }

    public void setAccountName(String accountName) {
        account.setAccountName(Helper.validName(accountName));
    }

    public String getDescription() {
        return account.getDescription();
    }

    public void setDescription(String description) {
        account.setDescription(description);
    }

    @Bindable
    public int getIcon() {
        return account.getIcon();
    }

    public void setIcon(int icon) {
        account.setIcon(icon);
        notifyPropertyChanged(BR.icon);
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
}
