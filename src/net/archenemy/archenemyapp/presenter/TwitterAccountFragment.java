package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.TwitterAdapter;
import net.archenemy.archenemyapp.model.TwitterAdapter.TwitterLoginCallback;
import net.archenemy.archenemyapp.model.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TwitterAccountFragment extends AccountFragment
	implements TwitterAdapter.TwitterLoginCallback{

	final class OnClickListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
    	if (TwitterAccountFragment.this.twitterAdapter.isLoggedIn()) {
    	  
    		String logout = getResources().getString(R.string.button_log_out);
        String cancel = getResources().getString(R.string.button_cancel);
        
        String message;
        if (TwitterAccountFragment.this.name != null) {
        	message = getResources().getString(R.string.account_logged_in) + ": " + TwitterAccountFragment.this.name;
        } else {
        	message = getResources().getString(R.string.account_logged_in);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(message)
        	.setIcon(getIconResId())
        	.setCancelable(true)
        	.setPositiveButton(logout, new DialogInterface.OnClickListener() {
        		@Override
            public void onClick(DialogInterface dialog, int which) {
            	   TwitterAccountFragment.this.providerAdapter.logOut();
            	   setLoggedOut();
           		}
            })
          .setNegativeButton(cancel, null);

        builder.create().show();

    	} else {
    		TwitterAccountFragment.this.twitterAdapter.logIn(getActivity(), TwitterAccountFragment.this);
    	}
    }
	}

	public static final String TAG = "TwitterAccountFragment";

	private TwitterAdapter twitterAdapter;

	private TwitterLoginCallback onLoginListener;

	@Override
  public int getIconResId() {
		return R.drawable.twitter;
	}

	@Override
	public String getTAG() {
		return TAG;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // Pass the activity result to the adapter.
    TwitterAdapter.getInstance().onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
        this.onLoginListener = (TwitterLoginCallback) activity;
    } catch (ClassCastException e) {
        throw new ClassCastException(activity.toString() + " must implement OnTwitterLoginListener");
    }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.twitterAdapter = TwitterAdapter.getInstance();
    this.providerAdapter = this.twitterAdapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
	    ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
	  View view = inflater.inflate(R.layout.twitter_account_fragment, container, false);

		// Twitter login button in the layout
		this.loginButton = (Button) view.findViewById(R.id.twitterButton);
		this.loginButton.setOnClickListener(new OnClickListener());

	  this.text = (FrameLayout) view.findViewById(R.id.text);

    if (this.showHeader) {
    	View accountInfoView = inflater.inflate(R.layout.account_info, null);
    	this.headerText = (TextView) accountInfoView.findViewById(R.id.headerText);
    	this.headerText.setText(R.string.twitter_login_header);
    	this.text.addView(accountInfoView, 0, new ViewGroup.LayoutParams(
    			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    if (this.showUserInfo) {
    	View userInfoView = inflater.inflate(R.layout.account_user, null);
    	this.userNameView = (TextView) userInfoView.findViewById(R.id.userNameView);
    	this.subtext = (TextView) userInfoView.findViewById(R.id.subTextView);
  		if (savedInstanceState != null) {
  			this.name = savedInstanceState.getString(Constants.TWITTER_USER_NAME, this.name);
  			this.userNameView.setText(this.name);
  		}
  		this.text.addView(userInfoView, 0, new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		}

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    bundle.putString(Constants.TWITTER_USER_NAME, this.name);
	}

	@Override
	public void onTwitterLogin() {
		setLoggedIn();
		this.onLoginListener.onTwitterLogin();
	}

	@Override
  protected void setLoggedIn() {
		if(this.showUserInfo) {
			super.setLoggedIn();
		    if (Utility.isConnectedToNetwork(getActivity(), false) && this.twitterAdapter.isLoggedIn()) {
		    	this.name = this.twitterAdapter.getUserName();
		    	this.userNameView.setText(this.name);
			}
		}
	}
}
