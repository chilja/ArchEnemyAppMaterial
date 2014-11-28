package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class SettingsActivity extends ActionBarActivity {

	private PreferenceFragment mPreferenceFragment;
	
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
