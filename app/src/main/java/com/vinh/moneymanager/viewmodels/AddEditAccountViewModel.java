package com.vinh.moneymanager.viewmodels;

import android.widget.EditText;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

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

//    @BindingAdapter("android:text")
//    public static void setText(EditText view, String oldText, String newText) {
//        if (newText != null && newText.length() > 15) return;
//        view.setText(newText);
//        if (newText != null) view.setSelection(newText.length());
//    }

    @Bindable
    public String getBalance() {
        return Helper.formatCurrencyWithoutSymbol(account.getBalance());
    }

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
        return account.getAccountName();
    }

    public void setAccountName(String accountName) {
        account.setAccountName(accountName);
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
