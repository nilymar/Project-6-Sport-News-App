package com.example.android.sportnewsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
//import com.example.android.newsapp.NumberPickerPreference;
//import com.example.android.newsapp.DatePickerPreference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingActivity extends AppCompatActivity {
    private static final String SHARED_PREFERENCES = "newsappshared"; // name for sharedPreferences location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public static class ArticlePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_main);
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
            Preference setArticleNumber = findPreference(getString(R.string.settings_articles_number_key));
            bindPreferenceSummaryToValue(setArticleNumber);
            Preference setStartDate = findPreference(getString(R.string.settings_start_date_key));
            bindPreferenceSummaryToValue(setStartDate);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            if (preference instanceof ListPreference) {
                String stringValue = value.toString();
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else if (preference instanceof NumberPickerPreference) {
                NumberPickerPreference numPref = (NumberPickerPreference) preference;
                String articleNum = String.valueOf(numPref.getValue());
                savePreferences(preference.getKey(),articleNum);
                preference.setSummary(articleNum);
            } else if (preference instanceof DatePickerPreference) {
                DatePickerPreference datePref = (DatePickerPreference) preference;
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(getString(R.string.xml_date_format));
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(getString(R.string.display_date_format));
                Calendar calendar = datePref.getDate();
                String startDate = simpleDateFormat1.format(calendar.getTime());
                String showDate = simpleDateFormat2.format(calendar.getTime());
                savePreferences(preference.getKey(),startDate);
                preference.setSummary(showDate);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            if (preference instanceof ListPreference) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                String preferenceString = preferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            } else  {
                String preferenceString = restorePreferences(preference.getKey());
                onPreferenceChange(preference, preferenceString);
            }
        }

        // This method to store the custom preferences changes
        public void savePreferences(String key, String value) {
            SharedPreferences myPreferences = this.getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor myEditor = myPreferences.edit();
            myEditor.putString(key, value);
            myEditor.apply();
        }
        // This method to restore the custom preferences data
        public String restorePreferences(String key) {
            SharedPreferences myPreferences = this.getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
            if (myPreferences.contains(key))
            return myPreferences.getString(key,"");
            else return "";
        }
    }

}

