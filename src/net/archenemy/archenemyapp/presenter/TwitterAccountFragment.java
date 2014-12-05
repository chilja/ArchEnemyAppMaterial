package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.TwitterAdapter;
import net.archenemy.archenemyapp.model.TwitterAdapter.TwitterLoginCallback;

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
    	if (TwitterAdapter.getInstance().isLoggedIn()) {

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
    		TwitterAdapter.getInstance().logIn(getActivity(), TwitterAccountFragment.this);
    	}
    }
	}

	public static final String TAG = "TwitterAccountFragment";

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
      onLoginListener = (TwitterLoginCallback) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnTwitterLoginListener");
    }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    providerAdapter = TwitterAdapter.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
	    ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
	  View view = inflater.inflate(R.layout.twitter_account_fragment, container, false);

		loginButton = (Button) view.findViewById(R.id.twitterButton);
		loginButton.setOnClickListener(new OnClickListener());

	  text = (FrameLayout) view.findViewById(R.id.text);

    if (showHeader) {
    	View accountInfoView = inflater.inflate(R.layout.account_info, null);
    	headerText = (TextView) accountInfoView.findViewById(R.id.headerText);
    	headerText.setText(R.string.twitter_login_header);
    	text.addView(accountInfoView, 0, new ViewGroup.LayoutParams(
    			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    if (showUserInfo) {
    	View userInfoView = inflater.inflate(R.layout.account_user, null);
    	userNameView = (TextView) userInfoView.findViewById(R.id.userNameView);
    	subtext = (TextView) userInfoView.findViewById(R.id.subTextView);
  		if (savedInstanceState != null) {
  			name = savedInstanceState.getString(Constants.TWITTER_USER_NAME, name);
  			userNameView.setText(name);
  		}
  		text.addView(userInfoView, 0, new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		}

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    bundle.putString(Constants.TWITTER_USER_NAME, name);
	}

	@Override
	public void onTwitterLogin() {
		setLoggedIn();
		onLoginListener.onTwitterLogin();
	}

	@Override
  protected void setLoggedIn() {
		if(showUserInfo) {
			super.setLoggedIn();
		    if (Utility.isConnectedToNetwork(getActivity(), false) && TwitterAdapter.getInstance().isLoggedIn()) {
		    	name = TwitterAdapter.getInstance().getUserName();
		    	userNameView.setText(name);
			}
		}
	}
}
