package com.vinh.moneymanager.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.room.MoneyManagerDatabase;
import com.vinh.moneymanager.room.daos.AccountDao;
import com.vinh.moneymanager.room.entities.Account;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AccountRepository {

    private final AccountDao accountDao;

    private final LiveData<List<Account>> accounts;

    public AccountRepository(Application application) {
        MoneyManagerDatabase database = MoneyManagerDatabase.getInstance(application);
        accountDao = database.accountDao();

        accounts = accountDao.getAccounts();
    }

    public void insert(Account account) {
        new InsertAccountAsyncTask(accountDao).execute(account);
    }

    public void update(Account account) {
        new UpdateAccountAsyncTask(accountDao).execute(account);
    }

    public void delete(Account account) {
        new DeleteAccountAsyncTask(accountDao).execute(account);
    }

    public Account search(int accountId) {
        SearchAccountAsyncTask asyncTask = new SearchAccountAsyncTask(accountDao);
        try {
            return asyncTask.execute(accountId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<Account>> getAccounts() {
        return accounts;
    }


    // AsyncTask

    private static class InsertAccountAsyncTask extends AsyncTask<Account, Void, Void> {
        private final AccountDao accountDao;

        private InsertAccountAsyncTask(AccountDao accountDao) {
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(Account... accounts) {
            accountDao.insert(accounts[0]);
            return null;
        }
    }

    private static class DeleteAccountAsyncTask extends AsyncTask<Account, Void, Void> {
        private final AccountDao accountDao;

        private DeleteAccountAsyncTask(AccountDao accountDao) {
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(Account... accounts) {
            accountDao.delete(accounts[0]);
            return null;
        }
    }

    private static class UpdateAccountAsyncTask extends AsyncTask<Account, Void, Void> {
        private final AccountDao accountDao;

        private UpdateAccountAsyncTask(AccountDao accountDao) {
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(Account... accounts) {
            accountDao.update(accounts[0]);
            return null;
        }
    }

    private static class SearchAccountAsyncTask extends AsyncTask<Integer, Void, Account> {
        private final AccountDao accountDao;

        private SearchAccountAsyncTask(AccountDao accountDao) {
            this.accountDao = accountDao;
        }

        @Override
        protected Account doInBackground(Integer... integers) {
            return accountDao.search(integers[0]);
        }
    }
}
