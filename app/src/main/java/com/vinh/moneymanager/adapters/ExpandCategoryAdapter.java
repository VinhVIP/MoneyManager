package com.vinh.moneymanager.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.listeners.OnItemFinanceListener;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;
import java.util.Map;

public class ExpandCategoryAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<Category> categories;
    private Map<Category, List<Finance>> mapFinance;
    private List<Account> accounts;

    private final OnItemFinanceListener listener;


    public ExpandCategoryAdapter(Context context, List<Category> categories, Map<Category, List<Finance>> mapFinance, OnItemFinanceListener listener) {
        this.context = context;
        this.categories = categories;
        this.mapFinance = mapFinance;
        this.listener = listener;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    public void setMapFinance(Map<Category, List<Finance>> mapFinance) {
        this.mapFinance = mapFinance;
        categories.clear();
        categories.addAll(mapFinance.keySet());
        notifyDataSetInvalidated();
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
        return categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mapFinance.get(categories.get(groupPosition)).size();
    }

    @Override
    public Category getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Finance getChild(int groupPosition, int childPosition) {
        return mapFinance.get(categories.get(groupPosition)).get(childPosition);
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
            holder = new GroupHolder(convertView);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        Category category = categories.get(groupPosition);

        long totalCost = 0;
        for (Finance f : mapFinance.get(categories.get(groupPosition))) {
            totalCost += f.getMoney();
        }

        holder.bindData(category.getType() == Helper.TYPE_EXPENSE ?
                        Helper.iconsExpense[category.getIcon()] :
                        Helper.iconsIncome[category.getIcon()],
                category.getName(),
                totalCost,
                isExpanded);

        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ChildHolder(convertView);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        Finance finance = getChild(groupPosition, childPosition);
        holder.bindData(finance);

        holder.view.setOnClickListener(v -> listener.onFinanceClick(finance, getGroup(groupPosition)));

        convertView.setTag(holder);
        return convertView;
    }

    private class GroupHolder {
        ImageView imgView, imgArrow;
        TextView tvTitle, tvTotal;

        public GroupHolder(View view) {
            imgView = view.findViewById(R.id.image_view_list_group);
            imgArrow = view.findViewById(R.id.img_arrow);
            tvTitle = view.findViewById(R.id.text_view_title);
            tvTotal = view.findViewById(R.id.text_view_total);
        }

        public void bindData(int imgRes, String title, long total, boolean isExpanded) {
            imgView.setImageResource(imgRes);
            tvTitle.setText(title);
            tvTotal.setText(Helper.formatCurrency(total));
            imgArrow.setImageResource(isExpanded ? R.drawable.ic_expand_arrow : R.drawable.ic_collapse_arrow);
        }
    }

    private class ChildHolder {
        View view;
        TextView tvDay, tvMonthYear, tvDayOfWeek, tvDetail, tvCost, tvAccountName;

        public ChildHolder(View view) {
            this.view = view;

            tvDay = view.findViewById(R.id.tv_calendar_day);
            tvMonthYear = view.findViewById(R.id.tv_calendar_month_year);
            tvDayOfWeek = view.findViewById(R.id.tv_calendar_day_of_week);
            tvDetail = view.findViewById(R.id.text_view_transfer_detail);
            tvCost = view.findViewById(R.id.text_view_item_price);
            tvAccountName = view.findViewById(R.id.text_view_item_account);
        }

        public void bindData(Finance finance) {
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

            tvDetail.setText(finance.getDetail());

            long cost = finance.getMoney();

            tvCost.setText(Helper.formatCurrency(cost));
            tvAccountName.setText(getAccountName(finance.getAccountId()));
        }
    }

}
