package com.android.markschmidt.nonzeroday;

/**
 * Created by markschmidt on 5/7/15.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePreference extends DialogPreference {
    private Calendar mCalendar;
    private TimePicker mPicker = null;


    public TimePreference(Context context) {
        this(context, null);

    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        setPositiveButtonText(R.string.set);
        setNegativeButtonText(R.string.cancel);
        mCalendar = Calendar.getInstance();
        setDialogTitle("");
    }

    @Override
    protected View onCreateDialogView() {
        mPicker = new TimePicker(getContext());

        return (mPicker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mPicker.setCurrentHour(User.getStartHour());
        mPicker.setCurrentMinute(0);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            mCalendar.set(Calendar.HOUR_OF_DAY, mPicker.getCurrentHour());

            setSummary(getSummary());
            if (callChangeListener(mCalendar.getTimeInMillis())) {
                persistLong(mCalendar.getTimeInMillis());
                notifyChanged();
            }
            User.setStartHour(mPicker.getCurrentHour());
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        mCalendar.set(Calendar.HOUR_OF_DAY, User.getStartHour());
        mCalendar.set(Calendar.MINUTE, 0);
        setSummary(getSummary());
    }

}