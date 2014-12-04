package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Settings
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
