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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.vinh.moneymanager.libs.Define;
import com.vinh.moneymanager.libs.DialogWeek;
import com.vinh.moneymanager.models.DateRangeModel;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.viewmodels.CategoryViewModel;
import com.vinh.moneymanager.viewmodels.FinanceViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.app.Activity.RESULT_OK;

public class ExpenseFragment extends Fragment implements SingleChoice.OnChoiceSelectedListener, RecyclerWeekAdapter.OnItemWeekClickListener, GridCategoryAdapter.OnGridItemClickListener {

    private static ExpenseFragment instance;

    // GridView hiển thị các danh mục chi tiêu và số tiền đã chi tiêu
    private GridView gridViewCategories;
    private CategoryViewModel categoryViewModel;

    // Expand List hiển thị các khoản chi tiêu theo danh mục và thời gian
    private ExpandableListView expandableListCategoryFinance;
    private ExpandableListFinanceAdapter listCategoryFinanceAdapter;
    private FinanceViewModel financeViewModel;


    private View viewControlBottomSheet;

    // List các danh mục
    private List<Category> categories = new ArrayList<>();
    // Map các khoản chi tiêu theo danh mục
    private Map<Category, List<Finance>> mapFinance;

    private Calendar calendar;
    private DialogWeek dialogWeek;

    private DateRangeModel dateRangeModel;
    private DateHandlerClick dateHandlerClick;


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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Fragment singleChoiceFragment = new SingleChoice(this, 2);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_single_choice, singleChoiceFragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentExpenseBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_expense, container, false);
        View view = binding.getRoot();

        dateRangeModel = new DateRangeModel();
        dateRangeModel.dateRange.set(new DateRange());
        binding.setDateRangeModel(dateRangeModel);
        dateHandlerClick = new DateHandlerClick();
        binding.setDateHandler(dateHandlerClick);

        dialogWeek = new DialogWeek(getContext(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR), this);


        // category view model
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        gridViewCategories = view.findViewById(R.id.grid_view_category);
        gridViewCategories.setAdapter(new GridCategoryAdapter(this.getContext(), categories, this));

        categoryViewModel.getExpenseCategories().observe(this.getActivity(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                ExpenseFragment.this.categories = categories;
                gridViewCategories.setAdapter(new GridCategoryAdapter(ExpenseFragment.this.getContext(), categories, ExpenseFragment.this));
            }
        });


        setLayoutBottomSheet(view);
        initExpandableList(view);

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

    private void initExpandableList(View view) {
        mapFinance = new TreeMap<>((c1, c2) -> c1.getId() - c2.getId());

        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);

        financeViewModel.getAllFinances().observe(this.getActivity(), finances -> {
            mapFinance.clear();

            for (Category c : categories) {
                mapFinance.put(c, new ArrayList<>());
            }

            for (Finance f : finances) {
                for (Category c : mapFinance.keySet()) {
                    if (f.getCategoryId() == c.getId()) {
                        mapFinance.get(c).add(f);
                    }
                }
            }

            for (Category c : mapFinance.keySet()) {
                for (Finance f : mapFinance.get(c)) {
                    System.out.println(f.getId() + " " + f.getDetail());
                }
            }

            listCategoryFinanceAdapter.setMapFinance(mapFinance);
        });

        expandableListCategoryFinance = view.findViewById(R.id.expandable_list_category_finance);
        listCategoryFinanceAdapter = new ExpandableListFinanceAdapter(this.getContext(), categories, mapFinance);
        expandableListCategoryFinance.setAdapter(listCategoryFinanceAdapter);

        expandableListCategoryFinance.setOnGroupExpandListener(groupPosition -> {

        });

        expandableListCategoryFinance.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            System.out.println(mapFinance.get(categories.get(groupPosition)).get(childPosition).getDetail());

            Category c = categories.get(groupPosition);
            Finance f = mapFinance.get(c).get(childPosition);
            Intent intent = new Intent(getActivity(), AddEditFinanceActivity.class);

            intent.putExtra(Define.FINANCE_ID, f.getId());
            intent.putExtra(Define.CATEGORY_ID, c.getId());
            intent.putExtra(Define.CATEGORY_NAME, c.getName());

            intent.putExtra(Define.ACCOUNT_ID, 1);
            intent.putExtra(Define.ACCOUNT_NAME, "Tiền mặt");

            intent.putExtra(Define.FINANCE_DATETIME, f.getDateTime());
            intent.putExtra(Define.FINANCE_COST, f.getCost());
            intent.putExtra(Define.FINANCE_DETAIL, f.getDetail());

            startActivityForResult(intent, Define.REQUEST_EDIT_FINANCE);
            return false;
        });
    }


    @Override
    public void onChoiceClick(int index) {
        DateRange range = dateRangeModel.dateRange.get();
        if (index == range.getMode()) return;
        Calendar calendar = Calendar.getInstance();

        range.setMode(index);
        switch (range.getMode()) {
            case DateRange.MODE_DAY:
                range.setStartDate(DateRange.nowDate);
                break;
            case DateRange.MODE_WEEK:
                int week = calendar.get(Calendar.WEEK_OF_YEAR);
                range.setWeek(week, calendar.get(Calendar.YEAR));
                break;
            case DateRange.MODE_MONTH:
                range.setStartDate(new DateRange.Date(1, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
                break;
        }
        dateRangeModel.setDateRangeValue(range);
    }

    @Override
    public void onItemWeekClick(DateRange dateWeek) {
        dateRangeModel.setDateRangeValue(dateWeek);
        dialogWeek.hideDialog();
    }

    @Override
    public void onGridItemClick(int position, boolean isCategory) {
        if (isCategory) {
            Intent intent = new Intent(getActivity(), AddEditFinanceActivity.class);
            intent.putExtra(Define.CATEGORY_ID, categories.get(position).getId());
            intent.putExtra(Define.CATEGORY_NAME, categories.get(position).getName());

            intent.putExtra(Define.ACCOUNT_ID, 1);
            intent.putExtra(Define.ACCOUNT_NAME, "Tiền mặt");

            startActivityForResult(intent, Define.REQUEST_ADD_FINANCE);
        } else {
            Intent intent = new Intent(getActivity(), AddEditCategoryActivity.class);
            startActivityForResult(intent, Define.REQUEST_ADD_CATEGORY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Define.REQUEST_ADD_CATEGORY) {
            if (resultCode == RESULT_OK) {
                Category inputCategory = new Category(data.getStringExtra("name"),
                        data.getIntExtra("type", 0),
                        data.getStringExtra("description"));

                categoryViewModel.insert(inputCategory);
            }
        } else if (requestCode == Define.REQUEST_ADD_FINANCE) {
            if (resultCode == RESULT_OK) {
                Finance inputFinance = new Finance(data.getLongExtra(Define.FINANCE_COST, 0),
                        data.getStringExtra(Define.FINANCE_DATETIME),
                        data.getStringExtra(Define.FINANCE_DETAIL),
                        data.getIntExtra(Define.CATEGORY_ID, 0),
                        data.getIntExtra(Define.ACCOUNT_ID, 0));

                financeViewModel.insert(inputFinance);
                System.out.println("Them khoan chi tieu thanh cong!");
            }
        } else if (requestCode == Define.REQUEST_EDIT_FINANCE) {
            if (resultCode == RESULT_OK) {
                Finance updateFinance = new Finance(data.getLongExtra(Define.FINANCE_COST, 0),
                        data.getStringExtra(Define.FINANCE_DATETIME),
                        data.getStringExtra(Define.FINANCE_DETAIL),
                        data.getIntExtra(Define.CATEGORY_ID, 0),
                        data.getIntExtra(Define.ACCOUNT_ID, 0));
                updateFinance.setId(data.getIntExtra(Define.FINANCE_ID, -1));
                System.out.println("ID Finance: " + updateFinance.getId());
                financeViewModel.update(updateFinance);
                System.out.println("Cap nhat khoan chi tieu thanh cong!");
            }
        }
    }

    public class DateHandlerClick {

        public void onDateClick(View v) {
            DateRange range = dateRangeModel.dateRange.get();

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
                        dateRangeModel.setDateRangeValue(range);
//                        tvDate.setText(dateRange1.getDateString());
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
                    range.setStartDate(new DateRange.Date(1, position + 1, Integer.parseInt(tvYear.getText().toString())));
                    dateRangeModel.setDateRangeValue(range);
//                tvDate.setText(dateRange1.getDateString());
                    dialog.cancel();
                }
            });


            dialog.show();
        }
    }
}