package com.vinh.moneymanager.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.vinh.moneymanager.R;
import com.vinh.moneymanager.activities.AddEditCategoryActivity;
import com.vinh.moneymanager.activities.AddEditFinanceActivity;
import com.vinh.moneymanager.adapters.ExpandableListFinanceAdapter;
import com.vinh.moneymanager.adapters.GridCategoryAdapter;
import com.vinh.moneymanager.adapters.RecyclerWeekAdapter;
import com.vinh.moneymanager.components.SingleChoice;
import com.vinh.moneymanager.databinding.FragmentExpenseBinding;
import com.vinh.moneymanager.libs.DateRange;
import com.vinh.moneymanager.libs.DialogWeek;
import com.vinh.moneymanager.libs.Helper;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.viewmodels.CategoryFinanceViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import static android.app.Activity.RESULT_OK;

public class ExpenseFragment extends Fragment implements SingleChoice.OnChoiceSelectedListener,
        RecyclerWeekAdapter.OnItemWeekClickListener,
        GridCategoryAdapter.OnGridItemClickListener, GridCategoryAdapter.OnGridItemLongClickListener {

    private static ExpenseFragment instance;

    // GridView hiển thị các danh mục chi tiêu và số tiền đã chi tiêu
    private GridView gridViewCategories;
    private GridCategoryAdapter categoryAdapter;
//    private CategoryViewModel categoryViewModel;

    // Expand List hiển thị các khoản chi tiêu theo danh mục và thời gian
    private ExpandableListView expandableListCategoryFinance;
    private ExpandableListFinanceAdapter financeAdapter;
//    private FinanceViewModel financeViewModel;


    private View viewControlBottomSheet;

    private Calendar calendar;
    private DialogWeek dialogWeek;

    private DateHandlerClick dateHandlerClick;
    private CategoryFinanceViewModel mViewModel;

    LinearLayout layoutBottomSheet, mainLayout;
    BottomSheetBehavior sheetBehavior;

    TabLayout tabLayout;

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

        mViewModel = new CategoryFinanceViewModel(this, this);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        // Mặc định là MODE_MONTH
        DateRange range = new DateRange(DateRange.MODE_MONTH,
                new DateRange.Date(1, month, year),
                new DateRange.Date(DateRange.getLastDay(month, year), month, year));

        mViewModel.dateRange.set(range);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentExpenseBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_expense, container, false);
        View view = binding.getRoot();

        binding.setDateHandler(dateHandlerClick);
        binding.setViewModel(mViewModel);

        initExpandListFinances(view);
        initGridCategories(view);
        setLayoutBottomSheet(view);

        return view;
    }


    private void setLayoutBottomSheet(View view) {
        mainLayout = view.findViewById(R.id.main_layout);
        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        viewControlBottomSheet = view.findViewById(R.id.icon_control_bottom_sheet);

        viewControlBottomSheet.setOnClickListener((v) -> {
            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                sheetBehavior.setDraggable(true);
                viewControlBottomSheet.setBackgroundResource(R.drawable.ic_line);
            } else if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                sheetBehavior.setDraggable(false);
                viewControlBottomSheet.setBackgroundResource(R.drawable.ic_close);
            }
        });

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        viewControlBottomSheet.setBackgroundResource(R.drawable.ic_line);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        sheetBehavior.setDraggable(false);
                        viewControlBottomSheet.setBackgroundResource(R.drawable.ic_close);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        initTabLayout(view);
    }

    private void initTabLayout(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        setMarginTabItem(tabLayout);    // tab item margin
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setMarginTabItem(TabLayout tabLayout) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(50, 0, 50, 0);
            tab.requestLayout();
        }
    }

    private void initExpandListFinances(View view) {

        expandableListCategoryFinance = view.findViewById(R.id.expandable_list_category_finance);
        financeAdapter = new ExpandableListFinanceAdapter(this.getContext(), new ArrayList<Category>(), new TreeMap<Category, List<Finance>>());
        expandableListCategoryFinance.setAdapter(financeAdapter);

        expandableListCategoryFinance.setOnGroupExpandListener(groupPosition -> {

        });

        expandableListCategoryFinance.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            System.out.println(mViewModel.getMapFinance().getValue().get(mViewModel.getCategories().getValue().get(groupPosition)).get(childPosition).getDetail());

            Category c = mViewModel.getCategories().getValue().get(groupPosition);
            Finance f = mViewModel.getMapFinance().getValue().get(c).get(childPosition);
            Intent intent = new Intent(getActivity(), AddEditFinanceActivity.class);

            intent.putExtra(Helper.FINANCE_ID, f.getId());
            intent.putExtra(Helper.CATEGORY_ID, c.getId());
            intent.putExtra(Helper.CATEGORY_NAME, c.getName());

            intent.putExtra(Helper.ACCOUNT_ID, 1);
            intent.putExtra(Helper.ACCOUNT_NAME, "Tiền mặt");

            intent.putExtra(Helper.FINANCE_DATETIME, f.getDateTime());
            intent.putExtra(Helper.FINANCE_COST, f.getCost());
            intent.putExtra(Helper.FINANCE_DETAIL, f.getDetail());

            startActivityForResult(intent, Helper.REQUEST_EDIT_FINANCE);
            return false;
        });
    }

    private void initGridCategories(View view) {
        gridViewCategories = view.findViewById(R.id.grid_view_category);
        categoryAdapter = new GridCategoryAdapter(this.getContext(), new TreeMap<Category, List<Finance>>(), this, this);
        gridViewCategories.setAdapter(categoryAdapter);

        mViewModel.getCategories().observe(this.getViewLifecycleOwner(), categories -> {
            //categoryAdapter.setCategories(categories);
        });
        mViewModel.getMapFinance().observe(this.getViewLifecycleOwner(), categoryListMap -> {
            financeAdapter.setMapFinance(categoryListMap);
            categoryAdapter.setMapFinance(categoryListMap);
        });

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
    public void onGridItemClick(int position, boolean isCategory) {
        if (isCategory) {
            Intent intent = new Intent(getActivity(), AddEditFinanceActivity.class);
            intent.putExtra(Helper.CATEGORY_ID, mViewModel.getCategories().getValue().get(position).getId());
            intent.putExtra(Helper.CATEGORY_NAME, mViewModel.getCategories().getValue().get(position).getName());

            intent.putExtra(Helper.ACCOUNT_ID, 1);
            intent.putExtra(Helper.ACCOUNT_NAME, "Tiền mặt");

            startActivityForResult(intent, Helper.REQUEST_ADD_FINANCE);
        } else {
            Intent intent = new Intent(getActivity(), AddEditCategoryActivity.class);
            startActivityForResult(intent, Helper.REQUEST_ADD_CATEGORY);
        }
    }

    @Override
    public void onGridItemLongClick(int position, boolean isCategory) {
        if(isCategory){
            Intent intent = new Intent(getActivity(), AddEditCategoryActivity.class);
            intent.putExtra(Helper.CATEGORY_ID, mViewModel.getCategories().getValue().get(position).getId());
            intent.putExtra(Helper.CATEGORY_TYPE, mViewModel.getCategories().getValue().get(position).getType());
            intent.putExtra(Helper.CATEGORY_NAME, mViewModel.getCategories().getValue().get(position).getName());
            intent.putExtra(Helper.CATEGORY_DESCRIPTION, mViewModel.getCategories().getValue().get(position).getDescription());

            startActivityForResult(intent, Helper.REQUEST_EDIT_CATEGORY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Helper.REQUEST_ADD_CATEGORY) {
            if (resultCode == RESULT_OK) {
                Category inputCategory = new Category(data.getStringExtra(Helper.CATEGORY_NAME),
                        data.getIntExtra(Helper.CATEGORY_TYPE, 0),
                        data.getStringExtra(Helper.CATEGORY_DESCRIPTION));

                mViewModel.categoryViewModel.insert(inputCategory);
            }
        } else if(requestCode == Helper.REQUEST_EDIT_CATEGORY){
            if (resultCode == RESULT_OK) {
                Category updateCategory = new Category(data.getStringExtra(Helper.CATEGORY_NAME),
                        data.getIntExtra(Helper.CATEGORY_TYPE, 0),
                        data.getStringExtra(Helper.CATEGORY_DESCRIPTION));
                updateCategory.setId(data.getIntExtra(Helper.CATEGORY_ID, -1));

                mViewModel.categoryViewModel.update(updateCategory);
            }
        }else if (requestCode == Helper.REQUEST_ADD_FINANCE) {
            if (resultCode == RESULT_OK) {
                Finance inputFinance = new Finance(data.getLongExtra(Helper.FINANCE_COST, 0),
                        data.getStringExtra(Helper.FINANCE_DATETIME),
                        data.getStringExtra(Helper.FINANCE_DETAIL),
                        data.getIntExtra(Helper.CATEGORY_ID, 0),
                        data.getIntExtra(Helper.ACCOUNT_ID, 0));

                mViewModel.financeViewModel.insert(inputFinance);
                System.out.println("Them khoan chi tieu thanh cong!");
            }
        } else if (requestCode == Helper.REQUEST_EDIT_FINANCE) {
            if (resultCode == RESULT_OK) {
                Finance updateFinance = new Finance(data.getLongExtra(Helper.FINANCE_COST, 0),
                        data.getStringExtra(Helper.FINANCE_DATETIME),
                        data.getStringExtra(Helper.FINANCE_DETAIL),
                        data.getIntExtra(Helper.CATEGORY_ID, 0),
                        data.getIntExtra(Helper.ACCOUNT_ID, 0));
                updateFinance.setId(data.getIntExtra(Helper.FINANCE_ID, -1));
                System.out.println("ID Finance: " + updateFinance.getId());
                mViewModel.financeViewModel.update(updateFinance);
                System.out.println("Cap nhat khoan chi tieu thanh cong!");
            }
        }
    }




    public class DateHandlerClick {

        public void onDateClick(View v) {
            DateRange range = mViewModel.dateRange.get();

            switch (range.getMode()) {
                case DateRange.MODE_DAY:
                    dialogChooseDay(range);
                    break;
                case DateRange.MODE_WEEK:
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

            List<String> list = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                list.add("Th" + i);
            }

            TextView tvYear = dialog.findViewById(R.id.text_view_year);
            tvYear.setText(Calendar.getInstance().get(Calendar.YEAR) + "");

            View btnPrevious = dialog.findViewById(R.id.btn_previous_month);
            View btnNext = dialog.findViewById(R.id.btn_next_month);
            btnPrevious.setOnClickListener((v) -> {
                int year = Integer.parseInt(tvYear.getText().toString());
                tvYear.setText((year - 1) + "");
            });
            btnNext.setOnClickListener((v) -> {
                int year = Integer.parseInt(tvYear.getText().toString());
                tvYear.setText((year + 1) + "");
            });

            GridView gridMonth = dialog.findViewById(R.id.grid_view_month);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExpenseFragment.this.getContext(), android.R.layout.simple_list_item_1, list);
            gridMonth.setAdapter(adapter);

            gridMonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int year = Integer.parseInt(tvYear.getText().toString());
                    range.setStartDate(new DateRange.Date(1, position + 1, year));
                    range.setEndDate(new DateRange.Date(range.getLastDay(position+1, year), position + 1, year));
                    mViewModel.setDateRangeValue(range);
                    dialog.cancel();
                }
            });


            dialog.show();
        }
    }
}