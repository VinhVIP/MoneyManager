package com.vinh.moneymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Transfer;
import com.vinh.moneymanager.viewmodels.AccountViewModel;

import java.util.List;

public class RecyclerTransferAdapter extends RecyclerView.Adapter<RecyclerTransferAdapter.TransferHolder> {

    private List<Transfer> transfers;
    private final OnItemTransferListener listener;

    private final AccountViewModel accountViewModel;

    public RecyclerTransferAdapter(List<Transfer> transfers, OnItemTransferListener listener, AccountViewModel accountViewModel) {
        this.transfers = transfers;
        this.listener = listener;
        this.accountViewModel = accountViewModel;
    }

    public void setAdapter(List<Transfer> transfers) {
        this.transfers = transfers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transfer, parent, false);
        return new TransferHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransferHolder holder, int position) {
        Transfer transfer = transfers.get(position);
        holder.bindData(transfer);
        holder.itemView.setOnClickListener(v -> listener.onItemTransferClick(transfer, position));
        holder.imgDelete.setOnClickListener(v -> listener.onItemDelete(transfer, position));
    }

    @Override
    public int getItemCount() {
        return transfers.size();
    }

    public void deleteTransfer(int position) {
        transfers.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemTransferListener {
        void onItemTransferClick(Transfer transfer, int position);

        void onItemDelete(Transfer transfer, int position);
    }

    // ---------------- Holder --------------------
    class TransferHolder extends RecyclerView.ViewHolder {

        TextView tvAccountOut, tvAccountIn, tvMoney, tvFee;
        TextView tvDay, tvMonthYear, tvDayOfWeek;
        ImageView imgDelete;

        public TransferHolder(@NonNull View itemView) {
            super(itemView);

            tvAccountOut = itemView.findViewById(R.id.tv_account_out);
            tvAccountIn = itemView.findViewById(R.id.tv_account_in);
            tvMoney = itemView.findViewById(R.id.tv_money);
            tvFee = itemView.findViewById(R.id.tv_fee);

            tvDay = itemView.findViewById(R.id.tv_calendar_day);
            tvMonthYear = itemView.findViewById(R.id.tv_calendar_month_year);
            tvDayOfWeek = itemView.findViewById(R.id.tv_calendar_day_of_week);

            imgDelete = itemView.findViewById(R.id.img_delete_transfer);
        }

        public void bindData(Transfer transfer) {
            tvAccountOut.setText(accountViewModel.search(transfer.getAccountOutId()).getAccountName());
            tvAccountIn.setText(accountViewModel.search(transfer.getAccountInId()).getAccountName());
            tvMoney.setText(Helper.formatCurrency(transfer.getMoney()));
            tvFee.setText(Helper.formatCurrency(transfer.getFee()));

            String[] dates = transfer.getDateTime().split("-")[0].split("/");
            tvDay.setText(dates[0]);
            tvMonthYear.setText(String.format("%s.%s", dates[1], dates[2]));
            tvDayOfWeek.setText(Helper.getDayOfWeek(new DateRange.Date(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]))));
            if (tvDayOfWeek.getText().toString().equalsIgnoreCase("CN")) {
                tvDayOfWeek.setBackgroundResource(R.color.colorSunday);
            } else {
                tvDayOfWeek.setBackgroundResource(R.color.colorDayOfWeek);
            }
        }

    }

}
