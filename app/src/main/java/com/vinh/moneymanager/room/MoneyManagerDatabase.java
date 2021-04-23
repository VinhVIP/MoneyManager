package com.vinh.moneymanager.room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.vinh.moneymanager.room.daos.AccountDao;
import com.vinh.moneymanager.room.daos.CategoryDao;
import com.vinh.moneymanager.room.daos.FinanceDao;
import com.vinh.moneymanager.room.daos.TypeDao;
import com.vinh.moneymanager.room.entities.Account;
import com.vinh.moneymanager.room.entities.Category;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.room.entities.Type;

@Database(entities = {Type.class, Category.class, Finance.class, Account.class}, version = 1)
public abstract class MoneyManagerDatabase extends RoomDatabase {

    private static MoneyManagerDatabase instance;

    public abstract TypeDao typeDao();
    public abstract CategoryDao categoryDao();
    public abstract AccountDao accountDao();
    public abstract FinanceDao financeDao();

    public static synchronized MoneyManagerDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MoneyManagerDatabase.class, "money_manager")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {

        private TypeDao typeDao;
        private CategoryDao categoryDao;
        private FinanceDao financeDao;
        private AccountDao accountDao;

        private PopulateDBAsyncTask(MoneyManagerDatabase database) {
            typeDao = database.typeDao();
            categoryDao = database.categoryDao();
            financeDao = database.financeDao();
            accountDao = database.accountDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            typeDao.insert(new Type(0, "Thu nhập"));
            typeDao.insert(new Type(1, "Chi tiêu"));
            typeDao.insert(new Type(2, "Chuyển khoản"));

            categoryDao.insert(new Category("Ăn uống", 1, "Bao gồm ăn vặt và ăn bữa chính"));
            categoryDao.insert(new Category("Mua sắm", 1, "Mua các vật dụng cá nhân"));

            categoryDao.insert(new Category("Lương", 0, "Mua các vật dụng cá nhân"));

            accountDao.insert(new Account("Tiền mặt", 5000000, "Tiền mặt"));
            accountDao.insert(new Account("Thẻ BIDV", 8000000, "Tiền trong thẻ ngân hàng"));

            return null;
        }
    }
}
