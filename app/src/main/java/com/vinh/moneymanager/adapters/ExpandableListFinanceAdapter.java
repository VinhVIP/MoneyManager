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
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpandableListFinanceAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Category> categories;
    private Map<Category, List<Finance>> mapFinance;

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    public ExpandableListFinanceAdapter(Context context, List<Category> categories, Map<Category, List<Finance>> mapFinance) {
        this.context = context;
        this.categories = categories;
        this.mapFinance = mapFinance;
    }

    public void setMapFinance(Map<Category, List<Finance>> mapFinance) {
        this.mapFinance = mapFinance;
        categories.clear();
        categories.addAll(mapFinance.keySet());
        notifyDataSetInvalidated();
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
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        ImageView imgView = convertView.findViewById(R.id.image_view_list_group);
        TextView tvTitle = convertView.findViewById(R.id.text_view_title);
        TextView tvTotal = convertView.findViewById(R.id.text_view_total);

        imgView.setImageResource(R.drawable.ic_star);
        tvTitle.setText(categories.get(groupPosition).getName());

        long totalCost = 0;
        for (Finance f : mapFinance.get(categories.get(groupPosition))) {
            totalCost += f.getCost();
        }

        tvTotal.setText(Helper.formatCurrency(totalCost));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        TextView tvTime = convertView.findViewById(R.id.text_view_item_time);
        TextView tvDetail = convertView.findViewById(R.id.text_view_item_detail);
        TextView tvCost = convertView.findViewById(R.id.text_view_item_price);

        tvTime.setText(mapFinance.get(categories.get(groupPosition)).get(childPosition).getDateTime());
        tvDetail.setText(mapFinance.get(categories.get(groupPosition)).get(childPosition).getDetail());

        long cost = mapFinance.get(categories.get(groupPosition)).get(childPosition).getCost();

        tvCost.setText(Helper.formatCurrency(cost));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
