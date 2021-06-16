package com.vinh.moneymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Account;

import java.util.List;

public class RecyclerAccountAdapter extends RecyclerView.Adapter<RecyclerAccountAdapter.ViewHolder> {

    private List<Account> accounts;
    private final OnItemAccountClickListener listener;

    public RecyclerAccountAdapter(List<Account> accounts, OnItemAccountClickListener listener) {
        this.accounts = accounts;
        this.listener = listener;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = accounts.get(position);

        holder.bindData(account);

        holder.itemView.setOnClickListener(v -> listener.onItemAccountClick(account));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemAccountClick(account);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public interface OnItemAccountClickListener {
        void onItemAccountClick(Account account);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAccount;
        TextView tvAccountName, tvAccountBalance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAccount = itemView.findViewById(R.id.img_account);
            tvAccountName = itemView.findViewById(R.id.tv_account_name);
            tvAccountBalance = itemView.findViewById(R.id.tv_asset);
        }

        public void bindData(Account account) {
            imageAccount.setImageResource(Helper.iconsAccount[account.getIcon()]);
            tvAccountName.setText(account.getAccountName());
            tvAccountBalance.setText(Helper.formatCurrency(account.getBalance()));
        }
    }
}
