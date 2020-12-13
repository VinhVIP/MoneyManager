package com.vinh.moneymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.room.entities.Category;

import java.util.List;

public class GridCategoryAdapter extends BaseAdapter {

    private List<Category> categories;
    private LayoutInflater inflater;
    private Context context;
    private OnGridItemClickListener listener;

    public GridCategoryAdapter(Context context, List<Category> categories, OnGridItemClickListener listener) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();
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

        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryImage.setImageResource(R.drawable.ic_star);
        holder.categoryTotal.setText("100.000");

        convertView.setOnClickListener((v)->{
            listener.onGridItemClick(position);
        });

        return convertView;
    }

    private class GridViewHolder{
        ImageView categoryImage;
        TextView categoryName;
        TextView categoryTotal;
    }

    public interface OnGridItemClickListener{
        void onGridItemClick(int position);
    }
}
