/**
 * Copyright 2014-present Chilja Gossow.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Fragment for editing settings
 * 
 * @author chiljagossow
 * 
 */
public class SettingsFragment extends PreferenceFragment implements
    OnSharedPreferenceChangeListener {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
    setStartMenuSummary();
  }

  @Override
  public void onPause() {
    super.onPause();
    PreferenceManager.getDefaultSharedPreferences(getActivity())
        .unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    PreferenceManager.getDefaultSharedPreferences(getActivity())
        .registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(Constants.PREF_KEY_START)) {
      setStartMenuSummary();
    }
  }

  private void setStartMenuSummary() {
    SharedPreferences sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());
    String start = sharedPreferences.getString(Constants.PREF_KEY_START, Constants.FACEBOOK);
    String summary = (Constants.FACEBOOK.equals(start)) ? getResources().getString(
        R.string.title_facebook) : getResources().getString(R.string.title_twitter);
    Preference startMenuPreference = getPreferenceManager()
        .findPreference(Constants.PREF_KEY_START);
    startMenuPreference.setSummary(summary);
  }
}
