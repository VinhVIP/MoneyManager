package com.vinh.moneymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.libs.Store;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GridCategoryAdapter extends BaseAdapter {

    private List<Category> categories;
    private Map<Category, List<Finance>> mapFinance;

    private LayoutInflater inflater;
    private Context context;
    private OnGridItemClickListener listener;

    public GridCategoryAdapter(Context context, Map<Category, List<Finance>> mapFinance, OnGridItemClickListener listener) {
        categories = new ArrayList<>();
        setMapFinance(mapFinance);
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    public void setMapFinance(Map<Category, List<Finance>> mapFinance) {
        this.mapFinance = mapFinance;
        categories.clear();
        categories.addAll(mapFinance.keySet());
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return categories.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.grid_category_item, null);
            holder = new GridViewHolder();
            holder.categoryImage = convertView.findViewById(R.id.image_view_category);
            holder.categoryName = convertView.findViewById(R.id.text_view_category_name);
            holder.categoryTotal = convertView.findViewById(R.id.text_view_category_total);

            convertView.setTag(holder);
        }else{
            holder = (GridViewHolder) convertView.getTag();
        }

        if(position != getCount()-1) {
            Category category = categories.get(position);
            holder.categoryName.setText(category.getName());
            // TODO: set image category
            holder.categoryImage.setImageResource(R.drawable.ic_star);
            long totalCost = 0;
            for(Finance f: mapFinance.get(categories.get(position))){
                totalCost += f.getCost();
            }
            holder.categoryTotal.setText(Helper.formatCurrency(totalCost));
        }else{
            holder.categoryName.setText("Thêm");
            holder.categoryImage.setImageResource(R.drawable.ic_add_circle_outline);
            holder.categoryTotal.setText("");
        }
        convertView.setOnClickListener((v)->{
            listener.onGridItemClick(position, position != getCount()-1);
        });

        return convertView;
    }

    private class GridViewHolder{
        ImageView categoryImage;
        TextView categoryName;
        TextView categoryTotal;
    }

    public interface OnGridItemClickListener{
        void onGridItemClick(int position, boolean isCategory);
    }
}
