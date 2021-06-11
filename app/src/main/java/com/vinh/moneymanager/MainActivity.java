package com.vinh.moneymanager;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vinh.moneymanager.adapters.MainPagerAdapter;
import com.vinh.moneymanager.components.BottomNavigationBehavior;
import com.vinh.moneymanager.fragments.AccountFragment;
import com.vinh.moneymanager.fragments.ExpenseFragment;
import com.vinh.moneymanager.fragments.StatisticFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;
    private int fragmentIndex = 0;

    private ViewPager2 viewPager;

    private ExpenseFragment expenseFragment;
    private AccountFragment accountFragment;
    private StatisticFragment statisticFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.nav_finance:
                viewPager.setCurrentItem(0, true);
                item.setChecked(true);
                break;
            case R.id.nav_account:
                viewPager.setCurrentItem(1, true);
                item.setChecked(true);
                break;
            case R.id.nav_statistic:
                viewPager.setCurrentItem(2, true);
                item.setChecked(true);
                break;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager_main);
        setupViewPager(viewPager);

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        // bugs
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigationView.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationBehavior());

    }

    private void setupViewPager(ViewPager2 viewPager) {

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), getLifecycle());

        expenseFragment = new ExpenseFragment();
        accountFragment = new AccountFragment();
        statisticFragment = new StatisticFragment();

        adapter.addFragment(expenseFragment);
        adapter.addFragment(accountFragment);
        adapter.addFragment(statisticFragment);

        viewPager.setAdapter(adapter);
        // Không cho scroll
        viewPager.setUserInputEnabled(false);
    }

}