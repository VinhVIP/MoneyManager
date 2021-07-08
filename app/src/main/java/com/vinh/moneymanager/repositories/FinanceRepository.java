package com.vinh.moneymanager.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.room.MoneyManagerDatabase;
import com.vinh.moneymanager.room.daos.FinanceDao;
import com.vinh.moneymanager.room.entities.Finance;

import java.util.List;

public class FinanceRepository {

    private final FinanceDao financeDao;


    private final LiveData<List<Finance>> finances;


    public FinanceRepository(Application application) {
        MoneyManagerDatabase database = MoneyManagerDatabase.getInstance(application);
        financeDao = database.financeDao();

        finances = financeDao.getAllFinances();
    }

    public void insert(Finance finance) {
        new InsertFinanceAsyncTask(financeDao).execute(finance);
    }

    public void update(Finance finance) {
        new UpdateFinanceAsyncTask(financeDao).execute(finance);
    }

    public void delete(Finance finance) {
        new DeleteFinanceAsyncTask(financeDao).execute(finance);
    }

    public LiveData<List<Finance>> getAllFinances() {
        return finances;
    }

    public LiveData<List<Finance>> getFinances(int categoryId) {
        return financeDao.getFinances(categoryId);
    }

    public LiveData<List<Finance>> getFinancesSearch(String keyword) {
        return financeDao.getFinancesSearch(keyword);
    }

    // ------------- AsyncTask --------------------

    private static class InsertFinanceAsyncTask extends AsyncTask<Finance, Void, Void> {
        private final FinanceDao financeDao;

        private InsertFinanceAsyncTask(FinanceDao financeDao) {
            this.financeDao = financeDao;
        }

        @Override
        protected Void doInBackground(Finance... finances) {
            financeDao.insert(finances[0]);
            return null;
        }
    }

    private static class DeleteFinanceAsyncTask extends AsyncTask<Finance, Void, Void> {
        private final FinanceDao financeDao;

        private DeleteFinanceAsyncTask(FinanceDao financeDao) {
            this.financeDao = financeDao;
        }

        @Override
        protected Void doInBackground(Finance... finances) {
            financeDao.delete(finances[0]);
            return null;
        }
    }

    private static class UpdateFinanceAsyncTask extends AsyncTask<Finance, Void, Void> {
        private final FinanceDao financeDao;

        private UpdateFinanceAsyncTask(FinanceDao financeDao) {
            this.financeDao = financeDao;
        }

        @Override
        protected Void doInBackground(Finance... finances) {
            financeDao.update(finances[0]);
            return null;
        }
    }
}
