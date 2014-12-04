package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.ProviderAdapter;
import net.archenemy.archenemyapp.model.Utility;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

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
		init();
	}

	void showHeader(Boolean showHeader) {
		this.showHeader = showHeader;
	}

	void showUserInfo(Boolean showUserInfo) {
		this.showUserInfo = showUserInfo;
	}

	protected void init() {
		if ((this.providerAdapter != null) && this.providerAdapter.isLoggedIn()) {
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

	protected void setLoggedIn(){
		this.loginButton.setEnabled(true);
		this.loginButton.setText(R.string.button_log_out);
		if(this.showUserInfo) {
			this.subtext.setText(R.string.account_logged_in);
			this.subtext.setTextColor(getResources().getColor(R.color.text_primary));
		}
	}

	protected void setLoggedOut(){
		this.loginButton.setEnabled(true);
		this.loginButton.setText(R.string.button_log_in);
		if(this.showUserInfo) {
			this.subtext.setText(R.string.account_logged_out);
			this.subtext.setTextColor(getResources().getColor(R.color.text_primary));
			this.userNameView.setText(null);
		}
	}

	protected void setOffline() {
		this.loginButton.setEnabled(false);
		if(this.showUserInfo) {
			this.subtext.setText(R.string.account_offline);
			this.subtext.setTextColor(getResources().getColor(R.color.accent));
			this.userNameView.setText(null);
		}
	}

	protected void setOnline() {
		this.loginButton.setEnabled(true);
	}
}

