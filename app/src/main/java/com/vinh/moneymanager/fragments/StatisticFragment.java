package com.vinh.moneymanager.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vinh.moneymanager.R;
import com.vinh.moneymanager.databinding.FragmentStatisticBinding;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.viewmodels.CategoryViewModel;
import com.vinh.moneymanager.viewmodels.FinanceViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatisticFragment extends Fragment implements OnChartValueSelectedListener {

    private FragmentStatisticBinding binding;

    private CategoryViewModel categoryViewModel;
    private FinanceViewModel financeViewModel;

    private PieChart pieChart;
    private HorizontalBarChart horizontalBarChart;
    private BarChart barChart;

    private List<Category> allCategories;
    private Map<Category, List<Finance>> mapAllFinances, mapMonthFinances;

    private DateHandlerClick dateHandlerClick;
    private DateRange dateRange;

    private int statisticMode = Helper.TYPE_EXPENSE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        // Mặc định là MODE_MONTH
        dateRange = new DateRange(DateRange.MODE_MONTH,
                new DateRange.Date(1, month, year),
                new DateRange.Date(DateRange.getLastDay(month, year), month, year));

        dateHandlerClick = new DateHandlerClick();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistic, container, false);
        View view = binding.getRoot();

        pieChart = view.findViewById(R.id.pieChart);
        horizontalBarChart = view.findViewById(R.id.horizontalBarChart);
        setupPieAndBarChart();


        barChart = view.findViewById(R.id.barChart);
        setupBarChart(barChart);

        categoryViewModel.getCategories().observe(this.getViewLifecycleOwner(), allCategories -> {
            this.allCategories = allCategories;

            financeViewModel.getAllFinances().observe(this.getViewLifecycleOwner(), finances -> {
                Map<Category, List<Finance>> map = new TreeMap<>((c1, c2) -> c1.getCategoryId() - c2.getCategoryId());

                for (Category c : allCategories) map.put(c, new ArrayList<>());

                for (Finance f : finances) {
                    for (Category c : map.keySet()) {
                        if (f.getCategoryId() == c.getCategoryId()) {
                            map.get(c).add(f);
                        }
                    }
                }
                this.mapAllFinances = map;
                update();


            });
        });

        binding.setDate(dateRange);
        binding.setDateHandler(dateHandlerClick);
        binding.setMode(statisticMode);

        Button btnChange = view.findViewById(R.id.btnStatistic);
        btnChange.setOnClickListener(v -> {
            if (statisticMode == Helper.TYPE_INCOME) statisticMode = Helper.TYPE_EXPENSE;
            else statisticMode = Helper.TYPE_INCOME;
            binding.setMode(statisticMode);

            updateChartData();
        });

        return view;
    }


    private void update() {
        Map<Category, List<Finance>> mapRange = new TreeMap<>((c1, c2) -> c1.getCategoryId() - c2.getCategoryId());

        for (Category c : mapAllFinances.keySet()) {
            mapRange.put(c, new ArrayList<>());

            for (Finance f : mapAllFinances.get(c)) {
                String strDate = f.getDateTime().split("-")[0].trim();
                DateRange.Date date = new DateRange.Date(strDate);
                if (date.compare(dateRange.getStartDate()) >= 0 && date.compare(dateRange.getEndDate()) <= 0) {
                    mapRange.get(c).add(f);
                }

            }
        }

        this.mapMonthFinances = mapRange;
        updateChartData();
    }


    private void setupBarChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        chart.animateY(2000);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);

            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();

        values1.add(new BarEntry(0, 25));
        values1.add(new BarEntry(1, 20));
        values1.add(new BarEntry(2, 32));

        values2.add(new BarEntry(0, 17));
        values2.add(new BarEntry(1, 10));
        values2.add(new BarEntry(2, 8));

        BarDataSet set1 = new BarDataSet(values1, "Set A");
        set1.setColor(Color.GREEN);
        BarDataSet set2 = new BarDataSet(values2, "Set B");
        set2.setColor(Color.BLUE);
        set1.setValueTextSize(12);
        set2.setValueTextSize(12);

        BarData data = new BarData(set1, set2);

        float groupSpace = 0.5f;
        float barSpace = 0.05f; // x4 DataSet
        float barWidth = 0.2f; // x4 DataSet
        int groupCount = 3;

        int startYear = 0;

        chart.setData(data);

        chart.getXAxis().setAxisMaximum(chart.getBarData().getGroupWidth(groupSpace, barSpace) * (groupCount + 1));
        chart.groupBars(startYear, groupSpace, barSpace);

        chart.getData().setHighlightEnabled(false);

        chart.getXAxis().setEnabled(false);
        chart.invalidate();
    }

    private void setupPieAndBarChart() {
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(35f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterTextSize(12);

        pieChart.setDrawEntryLabels(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.parseColor("#ccccff"));
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5, 5, 5, 20);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(15);
        legend.setTextSize(12);
        legend.setFormToTextSpace(10);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setDrawInside(false);

        pieChart.setOnChartValueSelectedListener(this);

        // Horizontal Bar Chart
        horizontalBarChart.getDescription().setEnabled(false);
        horizontalBarChart.setDrawGridBackground(false);
        horizontalBarChart.setFitBars(true);

        horizontalBarChart.getXAxis().setDrawGridLines(false);
        horizontalBarChart.getXAxis().setDrawAxisLine(false);
        horizontalBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        horizontalBarChart.getXAxis().setEnabled(true);

        horizontalBarChart.getAxisLeft().setEnabled(false);
        horizontalBarChart.getAxisRight().setEnabled(false);
        horizontalBarChart.getLegend().setEnabled(false);
        horizontalBarChart.setDrawValueAboveBar(false);

    }

    private void updateChartData() {
        pieChart.setCenterText(statisticMode == Helper.TYPE_INCOME ? "Thu nhập" : "Chi tiêu");

        ArrayList<String> labels = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int[] colors = ColorTemplate.MATERIAL_COLORS;

        int index = 0;
        for (Category category : mapMonthFinances.keySet()) {
            if (category.getType() != statisticMode) continue;

            long total = 0;
            for (Finance finance : mapMonthFinances.get(category)) {
                total += finance.getMoney();
            }
            if (total != 0) {
                labels.add(category.getName());
                pieEntries.add(new PieEntry(total, category.getName()));
                barEntries.add(new BarEntry(index++, total));
            }
        }
        System.out.println("labels: " + labels.size());

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(13);
        pieDataSet.setColors(colors);

        pieChart.setData(new PieData(pieDataSet));

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(Color.BLUE);
        barDataSet.setValueTextSize(12);
        barDataSet.setDrawValues(true);

        horizontalBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        horizontalBarChart.setData(new BarData(barDataSet));
        horizontalBarChart.getData().setHighlightEnabled(false);

        pieChart.animateXY(1000, 1000);
        horizontalBarChart.animateY(1500);

        pieChart.invalidate();
        horizontalBarChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry entry = (PieEntry) e;
        Toast.makeText(this.getContext(), entry.getLabel() + " : " + entry.getY(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }


    //-------- Date Click ------------
    public class DateHandlerClick {

        public void onDateClick(View v) {
            dialogChooseMonth(dateRange);
        }

        public void previous(View view) {
            dateRange.previous();
            binding.setDate(dateRange);
            update();
        }

        public void next(View view) {
            dateRange.next();
            binding.setDate(dateRange);
            update();
        }

        private void dialogChooseMonth(DateRange range) {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_choose_month);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView imgClose = dialog.findViewById(R.id.img_close_dialog);
            imgClose.setOnClickListener(v -> dialog.cancel());

            View btnChooseCurMonth = dialog.findViewById(R.id.tv_choose_current_month);
            btnChooseCurMonth.setOnClickListener(v -> {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
                range.setStartDate(new DateRange.Date(1, month, year));
                range.setEndDate(new DateRange.Date(DateRange.getLastDay(month, year), month, year));
//                mViewModel.setDateRangeValue(range);
                dialog.cancel();
            });

            List<String> list = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                list.add("Th" + i);
            }

            TextView tvYear = dialog.findViewById(R.id.text_view_year);
            tvYear.setText(String.valueOf(range.getStartDate().getYear()));

            View btnPrevious = dialog.findViewById(R.id.btn_previous_month);
            View btnNext = dialog.findViewById(R.id.btn_next_month);
            btnPrevious.setOnClickListener((v) -> {
                int year = Integer.parseInt(tvYear.getText().toString());
                tvYear.setText(String.valueOf(year - 1));
            });
            btnNext.setOnClickListener((v) -> {
                int year = Integer.parseInt(tvYear.getText().toString());
                tvYear.setText(String.valueOf(year + 1));
            });

            GridView gridMonth = dialog.findViewById(R.id.grid_view_month);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_text_center, list);
            gridMonth.setAdapter(adapter);

            gridMonth.setOnItemClickListener((parent, view, position, id) -> {
                int year = Integer.parseInt(tvYear.getText().toString());
                range.setStartDate(new DateRange.Date(1, position + 1, year));
                range.setEndDate(new DateRange.Date(DateRange.getLastDay(position + 1, year), position + 1, year));

                binding.setDate(dateRange);
                update();

                dialog.cancel();
            });


            dialog.show();
        }
    }

}