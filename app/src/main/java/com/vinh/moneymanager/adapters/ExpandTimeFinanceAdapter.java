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
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;
import java.util.Map;

public class ExpandTimeFinanceAdapter extends BaseExpandableListAdapter {

    private Map<String, List<Finance>> mapFinance;
    private List<String> dates;
    private List<Category> categories;
    private Context context;

    public ExpandTimeFinanceAdapter(Context context, List<String> dates, Map<String, List<Finance>> mapFinance, List<Category> categories) {
        this.mapFinance = mapFinance;
        this.dates = dates;
        this.context = context;
        this.categories = categories;
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
            if (c.getId() == categoryId) return c;
        }
        return null;
    }

    @Override
    public int getGroupCount() {
        return mapFinance.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mapFinance.get(dates.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mapFinance.get(dates.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mapFinance.get(dates.get(groupPosition)).get(childPosition);
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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView tvDay = convertView.findViewById(R.id.tv_calendar_day);
        TextView tvMonthYear = convertView.findViewById(R.id.tv_calendar_month_year);
        TextView tvDayOfWeek = convertView.findViewById(R.id.tv_calendar_day_of_week);

        TextView tvDetail = convertView.findViewById(R.id.text_view_item_detail);
        TextView tvTotal = convertView.findViewById(R.id.text_view_item_price);

        String strDate = dates.get(groupPosition);

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

        long totalCost = 0;
        for (Finance f : mapFinance.get(strDate)) {
            totalCost += f.getCost();
        }
        tvTotal.setText(Helper.formatCurrency(totalCost));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_time, null);
        }

        TextView tvTime = convertView.findViewById(R.id.text_view_time);
        TextView tvDetail = convertView.findViewById(R.id.text_view_item_detail);
        TextView tvCategoryName = convertView.findViewById(R.id.text_view_category_name);
        TextView tvCost = convertView.findViewById(R.id.text_view_item_price);

        Finance finance = mapFinance.get(dates.get(groupPosition)).get(childPosition);

        tvTime.setText(finance.getTime());
        tvDetail.setText(finance.getDetail());
        tvCategoryName.setText(getCategory(finance.getCategoryId()).getName());
        tvCost.setText(Helper.formatCurrency(finance.getCost()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
