package com.vinh.moneymanager.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.room.entities.Transfer;
import com.vinh.moneymanager.viewmodels.CategoryFinanceViewModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
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
    private FloatingActionButton fabSearch;
    private ViewPager2 viewPager;
    private FragmentFinanceStateAdapter pagerAdapter;
    private ChipGroup chipGroup;
    private Chip chipIncome, chipExpense;

    private ImageView imgExpandCollapse, imgDoubleArrow, imgExcel;
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

        settingDialog();
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

        imgExcel = view.findViewById(R.id.img_excel);
        imgExcel.setOnClickListener(v -> {
            showDialog();
//            List<Finance> finances = new ArrayList<>();
//            if (mViewModel.switchExpenseIncome.get() == Helper.TYPE_INCOME) {
//                for (String t : mapTimeIncome.keySet()) {
//                    finances.addAll(mapTimeIncome.get(t));
//                }
////                for (Category c : mapIncome.keySet()) {
////                    finances.addAll(mapIncome.get(c));
////                }
//            } else {
////                for (Category c : mapExpense.keySet()) {
////                    finances.addAll(mapExpense.get(c));
////                }
//                for (String t : mapTimeExpense.keySet()) {
//                    finances.addAll(mapTimeExpense.get(t));
//                }
//            }
//            createExcel(finances);
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
            allAccounts = accounts;
            expandFinanceAdapter.setAccounts(accounts);
            expandTimeAdapter.setAccounts(accounts);
        });
        return view;
    }

    private List<Account> allAccounts;

    private Account getAccount(int accountId) {
        for (Account account : allAccounts) {
            if (account.getAccountId() == accountId) return account;
        }
        return null;
    }

    private void prepareExcel() {
        List<Finance> financesIncome = new ArrayList<>();
        List<Finance> financesExpense = new ArrayList<>();
        List<Transfer> transfers = new ArrayList<>();

        if (isFilterIncome) {
            for (Category c : mViewModel.getAllFinances().keySet()) {
                if (c.getType() == Helper.TYPE_INCOME) {
                    if (isFilterAllTime) {
                        financesIncome.addAll(mViewModel.getAllFinances().get(c));
                    } else {
                        for (Finance f : mViewModel.getAllFinances().get(c)) {
                            String strDate = f.getDateTime().split("-")[0].trim();
                            if (inRange(new DateRange.Date(strDate), startDate, endDate)) {
                                financesIncome.add(f);
                            }
                        }
                    }
                }
            }
        }

        if (isFilterExpense) {
            for (Category c : mViewModel.getAllFinances().keySet()) {
                if (c.getType() == Helper.TYPE_EXPENSE) {
                    if (isFilterAllTime) {
                        financesExpense.addAll(mViewModel.getAllFinances().get(c));
                    } else {
                        for (Finance f : mViewModel.getAllFinances().get(c)) {
                            String strDate = f.getDateTime().split("-")[0].trim();
                            if (inRange(new DateRange.Date(strDate), startDate, endDate)) {
                                financesExpense.add(f);
                            }
                        }
                    }
                }
            }
        }

        if (isFilterTransfer) {
            if (isFilterAllTime) {
                transfers.addAll(mViewModel.getAllTransfer());
            } else {
                for (Transfer t : mViewModel.getAllTransfer()) {
                    String strDate = t.getDateTime().split("-")[0].trim();
                    if (inRange(new DateRange.Date(strDate), startDate, endDate)) {
                        transfers.add(t);
                    }
                }
            }
        }

        Helper.sortFinanceByTimeAsc(financesIncome);
        Helper.sortFinanceByTimeAsc(financesExpense);
        Helper.sortTransferByTimeAsc(transfers);

        createExcel(financesIncome, financesExpense, transfers);
    }

    private boolean inRange(DateRange.Date date, DateRange.Date start, DateRange.Date end) {
        if (date.compare(start) >= 0 && date.compare(end) <= 0) {
            return true;
        }
        if (date.compare(end) >= 0 && date.compare(start) <= 0) {
            return true;
        }
        return false;
    }

    private void createExcel(List<Finance> financesIncome, List<Finance> financesExpense, List<Transfer> transfers) {
        Calendar calendar = Calendar.getInstance();
        String fileName = "Data-";
        fileName += String.format("%d%d%d-%d%d%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));

        File filePath = new File(Environment.getExternalStorageDirectory() + "/" + fileName + ".xls");

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();

        HSSFSheet sheetIncome, sheetExpense, sheetTransfer;

        if (isFilterIncome) {
            sheetIncome = hssfWorkbook.createSheet("Thu nhập");

            HSSFRow hssfRow = sheetIncome.createRow(0);
            HSSFCell hssfCell = hssfRow.createCell(0);

            String[] title = new String[]{"STT", "Thời gian", "Danh mục", "Tài khoản", "Số tiền", "Ghi chú"};
            for (int i = 0; i < title.length; i++) {
                hssfRow.createCell(i).setCellValue(title[i]);
            }

            for (int i = 1; i <= financesIncome.size(); i++) {
                Finance f = financesIncome.get(i - 1);
                HSSFRow row = sheetIncome.createRow(i);

                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue(f.getDateTime());
                row.createCell(2).setCellValue(getCategory(f.getCategoryId()).getName());
                row.createCell(3).setCellValue(getAccount(f.getAccountId()).getAccountName());
                row.createCell(4).setCellValue(f.getMoney());
                row.createCell(5).setCellValue(f.getDetail());
            }
        }
        if (isFilterExpense) {
            sheetExpense = hssfWorkbook.createSheet("Chi tiêu");

            HSSFRow hssfRow = sheetExpense.createRow(0);
            HSSFCell hssfCell = hssfRow.createCell(0);

            String[] title = new String[]{"STT", "Thời gian", "Danh mục", "Tài khoản", "Số tiền", "Ghi chú"};
            for (int i = 0; i < title.length; i++) {
                hssfRow.createCell(i).setCellValue(title[i]);
            }

            for (int i = 1; i <= financesExpense.size(); i++) {
                Finance f = financesExpense.get(i - 1);
                HSSFRow row = sheetExpense.createRow(i);

                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue(f.getDateTime());
                row.createCell(2).setCellValue(getCategory(f.getCategoryId()).getName());
                row.createCell(3).setCellValue(getAccount(f.getAccountId()).getAccountName());
                row.createCell(4).setCellValue(f.getMoney());
                row.createCell(5).setCellValue(f.getDetail());

            }
        }
        if (isFilterTransfer) {
            sheetTransfer = hssfWorkbook.createSheet("Chuyển khoản");

            HSSFRow hssfRow = sheetTransfer.createRow(0);
            HSSFCell hssfCell = hssfRow.createCell(0);

            String[] title = new String[]{"STT", "Thời gian", "Tài khoản chuyển", "Tài khoản nhận", "Số tiền", "Phí", "Ghi chú"};
            for (int i = 0; i < title.length; i++) {
                hssfRow.createCell(i).setCellValue(title[i]);
            }

            for (int i = 1; i <= transfers.size(); i++) {
                Transfer t = transfers.get(i - 1);
                HSSFRow row = sheetTransfer.createRow(i);

                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue(t.getDateTime());
                row.createCell(2).setCellValue(getAccount(t.getAccountOutId()).getAccountName());
                row.createCell(3).setCellValue(getAccount(t.getAccountInId()).getAccountName());
                row.createCell(4).setCellValue(t.getMoney());
                row.createCell(5).setCellValue(t.getFee());
                row.createCell(6).setCellValue(t.getDetail());

            }
        }

        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(this.getContext(), "Tạo file excel thành công: " + fileName + ".xls", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), "Tạo file excel thất bại :((", Toast.LENGTH_SHORT).show();
        }
    }

    private Dialog dialog;
    private DateRange.Date startDate, endDate;
    private TextView tvStartDate, tvEndDate;

    private boolean isFilterIncome = true;
    private boolean isFilterExpense = true;
    private boolean isFilterTransfer = true;

    private boolean isFilterAllTime = true;

    private void showDialog() {
        startDate = mViewModel.dateRange.get().getStartDate();
        endDate = mViewModel.dateRange.get().getEndDate();

        tvStartDate.setText(String.format("%02d/%02d/%d", startDate.getDay(), startDate.getMonth(), startDate.getYear()));
        tvEndDate.setText(String.format("%02d/%02d/%d", endDate.getDay(), endDate.getMonth(), endDate.getYear()));

        dialog.show();
    }

    private void settingDialog() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_search_filter);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = dialog.findViewById(R.id.img_close_dialog);
        imgClose.setOnClickListener((v) -> dialog.cancel());

        CheckBox cbIncome = dialog.findViewById(R.id.cb_filter_income);
        CheckBox cbExpense = dialog.findViewById(R.id.cb_filter_expense);
        CheckBox cbTransfer = dialog.findViewById(R.id.cb_filter_transfer);

        RadioButton radioAllTime = dialog.findViewById(R.id.radio_all_time);
        RadioButton radioFixedTime = dialog.findViewById(R.id.radio_fixed_time);

        cbIncome.setOnCheckedChangeListener((buttonView, isChecked) -> isFilterIncome = isChecked);
        cbExpense.setOnCheckedChangeListener((buttonView, isChecked) -> isFilterExpense = isChecked);
        cbTransfer.setOnCheckedChangeListener((buttonView, isChecked) -> isFilterTransfer = isChecked);

        LinearLayout layoutFilterTime = dialog.findViewById(R.id.layout_filter_time);

        radioAllTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (radioAllTime.isChecked()) {
                layoutFilterTime.setVisibility(View.GONE);
            }
            isFilterAllTime = radioAllTime.isChecked();
        });

        radioFixedTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (radioFixedTime.isChecked()) {
                layoutFilterTime.setVisibility(View.VISIBLE);
            }
            isFilterAllTime = radioAllTime.isChecked();
        });

        Button btnApply = dialog.findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(v -> {
            prepareExcel();
            dialog.dismiss();
        });

        tvStartDate = dialog.findViewById(R.id.tv_start_date);
        tvEndDate = dialog.findViewById(R.id.tv_end_date);


        tvStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        tvStartDate.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
                        startDate = new DateRange.Date(dayOfMonth, month + 1, year);
                    },
                    startDate.getYear(),
                    startDate.getMonth() - 1,
                    startDate.getDay());
            datePickerDialog.show();
        });
        tvEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        tvEndDate.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
                        endDate = new DateRange.Date(dayOfMonth, month + 1, year);
                    },
                    endDate.getYear(),
                    endDate.getMonth() - 1,
                    endDate.getDay());
            datePickerDialog.show();
        });
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

                if (mViewModel.switchExpenseIncome.get() == Helper.TYPE_INCOME) {
                    expandFinanceAdapter.setMapFinance(mapIncome);
                    expandTimeAdapter.setMapFinance(mapTimeIncome);
                } else {
                    expandFinanceAdapter.setMapFinance(mapExpense);
                    expandTimeAdapter.setMapFinance(mapTimeExpense);
                }
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

        fabSearch = view.findViewById(R.id.fab_search);
        fabSearch.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SearchActivity.class));
//            if (getBottomSheetState() == BottomSheetBehavior.STATE_EXPANDED) {
//                setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
////                fabListFinances.setImageResource(R.drawable.ic_fab_list);
//            } else if (getBottomSheetState() == BottomSheetBehavior.STATE_COLLAPSED) {
//                setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
////                fabListFinances.setImageResource(R.drawable.ic_close);
//            }
        });

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    fabSearch.setVisibility(View.INVISIBLE);
                    imgDoubleArrow.setImageResource(R.drawable.double_down);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    fabSearch.setVisibility(View.VISIBLE);
                    imgDoubleArrow.setImageResource(R.drawable.double_up);
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
//            if (state == BottomSheetBehavior.STATE_EXPANDED) {
//                imgDoubleArrow.setImageResource(R.drawable.double_down);
//            } else if (state == BottomSheetBehavior.STATE_COLLAPSED) {
//                imgDoubleArrow.setImageResource(R.drawable.double_up);
//            }
        }
    }

    private void initTabLayout(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        imgExpandCollapse = view.findViewById(R.id.img_expand_collapse);
        imgDoubleArrow = view.findViewById(R.id.img_double_arrow);
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

        imgDoubleArrow.setOnClickListener(v -> {
            setBottomSheetState(getBottomSheetState() == BottomSheetBehavior.STATE_COLLAPSED
                    ? BottomSheetBehavior.STATE_EXPANDED
                    : BottomSheetBehavior.STATE_COLLAPSED);
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
            Log.d("MMM", "open: " + groupPosition);
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