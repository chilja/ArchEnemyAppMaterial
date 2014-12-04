package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.model.GraphUser;

public class FacebookAccountFragment extends AccountFragment
	implements FacebookAdapter.UserCallback{

	final class OnClickListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
    	if (FacebookAccountFragment.this.providerAdapter.isLoggedIn()) {
    	  // Log out dialog
    		final String logout = getResources().getString(R.string.button_log_out);
        final String cancel = getResources().getString(R.string.button_cancel);
        
        String message;
        if (FacebookAccountFragment.this.name != null) {
        	message = getResources().getString(R.string.account_logged_in) + ": " + FacebookAccountFragment.this.name;
        } else {
        	message = getResources().getString(R.string.account_logged_in);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(message)
      	  .setIcon(getIconResId())
      	  .setCancelable(true)
          .setPositiveButton(logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
          	   FacebookAccountFragment.this.providerAdapter.logOut();
          	   setLoggedOut();
         		}
          })
          .setNegativeButton(cancel, null);
        builder.create().show();
        
    	}else{
    	  // Log in process
    		FacebookAccountFragment.this.facebookAdapter.logIn(getActivity());
    	}
    }
	}
	
	public static final String TAG = "FacebookAccountFragment";

	protected FacebookAdapter facebookAdapter;

	@Override
  public int getIconResId() {
		return R.drawable.facebook_medium;
	}

	@Override
	public String getTAG() {
		return TAG;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.facebookAdapter = FacebookAdapter.getInstance();
    this.providerAdapter = this.facebookAdapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
	        ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
	  final View view = inflater.inflate(R.layout.facebook_account_fragment, container, false);

		this.loginButton = (Button) view.findViewById(R.id.facebookButton);
		this.loginButton.setOnClickListener(new OnClickListener());

	  this.text = (FrameLayout) view.findViewById(R.id.text);

	  if (this.showHeader) {
    	final View accountInfoView = inflater.inflate(R.layout.account_info, null);
    	this.headerText = (TextView) accountInfoView.findViewById(R.id.headerText);
    	this.headerText.setText(R.string.facebook_login_header);;
    	this.text.addView(accountInfoView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

		if (this.showUserInfo) {
			final View userInfoView = inflater.inflate(R.layout.account_user, null);
			this.userNameView = (TextView) userInfoView.findViewById(R.id.userNameView);
			this.subtext = (TextView) userInfoView.findViewById(R.id.subTextView);
			if (savedInstanceState != null) {
        this.name = savedInstanceState.getString(Constants.FACEBOOK_USER_NAME, this.name);
      }

			if (this.name != null) {
				this.userNameView.setText(this.name);
			} else {
				if (Utility.isConnectedToNetwork(getActivity(), false) && this.providerAdapter.isLoggedIn()) {
				  this.facebookAdapter.makeMeRequest(this, getActivity());
				}
			}
			this.text.addView(userInfoView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		}

		return view;
	}

	public void onFacebookLogin() {
    if (this.facebookAdapter.isLoggedIn()) {
    	//set the logged in state and request user 
    	setLoggedIn();
      this.facebookAdapter.makeMeRequest(this, getActivity());

    } else {
    	// set the logged out state
    	setLoggedOut();
    }
	}

	@Override
  public void onUserRequestCompleted(GraphUser user) {
		if (user != null) {
			this.name = user.getName();
      this.userNameView.setText(this.name);
    }
	}
}
