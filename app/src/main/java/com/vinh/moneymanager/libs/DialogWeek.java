package com.vinh.moneymanager.libs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.adapters.RecyclerWeekAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DialogWeek implements View.OnClickListener {

    RecyclerWeekAdapter.OnItemWeekClickListener listener;
    private final Context context;
    private Dialog dialog;
    private TextView tvMonth, tvYear;
    private View btnPreviousMonth, btnNextMonth, btnPreviousYear, btnNextYear, imgClose, btnCurWeek;
    private int month, year;
    private RecyclerView recyclerView;
    private RecyclerWeekAdapter weekAdapter;

    public DialogWeek(Context context, int month, int year, RecyclerWeekAdapter.OnItemWeekClickListener listener) {
        this.context = context;
        this.month = month;
        this.year = year;

        this.listener = listener;

        initDialog();
        updateMonthYear();
    }

    public void setMonthYear(int month, int year) {
        this.month = month;
        this.year = year;

        updateMonthYear();
    }

    private void initDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_choose_week);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvYear = dialog.findViewById(R.id.text_view_year);
        tvMonth = dialog.findViewById(R.id.text_view_month);

        recyclerView = dialog.findViewById(R.id.recycler_choose_week);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        btnPreviousMonth = dialog.findViewById(R.id.btn_previous_month);
        btnNextMonth = dialog.findViewById(R.id.btn_next_month);
        btnPreviousYear = dialog.findViewById(R.id.btn_previous_year);
        btnNextYear = dialog.findViewById(R.id.btn_next_year);
        imgClose = dialog.findViewById(R.id.img_close_dialog);
        btnCurWeek = dialog.findViewById(R.id.tv_choose_current_week);

        btnPreviousMonth.setOnClickListener(this);
        btnNextMonth.setOnClickListener(this);
        btnPreviousYear.setOnClickListener(this);
        btnNextYear.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        btnCurWeek.setOnClickListener(this);
    }

    private void updateMonthYear() {
        tvMonth.setText(String.format("%02d", month));
        tvYear.setText(String.valueOf(year));

        weekAdapter = new RecyclerWeekAdapter(context, getListDateRange(), listener);
        recyclerView.setAdapter(weekAdapter);

    }

    private List<DateRange> getListDateRange() {
        List<DateRange> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        while (true) {
            DateRange range = new DateRange(DateRange.MODE_WEEK);
            range.setWeek(week, year);

            if (range.getStartDate().year > year || (range.getStartDate().year == year && range.getStartDate().month > month)) {
                break;
            }

            list.add(range);
            week++;
        }

        return list;
    }

    public void showDialog() {
        this.dialog.show();
    }

    public void hideDialog() {
        this.dialog.cancel();
    }

    private void chooseCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        DateRange curDateRange = new DateRange(DateRange.MODE_WEEK);
        curDateRange.setWeek(week, calendar.get(Calendar.YEAR));

        listener.onItemWeekClick(curDateRange);
        hideDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous_month:
                if (--month < 1) {
                    month = 12;
                    year--;
                }
                updateMonthYear();
                break;
            case R.id.btn_next_month:
                if (++month > 12) {
                    month = 1;
                    year++;
                }
                updateMonthYear();
                break;
            case R.id.btn_previous_year:
                year--;
                updateMonthYear();
                break;
            case R.id.btn_next_year:
                year++;
                updateMonthYear();
                break;
            case R.id.img_close_dialog:
                hideDialog();
                break;
            case R.id.tv_choose_current_week:
                chooseCurrentWeek();
                break;
        }
    }

}


