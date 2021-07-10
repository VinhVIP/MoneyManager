package com.vinh.moneymanager.libs;

import android.content.Context;
import android.util.DisplayMetrics;

import com.vinh.moneymanager.R;
import com.vinh.moneymanager.room.entities.Finance;
import com.vinh.moneymanager.room.entities.Transfer;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Helper {
    public static final int TYPE_INCOME = 1;
    public static final int TYPE_EXPENSE = 2;
    public static final int TYPE_TRANSFER = 3;

    public static final int REQUEST_ADD_CATEGORY = 102;
    public static final int REQUEST_EDIT_CATEGORY = 103;
    public static final int REQUEST_ADD_FINANCE = 201;
    public static final int REQUEST_EDIT_FINANCE = 202;

    public static final int[] iconsExpense = new int[]{
            R.drawable.chi1,
            R.drawable.chi2,
            R.drawable.chi3,
            R.drawable.chi4,
            R.drawable.chi5,
            R.drawable.chi6,
            R.drawable.chi7,
            R.drawable.chi8,
            R.drawable.chi9,
            R.drawable.chi10,
            R.drawable.chi11,
            R.drawable.chi12,
            R.drawable.chi13
    };
    public static final int[] iconsIncome = new int[]{
            R.drawable.thu1,
            R.drawable.thu2,
            R.drawable.thu3,
            R.drawable.thu4,
            R.drawable.thu5
    };

    public static final int[] iconsAccount = new int[]{
            R.drawable.account1,
            R.drawable.account2
    };


    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_TYPE = "category_type";
    public static final String CATEGORY_DESCRIPTION = "category_description";
    public static final String CATEGORY_ICON = "category_icon";

    public static final String ACCOUNT_ID = "account_id";
    public static final String ACCOUNT_NAME = "account_name";
    public static final String ACCOUNT_BALANCE = "account_balance";
    public static final String ACCOUNT_DESCRIPTION = "account_description";
    public static final String ACCOUNT_ICON = "account_icon";

    public static final String FINANCE_DAY = "f_day";
    public static final String FINANCE_TIME = "f_time";
    public static final String FINANCE_COST = "f_cost";
    public static final String FINANCE_DETAIL = "f_detail";
    public static final String FINANCE_ID = "f_id";

    public static final String FINANCE_DATETIME = "f_datetime";

    public static final String ADD_CATEGORY = "add_category";
    public static final String EDIT_CATEGORY = "edit_category";
    public static final String EDIT_ACCOUNT = "edit_account";
    public static final String ADD_FINANCE = "add_category";
    public static final String EDIT_FINANCE = "edit_finance";
    public static final String EDIT_TRANSFER = "edit_transfer";
    public static final String ADD_TRANSFER = "add_transfer";

    public static final String TRANSFER_ID = "TRANSFER_ID";
    public static final String TRANSFER_DATETIME = "TRANSFER_DATETIME";
    public static final String TRANSFER_ACCOUNT_OUT_ID = "TRANSFER_ACCOUNT_OUT_ID";
    public static final String TRANSFER_ACCOUNT_IN_ID = "TRANSFER_ACCOUNT_IN_ID";
    public static final String TRANSFER_MONEY = "TRANSFER_MONEY";
    public static final String TRANSFER_FEE = "TRANSFER_FEE";
    public static final String TRANSFER_DETAIL = "TRANSFER_DETAIL";


    public static String formatCurrency(long cost) {
//        Locale localeVN = new Locale("vi", "VN");
//        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
//        return currencyVN.format(cost);
        return formatCurrencyWithoutSymbol(cost) + "đ";
    }

    public static String clearDotInText(String text) {
        return text.replace(".", "");
    }

    public static String formatCurrencyWithoutSymbol(String cost) {
        if (cost == null || cost.isEmpty()) return "";

        cost = clearDotInText(cost);
        return formatCurrencyWithoutSymbol(Long.parseLong(cost));
    }

    public static String formatCurrencyWithoutSymbol(long cost) {
        boolean sign = cost < 0;
        cost = Math.abs(cost);

        String s = String.valueOf(cost);
        String res = "";
        int i = s.length();
        for (; i >= 0; i -= 3) {
            final String substring = s.substring(Math.max(0, i - 3), i);
            res = substring + "." + res;
        }
        if (res.startsWith(".")) res = res.substring(1);
        if (res.endsWith(".")) res = res.substring(0, res.length() - 1);

        if (sign) res = "-" + res;
        return res;
    }

    public static String getDayOfWeek(DateRange.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(date.year, date.month - 1, date.day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "T2";
            case Calendar.TUESDAY:
                return "T3";
            case Calendar.WEDNESDAY:
                return "T4";
            case Calendar.THURSDAY:
                return "T5";
            case Calendar.FRIDAY:
                return "T6";
            case Calendar.SATURDAY:
                return "T7";
            case Calendar.SUNDAY:
                return "CN";
        }
        return "";
    }

    public static String validName(String name) {
        name = name.trim();
        String[] s = name.split(" ");
        String res = "";
        for (int i = 0; i < s.length - 1; i++) {
            if (!s[i].isEmpty()) res += s[i] + " ";
        }
        res += s[s.length - 1];
        return res;
    }

    public static int compareDateNow(int[] a) {
        int[] b = new int[3];
        Calendar now = Calendar.getInstance();
        b[0] = now.get(Calendar.YEAR);
        b[1] = now.get(Calendar.MONTH);
        b[2] = now.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < 3; i++) {
            if (a[i] < b[i]) return -1;
            else if (a[i] > b[i]) return 1;
        }
        return 0;
    }

    public static int compareTime(String[] date1, String[] time1, String[] date2, String[] time2) {
        int[] a = new int[5];
        for (int i = 0; i < 3; i++) a[i] = Integer.parseInt(date1[2 - i].trim());
        for (int i = 3; i < 5; i++) a[i] = Integer.parseInt(time1[i - 3].trim());

        int[] b = new int[5];
        for (int i = 0; i < 3; i++) b[i] = Integer.parseInt(date2[2 - i].trim());
        for (int i = 3; i < 5; i++) b[i] = Integer.parseInt(time2[i - 3].trim());

        for (int i = 0; i < 5; i++) {
            if (a[i] < b[i]) return -1;
            else if (a[i] > b[i]) return 1;
        }
        return 0;
    }

    public static void sortFinanceByTimeAsc(List<Finance> list) {
        Collections.sort(list, (t1, t2) ->
        {
            String[] date1 = t1.getDateTime().split("-")[0].split("/");
            String[] time1 = t1.getDateTime().split("-")[1].split(":");
            String[] date2 = t2.getDateTime().split("-")[0].split("/");
            String[] time2 = t2.getDateTime().split("-")[1].split(":");

            return compareTime(date1, time1, date2, time2);
        });
    }

    public static void sortTransferByTimeAsc(List<Transfer> list) {
        Collections.sort(list, (t1, t2) ->
        {
            String[] date1 = t1.getDateTime().split("-")[0].split("/");
            String[] time1 = t1.getDateTime().split("-")[1].split(":");
            String[] date2 = t2.getDateTime().split("-")[0].split("/");
            String[] time2 = t2.getDateTime().split("-")[1].split(":");

            return compareTime(date1, time1, date2, time2);
        });
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
