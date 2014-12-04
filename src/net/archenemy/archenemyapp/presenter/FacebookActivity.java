package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Utility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 * Background activity to handle Facebook interaction
 */

public abstract class FacebookActivity extends ActionBarActivity
	implements FacebookAdapter.OnFacebookLoginListener {

	protected FacebookAdapter facebookAdapter;
	protected boolean pendingLogin = false;

	//Facebook lifecycle helper
	protected UiLifecycleHelper mUiHelper;

	protected Session.StatusCallback callback =
    new Session.StatusCallback() {
    @Override
    public void call(Session session,
        SessionState state, Exception exception) {
    	onFacebookLogin();
    }
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.mUiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.mUiHelper = new UiLifecycleHelper(this, this.callback);
    if (Utility.isConnectedToNetwork(this, false)) {
	    this.mUiHelper.onCreate(savedInstanceState);
    }
	}

	@Override
	public void onDestroy() {
    super.onDestroy();
    this.mUiHelper.onDestroy();
	}

	@Override
	public void onPause() {
    super.onPause();
    this.mUiHelper.onPause();
	}

	@Override
	public void onResume() {
    super.onResume();
    this.mUiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    this.mUiHelper.onSaveInstanceState(outState);
	}
}
