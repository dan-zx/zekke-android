/*
 * Copyright 2013 ZeKKe Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zekke.android.activity;

import com.zekke.android.R;
import com.zekke.android.util.Constants;
import com.zekke.android.util.GoogleMapType;

import roboguice.inject.InjectPreference;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Settings;
import android.util.Log;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;

public class SettingsActivity extends RoboSherlockPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @InjectPreference("my_location_intent_pref_key") private Preference myLocationActionProvider;
    @InjectPreference("rate_google_play_pref_key")   private Preference rateAppPreference;
    @InjectPreference("map_type_pref_key")           private ListPreference mapTypePreference;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpMyLocationActionProvider();
        setUpMapTypePreference();
        setUpRateAppPreference();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpMyLocationActionProvider() {
        myLocationActionProvider.setIntent(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private void setUpMapTypePreference() {
        GoogleMapType currentMapType = GoogleMapType.valueOf(getSharedPreferences().getString(Constants.PREF_KEY_MAP_LAST_TYPE, GoogleMapType.NORMAL.name()));
        mapTypePreference.setSummary(getString(R.string.map_type_pref_summary, currentMapType.toString(getApplicationContext())));
        CharSequence[] entryValues = new CharSequence[GoogleMapType.values().length];

        for (int i = 0; i < GoogleMapType.values().length; i++) {
            entryValues[i] = GoogleMapType.values()[i].name();
        }

        mapTypePreference.setEntries(R.array.google_map_type_strings);
        mapTypePreference.setEntryValues(entryValues);
        mapTypePreference.setValue(currentMapType.name());
        mapTypePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Log.v(TAG, "Set map type to: " + o);
                GoogleMapType newMapType = GoogleMapType.valueOf(o.toString());
                getSharedPreferences().edit().
                        putString(Constants.PREF_KEY_MAP_LAST_TYPE, newMapType.name()).
                        commit();
                preference.setSummary(getString(R.string.map_type_pref_summary, newMapType.toString(getApplicationContext())));
                return true;
            }
        });
    }

    private void setUpRateAppPreference() {
        rateAppPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_zekke_url))));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_zekke_web_url))));
                }
                return true;
            }
        });
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
}