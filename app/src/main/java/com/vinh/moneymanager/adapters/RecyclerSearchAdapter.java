package com.vinh.moneymanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.listeners.OnItemSearchListener;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.room.entities.Transfer;

import java.util.ArrayList;
import java.util.List;

public class RecyclerSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList items;
    private OnItemSearchListener listener;

    private List<Category> categories;
    private List<Account> accounts;

    private String keyword;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public static final int TYPE_FINANCE = 1;
    public static final int TYPE_TRANSFER = 2;


    public RecyclerSearchAdapter(Context context, ArrayList items, OnItemSearchListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    public void setData(ArrayList items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        ViewHolder holder;

        if (viewType == TYPE_FINANCE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_finance, parent, false);
            holder = new FinanceHolder(view);
        } else {
            // TYPE TRANSFER
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_transfer, parent, false);
            holder = new TransferHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FINANCE) {
            FinanceHolder financeHolder = (FinanceHolder) holder;

            Finance finance = (Finance) items.get(position);
            Category category = getCategory(finance.getCategoryId());

            financeHolder.bindData(finance, category);
            financeHolder.itemView.setOnClickListener(v -> {
                listener.onFinanceClick(finance, category);
                Log.d("MMM", "finance search click");
            });
        } else {
            TransferHolder transferHolder = (TransferHolder) holder;

            Transfer transfer = (Transfer) items.get(position);

            transferHolder.bindData(transfer);
            transferHolder.itemView.setOnClickListener(v -> {
                listener.onTransferClick(transfer);
                Log.d("MMM", "transfer search click");
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Finance) return TYPE_FINANCE;
        return TYPE_TRANSFER;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    private Category getCategory(int categoryId) {
        for (Category category : categories) {
            if (category.getCategoryId() == categoryId) return category;
        }
        return null;
    }

    private Account getAccount(int accountId) {
        for (Account account : accounts) {
            if (account.getAccountId() == accountId) return account;
        }
        return null;
    }

    public class FinanceHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvMonthYear, tvDayOfWeek, tvDetail, tvCost, tvAccountName, tvCategoryName;

        public FinanceHolder(@NonNull View view) {
            super(view);
            tvDay = view.findViewById(R.id.tv_calendar_day);
            tvMonthYear = view.findViewById(R.id.tv_calendar_month_year);
            tvDayOfWeek = view.findViewById(R.id.tv_calendar_day_of_week);
            tvDetail = view.findViewById(R.id.text_view_transfer_detail);
            tvCost = view.findViewById(R.id.text_view_item_price);
            tvAccountName = view.findViewById(R.id.text_view_account_in);
            tvCategoryName = view.findViewById(R.id.text_view_account_out);
        }

        public void bindData(Finance finance, Category category) {
            String str = finance.getDateTime();
            String[] date = str.split("-")[0].split("/");

            tvDay.setText(date[0].trim());
            tvMonthYear.setText(String.format("%s.%s", date[1].trim(), date[2].trim()));
            tvDayOfWeek.setText(Helper.getDayOfWeek(new DateRange.Date(Integer.parseInt(date[0].trim()), Integer.parseInt(date[1].trim()), Integer.parseInt(date[2].trim()))));
            if (tvDayOfWeek.getText().toString().equalsIgnoreCase("CN")) {
                tvDayOfWeek.setBackgroundResource(R.color.colorSunday);
            } else {
                tvDayOfWeek.setBackgroundResource(R.color.colorDayOfWeek);
            }

            String detail = finance.getDetail();
            SpannableString span = new SpannableString(detail);
            if (keyword != null && !keyword.isEmpty()) {
                int startIndex = detail.indexOf(keyword);
                span.setSpan(new BackgroundColorSpan(Color.GREEN), startIndex, startIndex + keyword.length(), 0);
            }
            tvDetail.setText(span);

            long cost = finance.getMoney();

            tvCost.setText(Helper.formatCurrency(cost));

            tvAccountName.setText(getAccount(finance.getAccountId()).getAccountName());
            tvCategoryName.setText(category.getName());
        }
    }

    public class TransferHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvMonthYear, tvDayOfWeek, tvDetail, tvMoney, tvFee, tvAccountOut, tvAccountIn;

        public TransferHolder(@NonNull View view) {
            super(view);
            tvDay = view.findViewById(R.id.tv_calendar_day);
            tvMonthYear = view.findViewById(R.id.tv_calendar_month_year);
            tvDayOfWeek = view.findViewById(R.id.tv_calendar_day_of_week);
            tvDetail = view.findViewById(R.id.text_view_transfer_detail);
            tvMoney = view.findViewById(R.id.text_view_money);
            tvFee = view.findViewById(R.id.text_view_fee);
            tvAccountOut = view.findViewById(R.id.text_view_account_out);
            tvAccountIn = view.findViewById(R.id.text_view_account_in);
        }

        public void bindData(Transfer transfer) {
            String str = transfer.getDateTime();
            String[] date = str.split("-")[0].split("/");

            tvDay.setText(date[0].trim());
            tvMonthYear.setText(String.format("%s.%s", date[1].trim(), date[2].trim()));
            tvDayOfWeek.setText(Helper.getDayOfWeek(new DateRange.Date(Integer.parseInt(date[0].trim()), Integer.parseInt(date[1].trim()), Integer.parseInt(date[2].trim()))));
            if (tvDayOfWeek.getText().toString().equalsIgnoreCase("CN")) {
                tvDayOfWeek.setBackgroundResource(R.color.colorSunday);
            } else {
                tvDayOfWeek.setBackgroundResource(R.color.colorDayOfWeek);
            }

            String detail = transfer.getDetail();
            SpannableString span = new SpannableString(detail);
            if (keyword != null && !keyword.isEmpty()) {
                int startIndex = detail.indexOf(keyword);
                span.setSpan(new BackgroundColorSpan(Color.GREEN), startIndex, startIndex + keyword.length(), 0);
            }
            tvDetail.setText(span);

            long cost = transfer.getMoney();
            long fee = transfer.getFee();

            tvMoney.setText(Helper.formatCurrency(cost));
            tvFee.setText(Helper.formatCurrency(fee));

            tvAccountOut.setText(getAccount(transfer.getAccountOutId()).getAccountName());
            tvAccountIn.setText(getAccount(transfer.getAccountInId()).getAccountName());
        }
    }
}
