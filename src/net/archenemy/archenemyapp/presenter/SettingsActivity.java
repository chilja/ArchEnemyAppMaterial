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

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Activity for editing settings
 * 
 * @author chiljagossow
 * 
 */
public class SettingsActivity extends ActionBarActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_activity);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    setSupportActionBar(toolbar);

    TextView title = (TextView) findViewById(R.id.title);
    title.setText(R.string.title_activity_settings);
  }
}
