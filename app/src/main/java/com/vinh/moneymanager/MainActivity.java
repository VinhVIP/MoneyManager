package com.vinh.moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vinh.moneymanager.activities.AddEditFinanceActivity;
import com.vinh.moneymanager.fragments.ExpenseFragment;
import com.vinh.moneymanager.fragments.AccountFragment;
import com.vinh.moneymanager.fragments.StatisticFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

//        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        loadFragment(ExpenseFragment.getInstance());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.nav_finance:
                    fragment = ExpenseFragment.getInstance();
                    loadFragment(fragment);
                    return true;
                case R.id.nav_account:
                    fragment = new AccountFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.nav_statistic:
                    fragment = new StatisticFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void moveToAddEditFinance(){
        Intent intent = new Intent(MainActivity.this, AddEditFinanceActivity.class);
        startActivity(intent);
    }

    // Comeback to Java

}