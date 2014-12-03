package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.ProviderAdapter;
import net.archenemy.archenemyapp.model.Utility;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public abstract class AccountFragment extends BaseFragment {
	//UI elements
	protected TextView mUserNameView;
	protected TextView mSubtext;
	protected TextView mHeaderText;
	protected Button mLoginButton;	
	protected String mName;
	protected Boolean mShowHeader = false;
	protected Boolean mShowUserInfo = true;
	protected View mUserView;
	protected FrameLayout mText;
	
	protected ProviderAdapter mProviderAdapter;
	
	public void showHeader(Boolean showHeader) {
		mShowHeader = showHeader;
	}
	
	public void showUserInfo(Boolean showUserInfo) {
		mShowUserInfo = showUserInfo;
	}
	
	protected void fadeIn() {
//		Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
//		fadeIn.setFillAfter(true);
//		mText.startAnimation(fadeIn);
	}
	
	protected void fadeOut() {
//		Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
//		fadeOut.setFillAfter(true);
//		mText.startAnimation(fadeOut);
	}

	protected void init() {
		if (mProviderAdapter != null && mProviderAdapter.isLoggedIn()) {
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
	
	@Override
	public void onResume() {
		super.onResume();
		init();
	}
	
	protected void setLoggedOut(){
		mLoginButton.setEnabled(true);
		mLoginButton.setText(R.string.button_log_in);
		if(mShowUserInfo) {	
//			fadeOut();
			mSubtext.setText(R.string.account_logged_out);
			mSubtext.setTextColor(getResources().getColor(R.color.text_primary));
			mUserNameView.setText(null);  
//			fadeIn();
		}

	}

	protected void setLoggedIn(){
		mLoginButton.setEnabled(true);
		mLoginButton.setText(R.string.button_log_out);
		if(mShowUserInfo) {	
//			fadeOut();
			mSubtext.setText(R.string.account_logged_in);
			mSubtext.setTextColor(getResources().getColor(R.color.text_primary));			
		}
	}
	
	protected void setOffline() {
		mLoginButton.setEnabled(false);	
		if(mShowUserInfo) {	
//			fadeOut();
			mSubtext.setText(R.string.account_offline);
			mSubtext.setTextColor(getResources().getColor(R.color.accent));
			mUserNameView.setText(null);
//			fadeIn();
		}
	}
	
	protected void setOnline() {
		mLoginButton.setEnabled(true);
	}	
}

