package com.vinh.moneymanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vinh.moneymanager.components.BottomNavigationBehavior;
import com.vinh.moneymanager.fragments.AccountFragment;
import com.vinh.moneymanager.fragments.ExpenseFragment;
import com.vinh.moneymanager.fragments.StatisticFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigationView;
    private int fragmentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

//        getSupportActionBar().hide();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        loadFragment(ExpenseFragment.getInstance());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = item -> {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_finance:
                if (fragmentIndex == 0) break;
                fragmentIndex = 0;
                fragment = new ExpenseFragment();
                loadFragment(fragment);
                return true;
            case R.id.nav_account:
                if (fragmentIndex == 1) break;
                fragmentIndex = 1;
                fragment = new AccountFragment();
                loadFragment(fragment);
                return true;
            case R.id.nav_statistic:
                if (fragmentIndex == 2) break;
                fragmentIndex = 2;
                fragment = new StatisticFragment();
                loadFragment(fragment);
                return true;
        }
        return false;
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

}