package com.vinh.moneymanager.libs;

import java.text.NumberFormat;
import java.util.Locale;

public class Helper {
    public static final int REQUEST_ADD_CATEGORY = 102;
    public static final int REQUEST_EDIT_CATEGORY = 103;
    public static final int REQUEST_ADD_FINANCE = 201;
    public static final int REQUEST_EDIT_FINANCE = 202;

    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_TYPE = "category_type";
    public static final String CATEGORY_DESCRIPTION = "category_description";
    public static final String ACCOUNT_ID = "account_id";
    public static final String ACCOUNT_NAME = "account_name";
    public static final String FINANCE_DAY = "f_day";
    public static final String FINANCE_TIME = "f_time";
    public static final String FINANCE_COST = "f_cost";
    public static final String FINANCE_DETAIL = "f_detail";
    public static final String FINANCE_ID = "f_id";

    public static final String FINANCE_DATETIME = "f_datetime";

    public static String formatCurrency(long cost) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(cost);
    }

    public static String formatCurrentWithoutSymbol(long cost) {
        String s = String.valueOf(cost);
        String res = "";
        int i = s.length();
        for (; i >= 0; i -= 3) {
            final String substring = s.substring(Math.max(0, i - 3), i);
            System.out.println(substring);

            res = substring + "." + res;
        }
        if (res.startsWith(".")) res = res.substring(1);
        if (res.endsWith(".")) res = res.substring(0, res.length() - 1);
        return res;
    }

}
