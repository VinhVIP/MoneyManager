package com.vinh.moneymanager.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;
import java.util.Map;

public class ExpandCategoryFinanceAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Category> categories;
    private Map<Category, List<Finance>> mapFinance;


    public ExpandCategoryFinanceAdapter(Context context, List<Category> categories, Map<Category, List<Finance>> mapFinance) {
        this.context = context;
        this.categories = categories;
        this.mapFinance = mapFinance;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
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
    public Category getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Finance getChild(int groupPosition, int childPosition) {
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
            holder = new GroupHolder(convertView);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        Category category = categories.get(groupPosition);

        long totalCost = 0;
        for (Finance f : mapFinance.get(categories.get(groupPosition))) {
            totalCost += f.getCost();
        }

        holder.bindData(R.drawable.ic_star, category.getName(), totalCost);

        convertView.setTag(holder);
        return convertView;
    }

    private class GroupHolder {
        ImageView imgView;
        TextView tvTitle;
        TextView tvTotal;

        public GroupHolder(View view) {
            imgView = view.findViewById(R.id.image_view_list_group);
            tvTitle = view.findViewById(R.id.text_view_title);
            tvTotal = view.findViewById(R.id.text_view_total);
        }

        public void bindData(int imgRes, String title, long total) {
            imgView.setImageResource(imgRes);
            tvTitle.setText(title);
            tvTotal.setText(Helper.formatCurrency(total));
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ChildHolder(convertView);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.bindData(getChild(groupPosition, childPosition));

        convertView.setTag(holder);
        return convertView;
    }

    private class ChildHolder {
        TextView tvDay;
        TextView tvMonthYear;
        TextView tvDayOfWeek;
        TextView tvDetail;
        TextView tvCost;

        ImageView imgDelete;

        public ChildHolder(View view) {
            tvDay = view.findViewById(R.id.tv_calendar_day);
            tvMonthYear = view.findViewById(R.id.tv_calendar_month_year);
            tvDayOfWeek = view.findViewById(R.id.tv_calendar_day_of_week);
            tvDetail = view.findViewById(R.id.text_view_item_detail);
            tvCost = view.findViewById(R.id.text_view_item_price);



//            view.setOnTouchListener(new OnSwipeTouchListener(context){
//                @Override
//                public void onSwipeLeft() {
//                    Toast.makeText(context, "Swipe left", Toast.LENGTH_SHORT).show();
//                    float dip = 40f;
//                    Resources r = context.getResources();
//                    float px = TypedValue.applyDimension(
//                            TypedValue.COMPLEX_UNIT_DIP,
//                            dip,
//                            r.getDisplayMetrics()
//                    );
//                    view.setTranslationX(-px);
//                    imgDelete.getLayoutParams().width = (int) px;
//                    imgDelete.requestLayout();
//                }
//
//                @Override
//                public void onClick() {
//                    Toast.makeText(context, "Tapped", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onLongClick() {
//                    Toast.makeText(context, "Long click", Toast.LENGTH_SHORT).show();
//                }
//            });


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

            long cost = finance.getCost();

            tvCost.setText(Helper.formatCurrency(cost));
        }
    }



    // ---------- OnTouch Event ----------------
    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                onClick();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                onLongClick();
                super.onLongPress(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onLongClick(){
        }

        public void onClick(){
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }

    public interface OnItemChildListener{
        void onTap(int groupPosition, int childPosition);
    }

}
