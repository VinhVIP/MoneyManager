package com.vinh.moneymanager.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
import androidx.core.content.res.ResourcesCompat;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class StatisticFragment extends Fragment {

    private static StatisticFragment instance;
    private final int HORIZONTAL_BAR_HEIGHT = 35;

    private final ArrayList<BarEntry> entriesExpense = new ArrayList<>();
    private final ArrayList<BarEntry> entriesIncome = new ArrayList<>();

    private Dialog dialogSettings;

    private FragmentStatisticBinding binding;

    private CategoryViewModel categoryViewModel;
    private FinanceViewModel financeViewModel;

    private PieChart pieChart;
    private HorizontalBarChart horizontalBarChart;
    private BarChart barChart;

    private TextView tvNoData;

    private List<Finance> allFinances = new ArrayList<>();
    private Map<Category, List<Finance>> mapAllFinances, mapMonthFinances;

    private DateHandlerClick dateHandlerClick;
    private DateRange dateRange;

    private int statisticMode = Helper.TYPE_EXPENSE;
    private int[] colorsResource;
    private int currentYear;

    private final ArrayList<String> labels = new ArrayList<>();
    private final ArrayList<Long> values = new ArrayList<>();
    private final ArrayList<PieEntry> pieEntries = new ArrayList<>();
    private final ArrayList<BarEntry> barEntries = new ArrayList<>();

    private int[] chartColors;
    private boolean isShowLabels = true;
    private boolean isShowValues = true;

    private Typeface tfRegular, tfLight;

    public static StatisticFragment getInstance() {
        if (instance == null) {
            instance = new StatisticFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryViewModel = new ViewModelProvider(getActivity()).get(CategoryViewModel.class);
        financeViewModel = new ViewModelProvider(getActivity()).get(FinanceViewModel.class);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        // Mặc định là MODE_MONTH
        dateRange = new DateRange(DateRange.MODE_MONTH,
                new DateRange.Date(1, month, year),
                new DateRange.Date(DateRange.getLastDay(month, year), month, year));

        dateHandlerClick = new DateHandlerClick();

        // Typeface
        tfRegular = ResourcesCompat.getFont(getContext(), R.font.oswald);
        tfRegular = ResourcesCompat.getFont(getContext(), R.font.oswald_light);

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

        tvNoData = view.findViewById(R.id.tv_no_data);

        pieChart = view.findViewById(R.id.pieChart);
        horizontalBarChart = view.findViewById(R.id.horizontalBarChart);
        initPieAndHorizontalBarChart();


        barChart = view.findViewById(R.id.barChart);
        initBarChar();

        categoryViewModel.getCategories().observe(getActivity(), allCategories -> {
            financeViewModel.getAllFinances().observe(getActivity(), finances -> {
                allFinances = finances;

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

        updateDataYear();
    }

    long[][] money = new long[12][2];

    private void updateDataYear() {
        int selectedYear = dateRange.getStartDate().getYear();
        currentYear = selectedYear;

        for (long[] longs : money) Arrays.fill(longs, 0);

        for (Finance f : allFinances) {
            String[] dates = f.getDateTime().split("-")[0].trim().split("/");
            int year = Integer.parseInt(dates[2]);
            int month = Integer.parseInt(dates[1]);

            if (year == selectedYear) {
                int type = getCategoryType(f.getCategoryId());

                if (type == Helper.TYPE_EXPENSE) money[month - 1][0] += f.getMoney();
                else if (type == Helper.TYPE_INCOME) money[month - 1][1] += f.getMoney();
            }
        }

        entriesExpense.clear();
        entriesIncome.clear();
        for (int mon = 0; mon < money.length; mon++) {
            entriesExpense.add(new BarEntry(mon, money[mon][0]));
            entriesIncome.add(new BarEntry(mon, money[mon][1]));
        }

        updateChartYear();
    }

    private int getCategoryType(int categoryId) {
        for (Category c : mapAllFinances.keySet()) {
            if (c.getCategoryId() == categoryId) return c.getType();
        }
        return -1;
    }

    private void updateChartYear() {

        BarDataSet dataSet = new BarDataSet(entriesExpense, "Chi tiêu");
        dataSet.setColor(Color.RED);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return showValue(value);
            }
        });


        BarDataSet dataSet2 = new BarDataSet(entriesIncome, "Thu nhập");
        dataSet2.setColor(Color.GREEN);
        dataSet2.setDrawValues(true);
        dataSet2.setValueTextSize(10);
        dataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return showValue(value);
            }
        });

        float groupSpace = 0.12f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.42f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData barData = new BarData(dataSet, dataSet2);
        barData.setValueTypeface(tfRegular);
        barData.setBarWidth(barWidth);

        // make this BarData object grouped
        barData.groupBars(0, groupSpace, barSpace); // start at x = 0

        barChart.setData(barData);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * 12);

        barChart.invalidate();
    }

    private void initBarChar() {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.getDescription().setEnabled(false);
        String[] labels = {"Chi tiêu", "Thu nhập"};
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int setIndex = h.getDataSetIndex();
                int index = (int) e.getX();
                int month = index + 1;
                String currency = Helper.formatCurrency(money[index][setIndex]);
                String mess = String.format("%s tháng %d: %s", labels[setIndex], month, currency);
                Toast.makeText(StatisticFragment.this.getContext(), mess, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
//        barChart.setMaxVisibleValueCount(12);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfRegular);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(12);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "T" + (int) (value + 1);
            }
        });
//        xAxis.setValueFormatter(xAxisFormatter);

//        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTypeface(tfRegular);
//        leftAxis.setLabelCount(8, false);
//        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(10f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return showValue(value);
            }
        });

//        barChart.setExtraOffsets(0, 0, 0, -10);

        barChart.getAxisRight().setEnabled(false);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        l.setYEntrySpace(4f);
        l.setTypeface(tfRegular);

//        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
//        mv.setChartView(chart); // For bounds control
//        chart.setMarker(mv); // Set the marker to the chart


    }

    private void initPieAndHorizontalBarChart() {
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(38f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterTextSize(12);
        pieChart.setCenterTextTypeface(tfRegular);

        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(15, 5, 15, 5);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(15);
        legend.setTextSize(12);
        legend.setFormToTextSpace(10);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTypeface(tfRegular);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setDrawInside(false);
        legend.setEnabled(false); // disable

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry entry = (PieEntry) e;
                Toast.makeText(StatisticFragment.this.getContext(), entry.getLabel() + ": " + Helper.formatCurrency(values.get((int) h.getX())), Toast.LENGTH_SHORT).show();
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
        horizontalBarChart.getXAxis().setTypeface(tfRegular);

        horizontalBarChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return showValue(value);
            }
        });

        // Bottom Axis
        horizontalBarChart.getAxisLeft().setEnabled(true);
        horizontalBarChart.getAxisLeft().setAxisMinimum(0f);
        horizontalBarChart.getAxisLeft().setTypeface(tfRegular);
        horizontalBarChart.getAxisRight().setEnabled(false);


        horizontalBarChart.getLegend().setEnabled(false);
        horizontalBarChart.setDrawValueAboveBar(true);
        horizontalBarChart.setExtraOffsets(-10, 0, 0, 0);
        horizontalBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int i = labels.size() - 1 - (int) h.getX();
                String label = labels.get(i);
                String currency = Helper.formatCurrency(values.get(i));
                String mess = String.format("%s: %s", label, currency);
                Toast.makeText(StatisticFragment.this.getContext(), mess, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private String toK(float value) {
        if (value == 0) return "0";
        return String.format("%.0fK", value / 1e3);
    }

    private String toM(float value) {
        String s = toMWithoutSymbol(value);
        return s + "M";
    }

    private String toMWithoutSymbol(float value) {
        String s = String.format("%.2f", value / 1e6);
        while (s.endsWith("0")) s = s.substring(0, s.length() - 1);
        return s.endsWith(",") ? s.substring(0, s.length() - 1) : s;
    }

    private String toB(float value) {
        String s = toBWithoutSymbol(value);
        return s + "B";
    }

    private String toBWithoutSymbol(float value) {
        String s = String.format("%.2f", value / 1e9);
        while (s.endsWith("0")) s = s.substring(0, s.length() - 1);
        return s.endsWith(",") ? s.substring(0, s.length() - 1) : s;
    }

    private void updateChartData() {
        chartColors = getRandomColors();

        labels.clear();
        values.clear();
        pieEntries.clear();
        barEntries.clear();

        ArrayList<Long> listBarEntry = new ArrayList<>();

        for (Category category : mapMonthFinances.keySet()) {
            if (category.getType() != statisticMode) continue;

            long money = 0;
            for (Finance finance : mapMonthFinances.get(category)) {
                money += finance.getMoney();
            }
            if (money != 0) {
                labels.add(category.getName());
                values.add(money);
                pieEntries.add(new PieEntry(money, category.getName().length() > 10 ? category.getName().substring(0, 10).trim().concat("...") : category.getName()));
                listBarEntry.add(money);
            }
        }

        // Đảo ngược thứ tự xuất hiện
        for (int i = 0; i < listBarEntry.size(); i++) {
            barEntries.add(new BarEntry(i, listBarEntry.get(listBarEntry.size() - 1 - i)));
        }

        if (pieEntries.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            horizontalBarChart.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            pieChart.setVisibility(View.VISIBLE);
            horizontalBarChart.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            updatePieChartData();
            updateHorizontalBarChartData();
        }
    }

    private void updatePieChartData() {
        long total = 0;
        for (long money : values) {
            total += money;
        }
        String title = (statisticMode == Helper.TYPE_INCOME ? "THU NHẬP\n" : "CHI TIÊU\n") + Helper.formatCurrency(total);
        SpannableString str = new SpannableString(title);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0,8,0);

        pieChart.setCenterText(str);

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

        PieData data = new PieData(pieDataSet);
        data.setValueTypeface(tfRegular);
        pieChart.setData(data);
        // Hiển thị labels và values
        pieChart.setDrawEntryLabels(isShowLabels);
        pieChart.setEntryLabelTypeface(tfRegular);
        for (IDataSet<?> set : pieChart.getData().getDataSets())
            set.setDrawValues(isShowValues);

        pieChart.animateXY(1000, 1000);
        pieChart.invalidate();
    }

    private void updateHorizontalBarChartData() {
        horizontalBarChart.getLayoutParams().height = (int) Helper.convertDpToPixel(50 + barEntries.size() * HORIZONTAL_BAR_HEIGHT, getContext());
        horizontalBarChart.requestLayout();     // Cập nhật lại layout

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        // Đảo ngược thứ tụ màu
        int[] colors = new int[barEntries.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = chartColors[colors.length - 1 - i];
        }
        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(Color.BLUE);
        barDataSet.setValueTextSize(12);
        barDataSet.setDrawValues(true);

        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return showValue(barEntry.getY());
            }
        });

        horizontalBarChart.getXAxis().setEnabled(false);
        BarData barData = new BarData(barDataSet);
        barData.setHighlightEnabled(true);
        barData.setValueTextColor(Color.BLACK);
        barData.setBarWidth(0.7f);
        barData.setValueTypeface(tfRegular);
        horizontalBarChart.setData(barData);


        horizontalBarChart.animateY(1500);
        horizontalBarChart.invalidate();
    }

    private String showValue(float value) {
        if (value >= 1e9) return toB(value);
        else if (value >= 1e6) return toM(value);
        return toK(value);
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
                binding.setDate(range);
                update();
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