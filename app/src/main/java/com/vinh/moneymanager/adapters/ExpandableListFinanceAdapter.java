package com.vinh.moneymanager.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.HashMap;
import java.util.List;

public class ExpandableListFinanceAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Category> categories;
    private HashMap<Category, List<Finance>> mapFinance;


    public ExpandableListFinanceAdapter(Context context, List<Category> categories, HashMap<Category, List<Finance>> mapFinance) {
        this.context = context;
        this.categories = categories;
        this.mapFinance = mapFinance;
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
        tvTotal.setText("100.000đ");

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
        tvCost.setText(mapFinance.get(categories.get(groupPosition)).get(childPosition).getCost()+"đ");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
