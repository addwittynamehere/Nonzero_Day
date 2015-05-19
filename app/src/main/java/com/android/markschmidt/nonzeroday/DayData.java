package com.android.markschmidt.nonzeroday;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by markschmidt on 4/8/15.
 */
public class DayData {

    private int mScore; //number of pages, lines, pushups, whatever done for that element
    private Date mDate;
    private int mStreakAtDate;
    private int mTotalAtDate;

    private static final String TAG = "DayData";
    private static final String JSON_SCORE = "score";
    private static final String JSON_DATE = "date";
    private static final String JSON_STREAK = "streak";
    private static final String JSON_TOTAL = "total";


    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }

    public Date getDate() {
        return mDate;
    }

    public int getStreakAtDate() {
        return mStreakAtDate;
    }

    public void setStreakAtDate(int streakAtDate) {
        mStreakAtDate = streakAtDate;
    }

    public int getTotalAtDate() {
        return mTotalAtDate;
    }

    public void setTotalAtDate(int totalAtDate) {
        mTotalAtDate = totalAtDate;
    }

    public DayData(int s)
    {
        mDate = new Date();
        mScore = s;
    }

    public DayData(JSONObject json) throws JSONException
    {
        long time = json.getLong(JSON_DATE);
        mDate = new Date(time);
        mStreakAtDate = json.getInt(JSON_STREAK);
        mScore = json.getInt(JSON_SCORE);
        mTotalAtDate = json.getInt(JSON_TOTAL);
    }
    public JSONObject toJSON() throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(JSON_SCORE, mScore);
        long time = mDate.getTime();
        json.put(JSON_DATE, time);
        json.put(JSON_STREAK, mStreakAtDate);
        json.put(JSON_TOTAL, mTotalAtDate);
        return json;
    }

    public static long calculateTimeUntilExpiration(Date expirationDate)
    {
        long currentTime = new Date().getTime();
        long expirationTime = expirationDate.getTime();

        return expirationTime - currentTime;
    }

    public static String timeAsString(long t)
    {
        int hours = (int)(t/3600000);
        long minuteMillis = t%3600000;
        int minutes = (int)(minuteMillis/60000);

        return "" + hours + "h " + minutes + "m";

    }

    public static String dateAsString(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String monthString;
        switch (month){
            case Calendar.JANUARY: monthString = "January"; break;
            case Calendar.FEBRUARY: monthString = "February"; break;
            case Calendar.MARCH: monthString = "March"; break;
            case Calendar.APRIL: monthString = "April"; break;
            case Calendar.MAY: monthString = "May"; break;
            case Calendar.JUNE: monthString = "June"; break;
            case Calendar.JULY: monthString = "July"; break;
            case Calendar.AUGUST: monthString = "August"; break;
            case Calendar.SEPTEMBER: monthString = "September"; break;
            case Calendar.OCTOBER: monthString = "October"; break;
            case Calendar.NOVEMBER: monthString = "November"; break;
            case Calendar.DECEMBER: monthString = "December"; break;
            default: monthString="ERROR"; break;
        }

        return monthString + " " + day;
    }

    public static boolean isSameDay(Date newDate, Date otherDate)
    {
        Date nextDay1 = DayData.getActualDate(newDate);
        Date nextDay2 = DayData.getActualDate(otherDate);
        Log.i(TAG, "Actual most recent date: " + nextDay1 + "\nActual today: " + nextDay2);


        Calendar c1 = Calendar.getInstance();
        c1.setTime(newDate);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(otherDate);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);

        return day1 == day2;


    }

    public static Date getActualDate(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setLenient(true);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour <= User.getStartHour()) {
            calendar.set(Calendar.DAY_OF_MONTH, day - 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, User.getStartHour());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }
    //Precondition: date must be an actual date from getActualDate(date)
    public static Date getYesterday(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setLenient(true);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, day-1);

        return calendar.getTime();
    }

    public static Date getExpirationDate(Date d)
    {
        Date mostRecentDayData = d;
        Log.i(TAG, "Time of most recent data = " + mostRecentDayData.toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mostRecentDayData);
        calendar.setLenient(true);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if(hour <= User.getStartHour())
        {
            calendar.set(Calendar.HOUR_OF_DAY, User.getStartHour());
            calendar.set(Calendar.DAY_OF_MONTH, day + 1);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, User.getStartHour());
            calendar.set(Calendar.DAY_OF_MONTH, day + 2);
        }
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Log.i(TAG, "getExpirationDate is returning " + calendar.getTime().toString());
        return calendar.getTime();
    }
    public static Date getNextDay()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setLenient(true);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if(hour <= User.getStartHour())
        {
            calendar.set(Calendar.HOUR_OF_DAY, User.getStartHour());
            calendar.set(Calendar.DAY_OF_MONTH, day);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, User.getStartHour());
            calendar.set(Calendar.DAY_OF_MONTH, day + 1);
        }
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Log.i(TAG, "getNextDay is returning " + calendar.getTime().toString());
        return calendar.getTime();
    }

}
