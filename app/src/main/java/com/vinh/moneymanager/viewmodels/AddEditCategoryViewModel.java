package com.vinh.moneymanager.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Category;

public class AddEditCategoryViewModel extends BaseObservable {

    private Category category;

    private String buttonText;

    public AddEditCategoryViewModel() {
        category = new Category("", 0, "", 0);
        category.setCategoryId(0);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Bindable
    public int getType() {
        return category.getType();
    }

    public void setType(int type) {
        category.setType(type);
        notifyPropertyChanged(BR.type);
    }

    public String getName() {
        return Helper.validName(category.getName());
    }

    public void setName(String name) {
        category.setName(Helper.validName(name));
    }

    @Bindable
    public int getIcon() {
        return category.getIcon();
    }

    public void setIcon(int icon) {
        category.setIcon(icon);
        notifyPropertyChanged(BR.icon);
    }

    public String getDescription() {
        return category.getDescription();
    }

    public void setDescription(String description) {
        category.setDescription(description);
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
}
