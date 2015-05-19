package com.android.markschmidt.nonzeroday;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by markschmidt on 4/8/15.
 */
public class Objective {

    // Today I (verb) (score) (noun)

    // I've (pastVerb) (total) (noun)

    public ArrayList<DayData> mDayData;
    private int mTotal;
    private int mStreakCount;
    private UUID mId;

    private String mSingularNoun; // e.g. page
    private String mMultipleNoun; // e.g. pages
    private String mVerb; // Today I _____ (number)
    private String mPastVerb;
    private String mTitle;
    private Date mExpirationDate;



    private boolean onAStreak;
    private int mImage; // int corresponding to position in drawable array

    private static final String TAG = "Objective";
    private static final String JSON_DAYDATA = "daydata";
    private static final String JSON_TOTAL = "total";
    private static final String JSON_STREAK_COUNT = "streakcount";
    private static final String JSON_ID = "id";
    private static final String JSON_SNOUN = "snoun";
    private static final String JSON_MNOUN = "mnoun";
    private static final String JSON_VERB = "verb";
    private static final String JSON_PVERB = "pastverb";
    private static final String JSON_TITLE = "title";
    private static final String JSON_IMAGE = "imagePos";
    private static final String JSON_STREAK_CHECK = "onAStreak";
    private static final String JSON_EXPIRATION_DATE = "expirationDate";

    public JSONObject toJSON() throws JSONException
    {
        JSONObject json = new JSONObject();


        json.put(JSON_TOTAL, mTotal);
        json.put(JSON_STREAK_COUNT, mStreakCount);
        json.put(JSON_ID, mId.toString());
        json.put(JSON_SNOUN, mSingularNoun);
        json.put(JSON_MNOUN, mMultipleNoun);
        json.put(JSON_VERB, mVerb);
        json.put(JSON_PVERB, mPastVerb);
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_IMAGE, mImage);
        json.put(JSON_STREAK_CHECK, onAStreak);

        if(mExpirationDate != null)
            json.put(JSON_EXPIRATION_DATE, mExpirationDate.getTime());

        JSONArray jsonArray = new JSONArray();
        for(DayData d : mDayData)
        {
            Log.i(TAG, "Put daydata into jsonObject");
            jsonArray.put(d.toJSON());
        }
        json.put(JSON_DAYDATA, jsonArray);
        return json;
    }

    public Objective(JSONObject json) throws JSONException
    {
        if(json.has(JSON_SNOUN)) mSingularNoun = json.getString(JSON_SNOUN);
        if(json.has(JSON_VERB)) mVerb = json.getString(JSON_VERB);
        if(json.has(JSON_PVERB)) mPastVerb = json.getString(JSON_PVERB);
        if(json.has(JSON_TITLE)) mTitle = json.getString(JSON_TITLE);
        if(json.has(JSON_TOTAL)) mTotal = json.getInt(JSON_TOTAL);
        if(json.has(JSON_STREAK_COUNT)) mStreakCount = json.getInt(JSON_STREAK_COUNT);
        if(json.has(JSON_ID)) mId = UUID.fromString(json.getString(JSON_ID));
        if(json.has(JSON_IMAGE)) mImage = json.getInt(JSON_IMAGE);
        if(json.has(JSON_MNOUN)) mMultipleNoun = json.getString(JSON_MNOUN);
        if(json.has(JSON_STREAK_CHECK)) onAStreak = json.getBoolean(JSON_STREAK_CHECK);
        if(json.has(JSON_EXPIRATION_DATE)) mExpirationDate = new Date(json.getLong(JSON_EXPIRATION_DATE));
        else mExpirationDate = null;

        JSONArray jsonArray = (JSONArray) json.get(JSON_DAYDATA);
        Log.i(TAG,"jsonArray size = " + jsonArray.length());
        mDayData = new ArrayList<DayData>();
        for (int j = 0; j < jsonArray.length(); j++) {
            DayData data = new DayData(jsonArray.getJSONObject(j));
            mDayData.add(j, data);
        }

    }

    public Objective(String sn, String v, String p, String t, String mn, int i)
    {
        mSingularNoun = sn;
        mMultipleNoun = mn;
        mVerb = v;
        mPastVerb = p;
        mTitle = t;
        mTotal = 0;
        mStreakCount = 0;
        mId = UUID.randomUUID();
        mImage = i;
        onAStreak = false;
        mDayData = new ArrayList<DayData>();

    }

    public void checkStreak()
    {
        Date now = new Date();
        if(mExpirationDate != null && mExpirationDate.before(now))
        {
            // streak expires
            mStreakCount = 0;
            onAStreak = false;
        }
    }

    public UUID getId() {
        return mId;
    }

    public ArrayList<DayData> getDayData() {
        return mDayData;
    }


    public int getTotal() {
        return mTotal;
    }


    public int getStreakCount() {
        return mStreakCount;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public void setStreakCount(int streakCount) {
        mStreakCount = streakCount;
    }

    public void setDayData(ArrayList<DayData> dayData) {
        mDayData = dayData;
    }

    public String getVerb() {
        return mVerb;
    }

    public void setVerb(String verb) {
        mVerb = verb;
    }

    public String getSingularNoun() {
        return mSingularNoun;
    }

    public void setSingularNoun(String singularNoun) {
        mSingularNoun = singularNoun;
    }

    public String getPastVerb() {
        return mPastVerb;
    }

    public void setPastVerb(String pastVerb) {
        mPastVerb = pastVerb;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int image) {
        mImage = image;
    }

    public String getMultipleNoun() {
        return mMultipleNoun;
    }

    public void setMultipleNoun(String multipleNoun) {
        mMultipleNoun = multipleNoun;
    }

    public boolean isOnAStreak() { return onAStreak; }

    public void setOnAStreak(boolean onAStreak) {
        this.onAStreak = onAStreak;
    }

    public Date getExpirationDate() {
        return mExpirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        mExpirationDate = expirationDate;
    }






}
