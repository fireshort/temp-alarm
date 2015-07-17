package com.yuexiaohome.tempalarm.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.yuexiaohome.tempalarm.R;


public class SettingsActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Load preference data from XML
        addPreferencesFromResource(R.xml.settings);
    }

}
