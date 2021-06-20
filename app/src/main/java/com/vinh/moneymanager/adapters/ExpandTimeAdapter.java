package com.vinh.moneymanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.listeners.OnItemFinanceListener;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;
import java.util.Map;

public class ExpandTimeAdapter extends BaseExpandableListAdapter {

    private Map<String, List<Finance>> mapFinance;
    private final List<String> dates;
    private List<Category> categories;
    private final Context context;
    private List<Account> accounts;

    private final OnItemFinanceListener listener;

    public ExpandTimeAdapter(Context context, List<String> dates, Map<String, List<Finance>> mapFinance, List<Category> categories, OnItemFinanceListener listener) {
        this.mapFinance = mapFinance;
        this.dates = dates;
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    public void setMapFinance(Map<String, List<Finance>> mapFinance) {
        this.mapFinance = mapFinance;
        dates.clear();
        dates.addAll(mapFinance.keySet());
        notifyDataSetInvalidated();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    private Category getCategory(int categoryId) {
        for (Category c : categories) {
            if (c.getCategoryId() == categoryId) return c;
        }
        return null;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    private String getAccountName(int accountId) {
        if (accounts == null || accounts.isEmpty()) return "";
        for (Account account : accounts) {
            if (account.getAccountId() == accountId) {
                return account.getAccountName();
            }
        }
        return "";
    }

    @Override
    public int getGroupCount() {
        return mapFinance.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mapFinance.get(getGroup(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return dates.get(groupPosition);
    }

    @Override
    public Finance getChild(int groupPosition, int childPosition) {
        return mapFinance.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new GroupHolder(convertView);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        long totalCost = 0;
        for (Finance f : mapFinance.get(getGroup(groupPosition))) {
            totalCost += f.getMoney();
        }

        holder.bindData(getGroup(groupPosition), totalCost, isExpanded);

        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_time, null);
            holder = new ChildHolder(convertView);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        Finance finance = getChild(groupPosition, childPosition);
        holder.bindData(finance);

        holder.view.setOnClickListener(v -> listener.onFinanceClick(finance, getCategory(finance.getCategoryId())));

        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GroupHolder {
        private final TextView tvDay;
        private final TextView tvMonthYear;
        private final TextView tvDayOfWeek;
        private final TextView tvDetail;
        private final TextView tvTotal;
        private final TextView tvAccountName;
        private final ImageView imgArrow;

        public GroupHolder(View view) {
            tvDay = view.findViewById(R.id.tv_calendar_day);
            tvMonthYear = view.findViewById(R.id.tv_calendar_month_year);
            tvDayOfWeek = view.findViewById(R.id.tv_calendar_day_of_week);
            tvDetail = view.findViewById(R.id.text_view_transfer_detail);
            tvTotal = view.findViewById(R.id.text_view_item_price);
            tvAccountName = view.findViewById(R.id.text_view_item_account);
            imgArrow = view.findViewById(R.id.img_arrow);
        }

        public void bindData(String strDate, long total, boolean isExpanded) {
            String[] date = strDate.split("-")[0].split("/");
            tvDay.setText(date[0].trim());
            tvMonthYear.setText(String.format("%s.%s", date[1].trim(), date[2].trim()));
            tvDayOfWeek.setText(Helper.getDayOfWeek(new DateRange.Date(Integer.parseInt(date[0].trim()), Integer.parseInt(date[1].trim()), Integer.parseInt(date[2].trim()))));

            if (tvDayOfWeek.getText().toString().equalsIgnoreCase("CN")) {
                tvDayOfWeek.setBackgroundResource(R.color.colorSunday);
            } else {
                tvDayOfWeek.setBackgroundResource(R.color.colorDayOfWeek);
            }

            tvAccountName.setText("");
            tvDetail.setText("");
            tvTotal.setTypeface(ResourcesCompat.getFont(context, R.font.oswald), Typeface.BOLD);
            tvTotal.setTextColor(Color.BLACK);
            tvTotal.setText(Helper.formatCurrency(total));

            imgArrow.setVisibility(View.VISIBLE);
            imgArrow.setImageResource(isExpanded ? R.drawable.ic_expand_arrow : R.drawable.ic_collapse_arrow);
        }
    }

    private class ChildHolder {
        View view;
        TextView tvTime, tvDetail, tvCategoryName, tvCost, tvAccountName;

        public ChildHolder(View view) {
            this.view = view;

            tvTime = view.findViewById(R.id.text_view_time);
            tvDetail = view.findViewById(R.id.text_view_transfer_detail);
            tvCategoryName = view.findViewById(R.id.text_view_account_out);
            tvCost = view.findViewById(R.id.text_view_item_price);
            tvCost = view.findViewById(R.id.text_view_item_price);
            tvAccountName = view.findViewById(R.id.text_view_account_in);
        }

        public void bindData(Finance finance) {
            tvTime.setText(finance.getTime());
            tvDetail.setText(finance.getDetail());
            tvCategoryName.setText(getCategory(finance.getCategoryId()).getName());
            tvCost.setText(Helper.formatCurrency(finance.getMoney()));
            tvAccountName.setText(getAccountName(finance.getAccountId()));
        }
    }
}
