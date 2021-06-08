package com.vinh.moneymanager.libs;

import java.util.Calendar;

public class DateRange {

    public static final int MODE_DAY = 0;
    public static final int MODE_WEEK = 1;
    public static final int MODE_MONTH = 2;
    public static final int MODE_CUSTOM = 3;

    public static Date nowDate;
    public static Calendar calendar;

    static {
        calendar = Calendar.getInstance();
        nowDate = new Date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));

    }

    private int mode;
    private Date startDate, endDate;

    public DateRange(int mode) {
        this.mode = mode;
        startDate = endDate = nowDate;
    }


    public DateRange() {
        this(MODE_MONTH);
    }

    public DateRange(int mode, Date startDate, Date endDate) {
        this.mode = mode;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static int getLastDay(int month, int year) {
        switch (month) {
            case 2:
                if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
                    return 29;
                } else {
                    return 28;
                }
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    public String getDateString() {
        switch (mode) {
            case MODE_DAY:
                if (startDate.year == nowDate.year) {
                    return String.format("%02d/%02d", startDate.day, startDate.month);
                } else {
                    return String.format("%02d/%02d/%02d", startDate.day, startDate.month, startDate.year);
                }
            case MODE_MONTH:
                return String.format("Th%02d - %d", startDate.month, startDate.year);
            case MODE_WEEK:
                return String.format("Tuần %d - %d", getWeekOfYear(), startDate.year);
//                if (startDate.year != nowDate.year || endDate.year != nowDate.year){
//                    return String.format("%02d/%02d/%d - %02d/%02d/%d", startDate.day, startDate.month, startDate.year, endDate.day, endDate.month, endDate.year);
//                }else{
//                    return String.format("%02d/%02d - %02d/%02d", startDate.day, startDate.month, endDate.day, endDate.month);
//                }
            case MODE_CUSTOM:
                if (startDate.compare(endDate) == 0) {
                    if (startDate.year == nowDate.year) {
                        return String.format("%02d/%02d", startDate.day, startDate.month);
                    } else {
                        return String.format("%02d/%02d/%d", startDate.day, startDate.month, startDate.year);
                    }
                } else if (startDate.year == nowDate.year && endDate.year == nowDate.year) {
                    return String.format("%02d/%02d - %02d/%02d", startDate.day, startDate.month, endDate.day, endDate.month);
                } else {
                    return String.format("%02d/%02d/%d - %02d/%02d/%d", startDate.day, startDate.month, startDate.year, endDate.day, endDate.month, endDate.year);
                }
        }
        return "";
    }

    public String getWeekString() {
        return String.format("%02d/%02d - %02d/%02d", startDate.day, startDate.month, endDate.day, endDate.month);
    }

    public void next() {
        switch (mode) {
            case MODE_DAY:
                nextDay();
                break;
            case MODE_WEEK:
                nextWeek();
                break;
            case MODE_MONTH:
                nextMonth();
                break;
        }
    }

    public void previous() {
        switch (mode) {
            case MODE_DAY:
                previousDay();
                break;
            case MODE_WEEK:
                previousWeek();
                break;
            case MODE_MONTH:
                previousMonth();
                break;
        }
    }

    private void jumpDay(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_MONTH, startDate.day);
        calendar.set(Calendar.MONTH, startDate.month - 1);
        calendar.set(Calendar.YEAR, startDate.year);
        calendar.add(Calendar.DAY_OF_YEAR, n);
        startDate = new Date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        endDate = new Date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    private void nextDay() {
        jumpDay(1);
    }

    private void previousDay() {
        jumpDay(-1);
    }

    private void nextWeek() {
        jumpWeek(1);
    }

    private void previousWeek() {
        jumpWeek(-1);
    }

    private void nextMonth() {
        jumpMonth(1);
    }

    private void previousMonth() {
        jumpMonth(-1);
    }

    private void jumpWeek(int n) {
        setWeek(getWeekOfYear() + n, endDate.year);
    }

    private void jumpMonth(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.MONTH, startDate.month - 1);
        calendar.set(Calendar.YEAR, startDate.year);
        calendar.add(Calendar.MONTH, n);
        startDate = new Date(1, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        endDate = new Date(getLastDay(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    public void setWeek(int week, int year) {
        System.out.println(week + " ---- " + year);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.YEAR, year);


        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        setStartDate(new Date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));

        calendar.add(Calendar.DAY_OF_YEAR, 6);
        setEndDate(new Date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
    }

    public int getWeekOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_MONTH, startDate.day);
        calendar.set(Calendar.MONTH, startDate.month - 1);
        calendar.set(Calendar.YEAR, startDate.year);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
//        if(startDate.compare(this.endDate) > 0){
//            this.startDate = this.endDate;
//            this.endDate = startDate;
//        }else{
        this.startDate = startDate;
//        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
//        if(endDate.compare(this.startDate) < 0){
//            this.endDate = this.startDate;
//            this.startDate = endDate;
//        }else{
        this.endDate = endDate;
//        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public static class Date {

        int day, month, year;

        public Date(int day, int month, int year) {
            this.day = day;
            this.month = month;
            this.year = year;
        }

        public Date(String s) {
            String[] arr = s.split("/");
            day = Integer.parseInt(arr[0]);
            month = Integer.parseInt(arr[1]);
            year = Integer.parseInt(arr[2]);
        }

        public int compare(Date d) {
            if (year < d.year) return -1;
            else if (year > d.year) return 1;

            if (month < d.month) return -1;
            else if (month > d.month) return 1;

            if (day < d.day) return -1;
            else if (day > d.day) return 1;

            return 0;
        }

        public int getDay() {
            return day;
        }

        public int getMonth() {
            return month;
        }

        public int getYear() {
            return year;
        }
    }

}
