package com.android.markschmidt.nonzeroday;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by markschmidt on 5/8/15.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }

}

