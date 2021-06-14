package com.vinh.moneymanager.viewmodels;

import android.widget.EditText;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.vinh.moneymanager.BR;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Category;

public class AddEditFinanceViewModel extends BaseObservable {

    public ObservableInt categoryType = new ObservableInt();

    public ObservableField<Account> account = new ObservableField<>();
    public ObservableField<Account> accountIn = new ObservableField<>();
    public ObservableField<Category> category = new ObservableField<>();

    private String balance;

    private String transferFee;

    public AddEditFinanceViewModel() {
    }

    /*
     * Thiết lập vị trí con trỏ (cursor) của EditText luôn ở vị trí cuối cùng sau khi nhập
     * Bởi bình thường khi dùng phương thức setText thì con trỏ sẽ tự động nhảy về đầu dòng
     */
    @BindingAdapter("android:text")
    public static void setText(EditText view, String oldText, String newText) {
        if (newText != null && newText.length() <= 15) {
            view.setText(newText);
            view.setSelection(newText.length());
        }
    }

    @Bindable
    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        if (!balance.equals(this.balance)) {
            this.balance = Helper.formatCurrencyWithoutSymbol(balance);
            notifyPropertyChanged(BR.balance);
        }
    }

    @Bindable
    public String getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(String transferFee) {
        if (!transferFee.equals(this.transferFee)) {
            this.transferFee = Helper.formatCurrencyWithoutSymbol(transferFee);
            notifyPropertyChanged(BR.transferFee);
        }
    }

}
