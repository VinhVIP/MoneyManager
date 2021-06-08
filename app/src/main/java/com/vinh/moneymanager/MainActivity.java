package com.vinh.moneymanager;

import android.os.Bundle;

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
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

//        getSupportActionBar().hide();
        viewPager = findViewById(R.id.view_pager_main);
        setupViewPager(viewPager);

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//
//                switch (position) {
//                    case 0:
//                        navigationView.getMenu().findItem(R.id.nav_finance).setChecked(true);
//                        break;
//                    case 1:
//                        navigationView.getMenu().findItem(R.id.nav_account).setChecked(true);
//                        break;
//                    case 2:
//                        navigationView.getMenu().findItem(R.id.nav_statistic).setChecked(true);
//                        break;
//                }
//            }
//        });

        // bugs
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

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