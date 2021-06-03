package com.vinh.moneymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.listeners.OnItemFinanceListener;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;
import java.util.Map;

public class ExpandTimeAdapter extends BaseExpandableListAdapter {

    private Map<String, List<Finance>> mapFinance;
    private List<String> dates;
    private List<Category> categories;
    private Context context;

    private OnItemFinanceListener listener;

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

        holder.bindData(getGroup(groupPosition), totalCost);

        convertView.setTag(holder);
        return convertView;
    }

    private class GroupHolder {
        private TextView tvDay, tvMonthYear, tvDayOfWeek, tvDetail, tvTotal;

        public GroupHolder(View view) {
            tvDay = view.findViewById(R.id.tv_calendar_day);
            tvMonthYear = view.findViewById(R.id.tv_calendar_month_year);
            tvDayOfWeek = view.findViewById(R.id.tv_calendar_day_of_week);
            tvDetail = view.findViewById(R.id.text_view_item_detail);
            tvTotal = view.findViewById(R.id.text_view_item_price);
        }

        public void bindData(String strDate, long total) {
            String[] date = strDate.split("-")[0].split("/");
            tvDay.setText(date[0].trim());
            tvMonthYear.setText(String.format("%s.%s", date[1].trim(), date[2].trim()));
            tvDayOfWeek.setText(Helper.getDayOfWeek(new DateRange.Date(Integer.parseInt(date[0].trim()), Integer.parseInt(date[1].trim()), Integer.parseInt(date[2].trim()))));

            if (tvDayOfWeek.getText().toString().equalsIgnoreCase("CN")) {
                tvDayOfWeek.setBackgroundResource(R.color.colorSunday);
            } else {
                tvDayOfWeek.setBackgroundResource(R.color.colorDayOfWeek);
            }

            tvDetail.setText("");
            tvTotal.setText(Helper.formatCurrency(total));
        }
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

        holder.view.setOnClickListener(v->listener.onFinanceClick(finance, getCategory(finance.getCategoryId())));

        convertView.setTag(holder);
        return convertView;
    }

    private class ChildHolder {
        View view;
        TextView tvTime, tvDetail, tvCategoryName, tvCost;

        public ChildHolder(View view) {
            this.view = view;

            tvTime = view.findViewById(R.id.text_view_time);
            tvDetail = view.findViewById(R.id.text_view_item_detail);
            tvCategoryName = view.findViewById(R.id.text_view_category_name);
            tvCost = view.findViewById(R.id.text_view_item_price);
        }

        public void bindData(Finance finance) {
            tvTime.setText(finance.getTime());
            tvDetail.setText(finance.getDetail());
            tvCategoryName.setText(getCategory(finance.getCategoryId()).getName());
            tvCost.setText(Helper.formatCurrency(finance.getMoney()));
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
