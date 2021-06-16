package com.vinh.moneymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.DateRange;

import java.util.List;

public class RecyclerWeekAdapter extends RecyclerView.Adapter<RecyclerWeekAdapter.ViewHolder> {

    private final List<DateRange> list;
    private final Context context;
    private final OnItemWeekClickListener listener;

    public RecyclerWeekAdapter(Context context, List<DateRange> list, OnItemWeekClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateRange dateRange = list.get(position);
        holder.tvWeek.setText("Tuần " + dateRange.getWeekOfYear());
        holder.tvDateRange.setText(dateRange.getWeekString());

        holder.itemView.setOnClickListener((v) -> {
            listener.onItemWeekClick(dateRange);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemWeekClickListener {
        void onItemWeekClick(DateRange dateWeek);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvWeek, tvDateRange;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeek = itemView.findViewById(R.id.tv_week);
            tvDateRange = itemView.findViewById(R.id.tv_date_range);
        }
    }
}
