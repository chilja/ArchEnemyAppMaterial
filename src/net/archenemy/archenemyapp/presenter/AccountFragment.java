package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.ProviderAdapter;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
 /**
  * Abstract class  with basic logic for account fragments
  * @author chiljagossow
  *
  */
public abstract class AccountFragment extends BaseFragment {

	protected TextView userNameView;
	protected TextView subtext;
	protected TextView headerText;
	protected Button loginButton;
	protected String name;
	protected Boolean showHeader = false;
	protected Boolean showUserInfo = true;
	protected View userView;
	protected FrameLayout text;

	protected ProviderAdapter providerAdapter;

	@Override
	public void onResume() {
		super.onResume();
		updateState();
	}

	protected void setLoggedIn(){
		loginButton.setEnabled(true);
		loginButton.setText(R.string.button_log_out);
		if(showUserInfo) {
			subtext.setText(R.string.account_logged_in);
			subtext.setTextColor(getResources().getColor(R.color.text_primary));
		}
	}

	protected void setLoggedOut(){
		loginButton.setEnabled(true);
		loginButton.setText(R.string.button_log_in);
		if(showUserInfo) {
			subtext.setText(R.string.account_logged_out);
			subtext.setTextColor(getResources().getColor(R.color.text_primary));
			userNameView.setText(null);
		}
	}

	protected void setOffline() {
		loginButton.setEnabled(false);
		if(showUserInfo) {
			subtext.setText(R.string.account_offline);
			subtext.setTextColor(getResources().getColor(R.color.accent));
			userNameView.setText(null);
		}
	}

	protected void setOnline() {
		loginButton.setEnabled(true);
	}

	/**
	 * Updates states to logged in/out or off line
	 */
	protected void updateState() {
		if ((providerAdapter != null) && providerAdapter.isLoggedIn()) {
    		setLoggedIn();
	    } else {
	    	setLoggedOut();
    	}
		if (Utility.isConnectedToNetwork(getActivity(), false)) {
			setOnline();
		} else {
			setOffline();
		}
	}

	/**
	 * Set flag whether login header should be shown
	 * @param showHeader
	 */
	void showHeader(Boolean showHeader) {
		this.showHeader = showHeader;
	}

	/**
	 * Set flag whether user info should be shown
	 * @param showUserInfo
	 */
	void showUserInfo(Boolean showUserInfo) {
		this.showUserInfo = showUserInfo;
	}
}

