package com.vinh.moneymanager.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.adapters.ExpandableListFinanceAdapter;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class IncomeFragment extends Fragment {

    private static IncomeFragment instance;

    private ExpandableListView expandableListFinance;
    private ExpandableListAdapter listAdapter;

    private List<Category> categories;
    private HashMap<Category, List<Finance>> mapFinance;

    private Calendar calendar;

    private ImageView imgViewChooseDate;
    private TextView tvDate;

    private int lastSelectedYear, lastSelectedMonth, lastSelectedDayOfMonth;

    public IncomeFragment(){
        calendar = Calendar.getInstance();
        lastSelectedYear = calendar.get(Calendar.YEAR);
        lastSelectedMonth = calendar.get(Calendar.MONTH);
        lastSelectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static IncomeFragment getInstance() {
        if(instance == null){
            instance = new IncomeFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);
        expandableListFinance = view.findViewById(R.id.expandable_list_finance);

        mapFinance = new HashMap<>();
        categories = new ArrayList<>(mapFinance.keySet());

        listAdapter = new ExpandableListFinanceAdapter(this.getContext(), categories, mapFinance);
        expandableListFinance.setAdapter(listAdapter);

        expandableListFinance.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        expandableListFinance.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        imgViewChooseDate = view.findViewById(R.id.img_view_choose_date);
        tvDate = view.findViewById(R.id.text_view_date);
        tvDate.setText(String.format("%d/%d/%d", lastSelectedDayOfMonth, lastSelectedMonth + 1, lastSelectedYear));
        tvDate.setText("WEEK: "+Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, 48);
        calendar.set(Calendar.YEAR, 2020);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = calendar.getTime();
        String s = simpleDateFormat.format(date);
        tvDate.setText(s);

        imgViewChooseDate.setOnClickListener((v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(), onDateSetListener, lastSelectedYear, lastSelectedMonth, lastSelectedDayOfMonth);
            datePickerDialog.show();
        });

        return view;
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String selectedDate = String.format("%d/%d/%d", year, month, dayOfMonth);
            Date d, curDate;
            try {
                d = sdf.parse(selectedDate);
                curDate = sdf.parse(String.format("%d/%d/%d", calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
                if (d.after(curDate)) {
                    Toast.makeText(IncomeFragment.this.getContext(), "Không thể chọn ngày ở tương lai", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvDate.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year));

            lastSelectedYear = year;
            lastSelectedMonth = month;
            lastSelectedDayOfMonth = dayOfMonth;
        }
    };

    /*private static HashMap<Category, List<Finance>> populatedListData() {
        HashMap<Category, List<Finance>> hashMap = new HashMap<>();

        List<Finance> cricket = new ArrayList<>();
        cricket.add(new Finance(0, 20000, "11:30", "Work AAA", 0));
        cricket.add(new Finance(0, 20000, "11:30", "Mua ABC", 0));
        cricket.add(new Finance(0, 20000, "11:30", "Mua ABC", 0));
        cricket.add(new Finance(0, 20000, "11:30", "Mua ABC", 0));


        List<Finance> football = new ArrayList<>();
        football.add(new Finance(0, 20000, "11:30", "Work ABC", 0));
        football.add(new Finance(0, 20000, "11:30", "Mua ABC", 0));
        football.add(new Finance(0, 20000, "11:30", "Mua ABC", 0));
        football.add(new Finance(0, 20000, "11:30", "Mua ABC", 0));

        List<Finance> basketball = new ArrayList<>();
        basketball.add(new Finance(0, 20000, "11:30", "Mua ABC", 0));
        basketball.add(new Finance(0, 20000, "11:30", "Work ABC", 0));
        basketball.add(new Finance(0, 20000, "11:30", "Mua ABC", 0));
        basketball.add(new Finance(0, 20000, "11:30", "Mua ABC", 0));
        basketball.add(new Finance(0, 20000, "11:30", "Mua ABC", 0));

        hashMap.put(new Category("Wedding", 1, "Ahihi"), cricket);
        hashMap.put(new Category("Factory", 1, "Ahihi"), football);
        hashMap.put(new Category("Massage", 1, "Ahihi"), basketball);

        return hashMap;
    }*/
}