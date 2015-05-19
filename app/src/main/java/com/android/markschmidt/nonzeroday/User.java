package com.android.markschmidt.nonzeroday;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by markschmidt on 4/24/15.
 */

// A user is the unifying class of the entire project
// A user "contains" the Objectives which contain the daydata
// A user contains relevant settings such as StartHour
// A user keeps track of level and experience
public class User {
    private long mExperience;
    private int mLevel;
    private long mNextLevelExperience;
    private long mPreviousLevelExperience;
    private ArrayList<Objective> mObjectives;
    private static int sStartHour;

    private static final String JSON_PREVIOUS_LEVEL = "previousLevel";
    private static final String JSON_EXPERIENCE = "experience";
    private static final String JSON_LEVEL = "level";
    private static final String JSON_NEXT_LEVEl = "nextLevel";
    private static final String JSON_START_HOUR = "startHour";


    private static final String TAG = "User";

    public User()
    {
        mExperience = 0;
        mLevel = 1;
        mPreviousLevelExperience = 0;
        mNextLevelExperience = calculateNextLevelExperience(1);
        sStartHour = 0;
    }

    public JSONObject toJSON() throws JSONException
    {
        JSONObject json = new JSONObject();

        json.put(JSON_EXPERIENCE, mExperience);
        json.put(JSON_LEVEL, mLevel);
        json.put(JSON_NEXT_LEVEl, mNextLevelExperience);
        json.put(JSON_START_HOUR, sStartHour);
        json.put(JSON_PREVIOUS_LEVEL, mPreviousLevelExperience);

        return json;
    }

    public User(JSONObject json) throws JSONException
    {
        mExperience = json.getLong(JSON_EXPERIENCE);
        mLevel = json.getInt(JSON_LEVEL);
        mNextLevelExperience = json.getLong(JSON_NEXT_LEVEl);
        sStartHour = json.getInt(JSON_START_HOUR);
        mPreviousLevelExperience = json.getLong(JSON_PREVIOUS_LEVEL);
    }
    public long calculateNextLevelExperience(int level) {

        return 500*level;
    }

    public int addXP(int xp)
    {
        mExperience+= xp;
        if(mExperience >= mNextLevelExperience)
        {
            mLevel++;
            mPreviousLevelExperience = mNextLevelExperience;
            mNextLevelExperience = calculateNextLevelExperience(mLevel);
        }
        return xp;
    }

    public int calcXP(int streak)
    {
        return streak * 100;
    }

    public static int getStartHour() {
        return sStartHour;
    }
    public static void setStartHour(int s){
        sStartHour = s;
    }

    public ArrayList<Objective> getObjectives() {
        return mObjectives;
    }

    public void setObjectives(ArrayList<Objective> objectives) {
        mObjectives = objectives;
    }

    //returns experience added
    public int add(int s, int position)
    {
        int experience = 0;
        Objective o = mObjectives.get(position);
        ArrayList<DayData> data = o.getDayData();
        boolean newData = false;
        if(data.size() == 0)
        {
            DayData additionalData = new DayData(s);
            additionalData.setStreakAtDate(o.getStreakCount()+ 1);
            additionalData.setTotalAtDate(s);
            data.add(additionalData);
            experience = addXP(calcXP(o.getStreakCount()+1));

            Log.i(TAG, "Experience is " + mExperience + ", level " + (mLevel + 1) + " begins at " + mNextLevelExperience);


            o.setStreakCount(1);

        }
        else {
            DayData top = data.get(data.size()-1);
            if (DayData.isSameDay(top.getDate(), new Date())) {
                top.setScore(top.getScore() + s);
                top.setTotalAtDate(top.getTotalAtDate() + s);
            }
            else {
                DayData additionalData = new DayData(s);
                additionalData.setStreakAtDate(o.getStreakCount() + 1);
                additionalData.setTotalAtDate(o.getTotal() + s);
                data.add(additionalData);
                o.setStreakCount(o.getStreakCount() + 1);
                newData = true;
                experience = addXP(calcXP(o.getStreakCount()));
                Log.i(TAG, "Experience is " + mExperience + ", level " + (mLevel + 1) + " begins at " + mNextLevelExperience);
            }

        }
        o.setTotal(o.getTotal() + s);
        o.setOnAStreak(true);
        return experience;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public long getExperience() {
        return mExperience;
    }

    public void setExperience(long experience) {
        mExperience = experience;
    }



    public long getNextLevelExperience() {
        return mNextLevelExperience;
    }

    public void setNextLevelExperience(long nextLevelExperience) {
        mNextLevelExperience = nextLevelExperience;
    }

    public long getPreviousLevelExperience() {
        return mPreviousLevelExperience;
    }

    public void setPreviousLevelExperience(long previousLevelExperience) {
        mPreviousLevelExperience = previousLevelExperience;
    }

    public double calcPercentageXP()
    {
        double numerator = mExperience - mPreviousLevelExperience;
        double denominator = mNextLevelExperience - mPreviousLevelExperience;
        double percentage = (100 * (numerator/denominator));
        Log.i(TAG, "Percentage is " + percentage + " \nEquation is (" + mExperience + "-" + mPreviousLevelExperience + ")/("
        + mNextLevelExperience + "-" + mPreviousLevelExperience + ") * 100\nNumerator is " + numerator + ", denominator is " + denominator);
        return percentage;
    }

    public long calcXPtoLevelUp()
    {
        return mNextLevelExperience - mExperience;
    }
}
