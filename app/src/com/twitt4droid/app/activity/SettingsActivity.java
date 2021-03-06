/*
 * Copyright 2014 Daniel Pedraza-Arcega
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
package com.twitt4droid.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private Preference licencesPreference;
    private Preference clearCachePreference;
    private Preference closeSessionPreference;
    private Preference versionPreference;
    private ListPreference changeThemePreference;
    
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        findPreferences();
        setUpLicencesPreference();
        setUpClearCachePreference();
        setUpChangeThemePreference();
        setUpCloseSessionPreference();
        setUpVersionPreference();
    }

    @SuppressWarnings("deprecation")
    private void findPreferences() {
        licencesPreference = findPreference(getString(R.string.licences_key));
        clearCachePreference = findPreference(getString(R.string.clear_cache_key));
        closeSessionPreference  = findPreference(getString(R.string.close_session_key));
        versionPreference = findPreference(getString(R.string.app_version_key));
        changeThemePreference = (ListPreference) findPreference(getString(R.string.change_theme_key));
    }

    private void setUpLicencesPreference() {
        licencesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                WebView webView = new WebView(SettingsActivity.this);
                webView.loadUrl(getString(R.string.licenses_file));
                new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle(R.string.licenses_dialog_tile)
                    .setView(webView)
                    .setCancelable(true)
                    .show();
                return true;
            }
        });
    }

    private void setUpClearCachePreference() {
        clearCachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Twitt4droid.clearCache(getApplicationContext());
                Toast.makeText(getApplicationContext(), 
                        R.string.cache_cleared_message, 
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setUpChangeThemePreference() {
        changeThemePreference.setSummary(getString(R.string.change_theme_summary, changeThemePreference.getEntry()));
        changeThemePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ListPreference p = (ListPreference)preference;
                int index = p.findIndexOfValue(newValue.toString());
                String entryValue = p.getEntries()[index].toString();
                p.setSummary(getString(R.string.change_theme_summary, entryValue));
                Toast.makeText(getApplicationContext(), 
                        R.string.change_theme_warning, Toast.LENGTH_LONG)
                        .show();
                return true;
            }
        });
    }

    private void setUpCloseSessionPreference() {
        closeSessionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle(R.string.close_session_title)
                    .setMessage(R.string.close_session_alert_message)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Twitt4droid.resetData(getApplicationContext());
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
                return true;
            }
        });
    }

    private void setUpVersionPreference() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionPreference.setSummary(versionName);
        } catch (NameNotFoundException ex) {
            Log.e(TAG, "Couldn't find version name", ex);
        }
    }
}