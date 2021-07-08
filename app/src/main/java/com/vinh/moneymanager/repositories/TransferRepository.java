package com.vinh.moneymanager.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.vinh.moneymanager.room.MoneyManagerDatabase;
import com.vinh.moneymanager.room.daos.TransferDao;
import com.vinh.moneymanager.room.entities.Transfer;

import java.util.List;

public class TransferRepository {

    private final TransferDao transferDao;

    private final LiveData<List<Transfer>> transfers;

    public TransferRepository(Application application) {
        MoneyManagerDatabase database = MoneyManagerDatabase.getInstance(application);
        transferDao = database.transferDao();

        transfers = transferDao.getTransfers();
    }

    public void insert(Transfer transfer) {
        new InsertTransferAsyncTask(transferDao).execute(transfer);
    }

    public void update(Transfer transfer) {
        new UpdateTransferAsyncTask(transferDao).execute(transfer);
    }

    public void delete(Transfer transfer) {
        new DeleteTransferAsyncTask(transferDao).execute(transfer);
    }

    public LiveData<List<Transfer>> getTransfers() {
        return transfers;
    }

    public LiveData<List<Transfer>> getTransfersSearch(String keyword) {
        return transferDao.getTransfersSearch(keyword);
    }


    // AsyncTask

    private static class InsertTransferAsyncTask extends AsyncTask<Transfer, Void, Void> {
        private final TransferDao transferDao;

        private InsertTransferAsyncTask(TransferDao transferDao) {
            this.transferDao = transferDao;
        }

        @Override
        protected Void doInBackground(Transfer... transfers) {
            transferDao.insert(transfers[0]);
            return null;
        }
    }

    private static class DeleteTransferAsyncTask extends AsyncTask<Transfer, Void, Void> {
        private final TransferDao transferDao;

        private DeleteTransferAsyncTask(TransferDao transferDao) {
            this.transferDao = transferDao;
        }

        @Override
        protected Void doInBackground(Transfer... transfers) {
            transferDao.delete(transfers[0]);
            return null;
        }
    }

    private static class UpdateTransferAsyncTask extends AsyncTask<Transfer, Void, Void> {
        private final TransferDao transferDao;

        private UpdateTransferAsyncTask(TransferDao transferDao) {
            this.transferDao = transferDao;
        }

        @Override
        protected Void doInBackground(Transfer... transfers) {
            transferDao.update(transfers[0]);
            return null;
        }
    }
}
