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
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.listeners.OnItemFinanceListener;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;

public class RecyclerSearchAdapter extends RecyclerView.Adapter<RecyclerSearchAdapter.Holder> {

    private final Context context;
    private List<Finance> finances;
    private final OnItemFinanceListener listener;

    public RecyclerSearchAdapter(Context context, List<Finance> finances, OnItemFinanceListener listener) {
        this.context = context;
        this.finances = finances;
        this.listener = listener;
    }

    public void setFinances(List<Finance> finances) {
        this.finances = finances;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Finance finance = finances.get(position);
        holder.bindData(finance);
    }

    @Override
    public int getItemCount() {
        return finances.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvDay, tvMonthYear, tvDayOfWeek, tvDetail, tvCost, tvAccountName;

        public Holder(@NonNull View view) {
            super(view);
            tvDay = view.findViewById(R.id.tv_calendar_day);
            tvMonthYear = view.findViewById(R.id.tv_calendar_month_year);
            tvDayOfWeek = view.findViewById(R.id.tv_calendar_day_of_week);
            tvDetail = view.findViewById(R.id.text_view_item_detail);
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
            tvAccountName.setText("Vinh");
//            tvAccountName.setText(getAccountName(finance.getAccountId()));
        }
    }
}
