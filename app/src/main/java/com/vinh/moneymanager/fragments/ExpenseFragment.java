package com.vinh.moneymanager.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vinh.moneymanager.R;
import com.vinh.moneymanager.activities.AddEditCategoryActivity;
import com.vinh.moneymanager.activities.AddEditFinanceActivity;
import com.vinh.moneymanager.activities.SearchActivity;
import com.vinh.moneymanager.adapters.ExpandCategoryAdapter;
import com.vinh.moneymanager.adapters.ExpandTimeAdapter;
import com.vinh.moneymanager.adapters.FragmentFinanceStateAdapter;
import com.vinh.moneymanager.adapters.RecyclerWeekAdapter;
import com.vinh.moneymanager.components.SingleChoice;
import com.vinh.moneymanager.databinding.FragmentExpenseBinding;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.DialogWeek;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.listeners.OnItemCategoryListener;
import com.vinh.moneymanager.listeners.OnItemFinanceListener;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.viewmodels.CategoryFinanceViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExpenseFragment extends Fragment implements SingleChoice.OnChoiceSelectedListener,
        RecyclerWeekAdapter.OnItemWeekClickListener,
        OnItemCategoryListener, OnItemFinanceListener {

    private static ExpenseFragment instance;

    private final int MODE_CATEGORY = 0;
    private final int MODE_TIME = 1;
    private int mode = MODE_CATEGORY;

    private LinearLayout layoutBottomSheet, mainLayout;
    private BottomSheetBehavior sheetBehavior;

    private final Map<Category, List<Finance>> mapIncome = new TreeMap<>((c1, c2) -> c1.getCategoryId() - c2.getCategoryId());
    private final Map<Category, List<Finance>> mapExpense = new TreeMap<>((c1, c2) -> c1.getCategoryId() - c2.getCategoryId());

    private final Map<String, List<Finance>> mapTimeIncome = new TreeMap<>((o1, o2) -> {
        DateRange.Date d1 = new DateRange.Date(o1);
        DateRange.Date d2 = new DateRange.Date(o2);
        return d1.compare(d2);
    });
    private final Map<String, List<Finance>> mapTimeExpense = new TreeMap<>((o1, o2) -> {
        DateRange.Date d1 = new DateRange.Date(o1);
        DateRange.Date d2 = new DateRange.Date(o2);
        return d1.compare(d2);
    });

    // Expand List hiển thị các khoản chi tiêu theo danh mục và thời gian
    private ExpandableListView expandListView;
    // Adapter cho Expand ListView
    private ExpandCategoryAdapter expandFinanceAdapter;
    private ExpandTimeAdapter expandTimeAdapter;

    private final Calendar calendar;
    private DialogWeek dialogWeek;

    private DateHandlerClick dateHandlerClick;

    // Main View Model
    private CategoryFinanceViewModel mViewModel;

    private TabLayout tabLayout;
    private FloatingActionButton fabListFinances;
    private ViewPager2 viewPager;
    private FragmentFinanceStateAdapter pagerAdapter;
    private ChipGroup chipGroup;
    private Chip chipIncome, chipExpense;

    private ImageView imgExpandCollapse, imgSearch;
    private boolean isExpandedAll = false;

    private ListCategoryFragment listIncomeFragment, listExpenseFragment;
    private final List<Category> allCategories = new ArrayList<>();

    public ExpenseFragment() {
        calendar = Calendar.getInstance();
    }

    public static ExpenseFragment getInstance() {
        if (instance == null) {
            instance = new ExpenseFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(getActivity()).get(CategoryFinanceViewModel.class);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        // Mặc định là MODE_MONTH
        DateRange range = new DateRange(DateRange.MODE_MONTH,
                new DateRange.Date(1, month, year),
                new DateRange.Date(DateRange.getLastDay(month, year), month, year));

        mViewModel.dateRange.set(range);

        mViewModel.initLiveData(getActivity(), getActivity());

        dateHandlerClick = new DateHandlerClick();
        dialogWeek = new DialogWeek(getContext(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR), this);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Fragment singleChoiceFragment = new SingleChoice(this, mViewModel.dateRange.get().getMode());
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_single_choice, singleChoiceFragment).commit();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentExpenseBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_expense, container, false);
        View view = binding.getRoot();

        binding.setDateHandler(dateHandlerClick);
        binding.setViewModel(mViewModel);

        initExpandListFinances(view);
        initViewPager(view);
        setLayoutBottomSheet(view);
        imgSearch = view.findViewById(R.id.img_search);
        imgSearch.setOnClickListener(v -> {
            Intent in = new Intent(getActivity(), SearchActivity.class);
            startActivity(in);
        });


        mViewModel.getMapTimeFinance().observe(getActivity(), timeListMap -> {
            mapTimeIncome.clear();
            mapTimeExpense.clear();

            Category category;

            for (String date : timeListMap.keySet()) {
                for (Finance f : timeListMap.get(date)) {
                    category = getCategory(f.getCategoryId());

                    if (category.getType() == Helper.TYPE_INCOME) {
                        if (!mapTimeIncome.containsKey(date)) {
                            mapTimeIncome.put(date, new ArrayList<>());
                        }
                        mapTimeIncome.get(date).add(f);
                    } else if (category.getType() == Helper.TYPE_EXPENSE) {
                        if (!mapTimeExpense.containsKey(date)) {
                            mapTimeExpense.put(date, new ArrayList<>());
                        }
                        mapTimeExpense.get(date).add(f);
                    }
                }
            }

            expandTimeAdapter.setCategories(allCategories);
            if (mViewModel.switchExpenseIncome.get() == Helper.TYPE_INCOME) {
                expandTimeAdapter.setMapFinance(mapTimeIncome);
            } else {
                expandTimeAdapter.setMapFinance(mapTimeExpense);
            }
        });

        mViewModel.getMapCategoryFinance().observe(getActivity(), categoryListMap -> {
            allCategories.clear();
            allCategories.addAll(categoryListMap.keySet());

            mapIncome.clear();
            mapExpense.clear();

            long totalIncome = 0;
            long totalExpense = 0;

            for (Category c : allCategories) {
                if (c.getType() == Helper.TYPE_INCOME) {
                    mapIncome.put(c, new ArrayList<>());
                    for (Finance f : categoryListMap.get(c)) {
                        mapIncome.get(c).add(f);
                        if (c.getType() == Helper.TYPE_INCOME) {
                            totalIncome += f.getMoney();
                        }
                    }
                } else if (c.getType() == Helper.TYPE_EXPENSE) {
                    mapExpense.put(c, new ArrayList<>());
                    for (Finance f : categoryListMap.get(c)) {
                        mapExpense.get(c).add(f);
                        if (c.getType() == Helper.TYPE_EXPENSE) {
                            totalExpense += f.getMoney();
                        }
                    }
                }

            }

            mViewModel.totalCostIncome.set(totalIncome);
            mViewModel.totalCostExpense.set(totalExpense);

            listIncomeFragment.notifyData(mapIncome);
            listExpenseFragment.notifyData(mapExpense);

            if (mViewModel.switchExpenseIncome.get() == Helper.TYPE_INCOME) {
                expandFinanceAdapter.setMapFinance(mapIncome);
            } else {
                expandFinanceAdapter.setMapFinance(mapExpense);
            }

        });

        mViewModel.getAccounts().observe(getActivity(), accounts -> {
            expandFinanceAdapter.setAccounts(accounts);
            expandTimeAdapter.setAccounts(accounts);
        });
        return view;
    }

    private void initViewPager(View view) {
        viewPager = view.findViewById(R.id.view_pager_expense_income);
        listIncomeFragment = new ListCategoryFragment(this, this);
        listIncomeFragment.setTitle("Thêm khoản thu nhập");
        listExpenseFragment = new ListCategoryFragment(this, this);
        listExpenseFragment.setTitle("Thêm khoản chi tiêu");

        pagerAdapter = new FragmentFinanceStateAdapter(this);
        pagerAdapter.setFragment(listIncomeFragment, listExpenseFragment);

        viewPager.setAdapter(pagerAdapter);


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ((Chip) (chipGroup.getChildAt(position))).setChecked(true);
                mViewModel.switchExpenseIncome.set(position + 1);
            }
        });


        chipGroup = view.findViewById(R.id.chipGroup);
        chipIncome = view.findViewById(R.id.chipIncome);
        chipExpense = view.findViewById(R.id.chipExpense);

        chipIncome.setOnClickListener(v -> {
            if (mViewModel.switchExpenseIncome.get() != Helper.TYPE_INCOME) {
                mViewModel.switchExpenseIncome.set(Helper.TYPE_INCOME);

                viewPager.setCurrentItem(0, true);
                pagerAdapter.notifyItemChanged(0);

                expandFinanceAdapter.setMapFinance(mapIncome);
                expandTimeAdapter.setMapFinance(mapTimeIncome);
            }
            chipIncome.setChecked(true);
        });

        chipExpense.setOnClickListener(v -> {
            if (mViewModel.switchExpenseIncome.get() != Helper.TYPE_EXPENSE) {
                mViewModel.switchExpenseIncome.set(Helper.TYPE_EXPENSE);

                viewPager.setCurrentItem(1, true);
                pagerAdapter.notifyItemChanged(1);

                expandFinanceAdapter.setMapFinance(mapExpense);
                expandTimeAdapter.setMapFinance(mapTimeExpense);
            }
            chipExpense.setChecked(true);
        });
    }

    private void setLayoutBottomSheet(View view) {
        mainLayout = view.findViewById(R.id.main_layout);
        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setDraggable(true);

        fabListFinances = view.findViewById(R.id.fab_list_finance);
        fabListFinances.setOnClickListener(v -> {

            if (getBottomSheetState() == BottomSheetBehavior.STATE_EXPANDED) {
                setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
//                fabListFinances.setImageResource(R.drawable.ic_fab_list);
            } else if (getBottomSheetState() == BottomSheetBehavior.STATE_COLLAPSED) {
                setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
//                fabListFinances.setImageResource(R.drawable.ic_close);
            }
        });

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    fabListFinances.setVisibility(View.INVISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    fabListFinances.setImageResource(R.drawable.ic_fab_list);
                    fabListFinances.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        initTabLayout(view);
    }

    public int getBottomSheetState() {
        return sheetBehavior.getState();
    }

    public void setBottomSheetState(int state) {
        if (getBottomSheetState() != state) {
            sheetBehavior.setState(state);
            if (state == BottomSheetBehavior.STATE_EXPANDED) {
                fabListFinances.setImageResource(R.drawable.ic_close);
                fabListFinances.setVisibility(View.INVISIBLE);
            } else if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                fabListFinances.setImageResource(R.drawable.ic_fab_list);
                fabListFinances.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initTabLayout(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        imgExpandCollapse = view.findViewById(R.id.img_expand_collapse);
        imgExpandCollapse.setImageResource(isExpandedAll ? R.drawable.ic_collapse : R.drawable.ic_expand);

        setMarginTabItem(tabLayout);    // tab item margin
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() != mode) {
                    mode = tab.getPosition();
                    if (mode == MODE_CATEGORY) {
                        expandListView.setAdapter(expandFinanceAdapter);
                    } else if (mode == MODE_TIME) {
                        expandListView.setAdapter(expandTimeAdapter);
                    }
                    expandCollapseGroup();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imgExpandCollapse.setOnClickListener(v -> {
            isExpandedAll = !isExpandedAll;
            imgExpandCollapse.setImageResource(isExpandedAll ? R.drawable.ic_collapse : R.drawable.ic_expand);

            expandCollapseGroup();
        });
    }

    private void expandCollapseGroup() {
        int size = 0;
        if (mode == MODE_CATEGORY) size = expandFinanceAdapter.getGroupCount();
        else if (mode == MODE_TIME) size = expandTimeAdapter.getGroupCount();

        if (isExpandedAll) {
            for (int i = 0; i < size; i++)
                expandListView.expandGroup(i);
        } else {
            for (int i = 0; i < size; i++)
                expandListView.collapseGroup(i);
        }
    }

    private void setMarginTabItem(TabLayout tabLayout) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins((int) Helper.convertDpToPixel(20, getContext()), 0, (int) Helper.convertDpToPixel(20, getContext()), 0);
            tab.requestLayout();
        }
    }

    private void initExpandListFinances(View view) {

        expandListView = view.findViewById(R.id.expandable_list_category_finance);
        expandFinanceAdapter = new ExpandCategoryAdapter(this.getContext(), new ArrayList<>(), new TreeMap<>(), this);
        expandTimeAdapter = new ExpandTimeAdapter(this.getContext(), new ArrayList<>(), new TreeMap<>(), new ArrayList<>(), this);

        if (mode == MODE_CATEGORY) {
            expandListView.setAdapter(expandFinanceAdapter);
        } else if (mode == MODE_TIME) {
            expandListView.setAdapter(expandTimeAdapter);
        }

        // ------------ Group Click -----------------
        expandListView.setOnGroupExpandListener(groupPosition -> {

        });

    }

    private Category getCategory(int categoryId) {
        for (Category c : allCategories) {
            if (c.getCategoryId() == categoryId) return c;
        }
        return null;
    }


    @Override
    public void onChoiceClick(int index) {
        DateRange range = mViewModel.dateRange.get();
        if (index == range.getMode()) return;
        Calendar calendar = Calendar.getInstance();

        range.setMode(index);
        switch (range.getMode()) {
            case DateRange.MODE_DAY:
                range.setStartDate(DateRange.nowDate);
                range.setEndDate(DateRange.nowDate);
                break;

            case DateRange.MODE_WEEK:
                int week = calendar.get(Calendar.WEEK_OF_YEAR);
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) week--;
                range.setWeek(week, calendar.get(Calendar.YEAR));
                break;

            case DateRange.MODE_MONTH:
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int endDayInMonth = DateRange.getLastDay(month, year);

                range.setStartDate(new DateRange.Date(1, month, year));
                range.setEndDate(new DateRange.Date(endDayInMonth, month, year));
                break;
        }

        mViewModel.setDateRangeValue(range);
    }

    @Override
    public void onItemWeekClick(DateRange dateWeek) {
        mViewModel.setDateRangeValue(dateWeek);
        dialogWeek.hideDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onCategoryClick(Category category, int position) {
        Log.d("MMM", category.getCategoryId() + " : " + category.getName() + " : " + position);
        System.out.println("MMM abc: " + category.getCategoryId() + " : " + category.getName() + " : " + position);
        Bundle bundle = new Bundle();
        bundle.putInt(Helper.CATEGORY_ID, category.getCategoryId());
        bundle.putString(Helper.CATEGORY_NAME, category.getName());
        bundle.putInt(Helper.CATEGORY_TYPE, category.getType());
        bundle.putInt(Helper.ACCOUNT_ID, 0);

        Intent intent = new Intent(getActivity(), AddEditFinanceActivity.class);
        intent.putExtra(Helper.ADD_FINANCE, bundle);

        startActivityForResult(intent, Helper.REQUEST_ADD_FINANCE);
    }

    @Override
    public void onCategoryLongClick(Category category, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(Helper.CATEGORY_ID, category.getCategoryId());
        bundle.putInt(Helper.CATEGORY_TYPE, category.getType());
        bundle.putString(Helper.CATEGORY_NAME, category.getName());
        bundle.putString(Helper.CATEGORY_DESCRIPTION, category.getDescription());
        bundle.putInt(Helper.CATEGORY_ICON, category.getIcon());

        Intent intent = new Intent(getActivity(), AddEditCategoryActivity.class);
        intent.putExtra(Helper.EDIT_CATEGORY, bundle);

        startActivityForResult(intent, Helper.REQUEST_EDIT_CATEGORY);
    }

    @Override
    public void onCategoryAdd() {
        Bundle bundle = new Bundle();
        bundle.putInt(Helper.CATEGORY_TYPE, mViewModel.switchExpenseIncome.get());

        Intent intent = new Intent(getActivity(), AddEditCategoryActivity.class);
        intent.putExtra(Helper.ADD_CATEGORY, bundle);

        startActivityForResult(intent, Helper.REQUEST_ADD_CATEGORY);
    }

    @Override
    public void onFinanceClick(Finance finance, Category category) {
        Bundle dataSend = new Bundle();

        dataSend.putInt(Helper.FINANCE_ID, finance.getFinanceId());
        dataSend.putInt(Helper.CATEGORY_ID, finance.getCategoryId());
        dataSend.putString(Helper.CATEGORY_NAME, category.getName());
        dataSend.putInt(Helper.CATEGORY_TYPE, category.getType());
        dataSend.putInt(Helper.ACCOUNT_ID, finance.getAccountId());
        dataSend.putString(Helper.FINANCE_DATETIME, finance.getDateTime());
        dataSend.putLong(Helper.FINANCE_COST, finance.getMoney());
        dataSend.putString(Helper.FINANCE_DETAIL, finance.getDetail());

        Intent intent = new Intent(getActivity(), AddEditFinanceActivity.class);
        intent.putExtra(Helper.EDIT_FINANCE, dataSend);

        startActivityForResult(intent, Helper.REQUEST_EDIT_FINANCE);
    }


    public class DateHandlerClick {

        public void onDateClick(View v) {
            DateRange range = mViewModel.dateRange.get();

            switch (range.getMode()) {
                case DateRange.MODE_DAY:
                    dialogChooseDay(range);
                    break;
                case DateRange.MODE_WEEK:
                    dialogWeek.setMonthYear(range.getStartDate().getMonth(), range.getStartDate().getYear());
                    dialogWeek.showDialog();
                    break;
                case DateRange.MODE_MONTH:
                    dialogChooseMonth(range);
                    break;
            }
        }

        private void dialogChooseDay(DateRange range) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseFragment.this.getContext(),
                    (view, year, month, dayOfMonth) -> {
                        range.setStartDate(new DateRange.Date(dayOfMonth, month + 1, year));
                        range.setEndDate(new DateRange.Date(dayOfMonth, month + 1, year));
                        mViewModel.setDateRangeValue(range);
                    },
                    range.getStartDate().getYear(),
                    range.getStartDate().getMonth() - 1,
                    range.getStartDate().getDay());
            datePickerDialog.show();
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
                mViewModel.setDateRangeValue(range);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExpenseFragment.this.getContext(), R.layout.item_text_center, list);
            gridMonth.setAdapter(adapter);

            gridMonth.setOnItemClickListener((parent, view, position, id) -> {
                int year = Integer.parseInt(tvYear.getText().toString());
                range.setStartDate(new DateRange.Date(1, position + 1, year));
                range.setEndDate(new DateRange.Date(DateRange.getLastDay(position + 1, year), position + 1, year));
                mViewModel.setDateRangeValue(range);
                dialog.cancel();
            });


            dialog.show();
        }
    }
}