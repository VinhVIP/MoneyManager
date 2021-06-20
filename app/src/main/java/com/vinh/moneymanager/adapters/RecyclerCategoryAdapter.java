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
import com.vinh.moneymanager.listeners.OnItemCategoryListener;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;
import java.util.Map;

public class RecyclerCategoryAdapter extends RecyclerView.Adapter<RecyclerCategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Map<Category, List<Finance>> mapFinance;
    private final OnItemCategoryListener listener;

    public RecyclerCategoryAdapter(List<Category> categories, Map<Category, List<Finance>> mapFinance, OnItemCategoryListener listener) {
        this.categories = categories;
        this.mapFinance = mapFinance;
        this.listener = listener;
    }

    public void setAdapter(List<Category> categories, Map<Category, List<Finance>> mapFinance) {
        this.categories = categories;
        this.mapFinance = mapFinance;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            Category category = categories.get(position);

            long totalCost = 0;
            List<Finance> financeList = mapFinance.get(categories.get(position));
            if (financeList != null) {
                for (Finance f : financeList) {
                    totalCost += f.getMoney();
                }
            }

            holder.bindData(category.getType() == Helper.TYPE_EXPENSE ? Helper.iconsExpense[category.getIcon()] : Helper.iconsIncome[category.getIcon()],
                    category.getName(),
                    Helper.formatCurrencyWithoutSymbol(totalCost));

            holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category, position));
            holder.itemView.setOnLongClickListener(v -> {
                listener.onCategoryLongClick(category, position);
                return false;
            });
        } else {
            holder.bindData(R.drawable.ic_add_circle_outline, "Thêm", "");

            holder.itemView.setOnClickListener(v -> listener.onCategoryAdd());
        }
    }

    @Override
    public int getItemCount() {
        return categories.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) return 1;
        return 0;
    }


    // --------------ViewHolder ----------------
    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryImage;
        TextView categoryName;
        TextView categoryTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.image_view_category);
            categoryName = itemView.findViewById(R.id.text_view_account_out);
            categoryTotal = itemView.findViewById(R.id.text_view_category_total);
        }

        public void bindData(int imgRes, String name, String total) {
            categoryImage.setImageResource(imgRes);
            categoryName.setText(name);
            categoryTotal.setText(total);
        }
    }
}
