package com.vinh.moneymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;

public class RecyclerFinanceAdapter extends RecyclerView.Adapter {

    private static final int TYPE_GROUP = 1;
    public static final int TYPE_CHILD = 2;

    private Context context;
    private List list;

    public RecyclerFinanceAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;

        switch (viewType) {
            case TYPE_GROUP:
                view = inflater.inflate(R.layout.list_group, parent, false);
                return new GroupHolder(view);
            case TYPE_CHILD:
                view = inflater.inflate(R.layout.list_item, parent, false);
                return new ChildHolder(view);
        }

        return new GroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TYPE_GROUP:
                Category category = (Category) list.get(position);
                GroupHolder groupHolder = (GroupHolder) holder;

                groupHolder.imageView.setImageResource(R.drawable.ic_star);
                groupHolder.tvTitle.setText(category.getName());
                groupHolder.tvTotal.setText("100.000đ");
                break;
            case TYPE_CHILD:
                Finance finance = (Finance) list.get(position);
                ChildHolder childHolder = (ChildHolder) holder;

                childHolder.tvTime.setText(finance.getDateTime());
                childHolder.tvDetail.setText(finance.getDetail());
                childHolder.tvCost.setText(finance.getCost()+"đ");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof Category) return TYPE_GROUP;
        else if (list.get(position) instanceof Finance) return TYPE_CHILD;

        return super.getItemViewType(position);
    }

    private class GroupHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvTitle, tvTotal;

        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_list_group);
            tvTitle = itemView.findViewById(R.id.text_view_title);
            tvTotal=itemView.findViewById(R.id.text_view_total);
        }
    }

    private class ChildHolder extends RecyclerView.ViewHolder {

        TextView tvTime, tvDetail, tvCost;

        public ChildHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.text_view_item_time);
            tvDetail = itemView.findViewById(R.id.text_view_item_detail);
            tvCost = itemView.findViewById(R.id.text_view_item_price);
        }
    }
}
