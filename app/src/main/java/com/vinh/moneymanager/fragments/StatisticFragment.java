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
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
import java.util.Random;
import java.util.TreeMap;

public class StatisticFragment extends Fragment {

    private FragmentStatisticBinding binding;

    private CategoryViewModel categoryViewModel;
    private FinanceViewModel financeViewModel;

    private PieChart pieChart;
    private HorizontalBarChart horizontalBarChart;
    private BarChart barChart;

    private Map<Category, List<Finance>> mapAllFinances, mapMonthFinances;

    private DateHandlerClick dateHandlerClick;
    private DateRange dateRange;

    private int statisticMode = Helper.TYPE_EXPENSE;

    private final int HORIZONTAL_BAR_HEIGHT = 150;

    private int[] colorsResource;

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

        colorsResource = new int[]{
                ContextCompat.getColor(getContext(), R.color.cyan),
                ContextCompat.getColor(getContext(), R.color.blueGray),
                ContextCompat.getColor(getContext(), R.color.jade),
                ContextCompat.getColor(getContext(), R.color.lightBlue),
                ContextCompat.getColor(getContext(), R.color.royalBlue),
                ContextCompat.getColor(getContext(), R.color.brown),
                ContextCompat.getColor(getContext(), R.color.khaki),
                ContextCompat.getColor(getContext(), R.color.glaucous),
                ContextCompat.getColor(getContext(), R.color.sageGreen),
                ContextCompat.getColor(getContext(), R.color.brightGreen),
                ContextCompat.getColor(getContext(), R.color.malachite),
                ContextCompat.getColor(getContext(), R.color.amber),
                ContextCompat.getColor(getContext(), R.color.coralPink),
                ContextCompat.getColor(getContext(), R.color.fuchsia),
                ContextCompat.getColor(getContext(), R.color.purple),
                ContextCompat.getColor(getContext(), R.color.iris),
                ContextCompat.getColor(getContext(), R.color.lightViolet),
                ContextCompat.getColor(getContext(), R.color.scarlet)
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistic, container, false);
        View view = binding.getRoot();

        pieChart = view.findViewById(R.id.pieChart);
        horizontalBarChart = view.findViewById(R.id.horizontalBarChart);
        initPieAndHorizontalBarChart();


        barChart = view.findViewById(R.id.barChart);
        setupBarChart(barChart);

        categoryViewModel.getCategories().observe(this.getViewLifecycleOwner(), allCategories -> {
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

        // --- Dialog Settings ---
        initSettingsDialog();

        ImageView btnSettings = view.findViewById(R.id.btnSetting);
        btnSettings.setOnClickListener(v -> {
            dialogSettings.show();
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

    private void initPieAndHorizontalBarChart() {
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(38f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterTextSize(12);

        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(20, 0, 20, 0);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(15);
        legend.setTextSize(12);
        legend.setFormToTextSpace(10);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setDrawInside(false);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry entry = (PieEntry) e;
                Toast.makeText(StatisticFragment.this.getContext(), entry.getLabel() + ": " + Helper.formatCurrency((long) entry.getY()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // Horizontal Bar Chart
        horizontalBarChart.getDescription().setEnabled(false);
        horizontalBarChart.setDrawGridBackground(false);
        horizontalBarChart.setFitBars(true);

        horizontalBarChart.getXAxis().setDrawGridLines(false);
        horizontalBarChart.getXAxis().setDrawAxisLine(false);
        horizontalBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        horizontalBarChart.getXAxis().setEnabled(true);
        horizontalBarChart.getXAxis().setTextSize(12);

        horizontalBarChart.getAxisLeft().setEnabled(true);
        horizontalBarChart.getAxisLeft().setAxisMinimum(0f);
        horizontalBarChart.getAxisRight().setEnabled(false);

        horizontalBarChart.getLegend().setEnabled(false);
        horizontalBarChart.setDrawValueAboveBar(true);
        horizontalBarChart.setExtraOffsets(-10, 0, 0, 0);
        horizontalBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                BarEntry entry = (BarEntry) e;
                Toast.makeText(StatisticFragment.this.getContext(), labels.get((int) entry.getX()) + ": " + Helper.formatCurrency((long) entry.getY()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private ArrayList<String> labels = new ArrayList<>();
    private ArrayList<PieEntry> pieEntries = new ArrayList<>();
    private ArrayList<BarEntry> barEntries = new ArrayList<>();
    private int[] chartColors;

    private void updateChartData() {
        chartColors = getRandomColors();

        labels.clear();
        pieEntries.clear();
        barEntries.clear();

        int index = 0;
        for (Category category : mapMonthFinances.keySet()) {
            if (category.getType() != statisticMode) continue;

            long money = 0;
            for (Finance finance : mapMonthFinances.get(category)) {
                money += finance.getMoney();
            }
            if (money != 0) {
                labels.add(category.getName());
                pieEntries.add(new PieEntry(money, category.getName()));
                barEntries.add(new BarEntry(index, money));
                index++;
            }
        }

        updatePieChartData();
        updateHorizontalBarChartData();
    }

    private void updatePieChartData() {
        pieChart.setCenterText(statisticMode == Helper.TYPE_INCOME ? "THU NHẬP" : "CHI TIÊU");
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setSliceSpace(1);
        pieDataSet.setValueTextSize(13);
        pieDataSet.setColors(chartColors);

        pieDataSet.setValueLinePart1OffsetPercentage(83.f);
        pieDataSet.setValueLinePart1Length(0.5f);
        pieDataSet.setValueLinePart2Length(0.4f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                return String.format("%.1f", value) + "%";
            }
        });

        pieChart.setData(new PieData(pieDataSet));
        // Hiển thị labels và values
        pieChart.setDrawEntryLabels(isShowLabels);
        for (IDataSet<?> set : pieChart.getData().getDataSets())
            set.setDrawValues(isShowValues);

        pieChart.animateXY(1000, 1000);
        pieChart.invalidate();
    }

    private void updateHorizontalBarChartData() {
        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(chartColors);
        barDataSet.setValueTextColor(Color.BLUE);
        barDataSet.setValueTextSize(12);
        barDataSet.setDrawValues(true);

        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return Helper.formatCurrentWithoutSymbol((long) barEntry.getY());
            }
        });

        horizontalBarChart.getXAxis().setEnabled(false);
        BarData barData = new BarData(barDataSet);
        barData.setHighlightEnabled(true);
        barData.setValueTextColor(Color.BLACK);
        barData.setBarWidth(0.8f);
        horizontalBarChart.setData(barData);

        // TODO: Chưa thể set height khi vừa vào menu Thống Kê, hoặc chuyển từ xem CHI TIÊU <-> THU NHẬP
        horizontalBarChart.getLayoutParams().height = Math.max(barEntries.size() * HORIZONTAL_BAR_HEIGHT, 450);
        Toast.makeText(this.getContext(), horizontalBarChart.getLayoutParams().height + "", Toast.LENGTH_SHORT).show();

        horizontalBarChart.animateY(1500);
        horizontalBarChart.invalidate();
    }


    public int[] getRandomColors() {
        int[] ranIndex = new int[colorsResource.length];
        for (int i = 0; i < ranIndex.length; i++) ranIndex[i] = i;
        Random r = new Random();
        for (int i = ranIndex.length - 1; i > 0; i--) {
            int j = Math.abs(r.nextInt()) % (i + 1);
            int k = ranIndex[i];
            ranIndex[i] = ranIndex[j];
            ranIndex[j] = k;
        }
        for (int i = 0; i < ranIndex.length; i++) {
            ranIndex[i] = colorsResource[ranIndex[i]];
        }
        return ranIndex;
    }

    private void showPieChartLabels(boolean isShowLabels) {
        pieChart.setDrawEntryLabels(isShowLabels);
        pieChart.invalidate();
    }

    private void showPieChartValues(boolean isShowValues) {
        for (IDataSet<?> set : pieChart.getData().getDataSets())
            set.setDrawValues(isShowValues);

        pieChart.invalidate();
    }

    private boolean isShowLabels = true;
    private boolean isShowValues = true;
    Dialog dialogSettings;

    private void initSettingsDialog() {
        dialogSettings = new Dialog(getContext());
        dialogSettings.setContentView(R.layout.dialog_settings);
        dialogSettings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CheckBox cbShowLabels = dialogSettings.findViewById(R.id.cbShowChartLabels);
        CheckBox cbShowValues = dialogSettings.findViewById(R.id.cbShowChartValues);

        cbShowLabels.setChecked(isShowLabels);
        cbShowValues.setChecked(isShowValues);

        Button btnClose = dialogSettings.findViewById(R.id.btnCloseDialog);
        btnClose.setOnClickListener(v -> {

            if (isShowLabels != cbShowLabels.isChecked()) {
                isShowLabels = cbShowLabels.isChecked();
                showPieChartLabels(isShowLabels);
            }

            if (isShowValues != cbShowValues.isChecked()) {
                isShowValues = cbShowValues.isChecked();
                showPieChartValues(isShowValues);
            }

            dialogSettings.dismiss();
        });

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