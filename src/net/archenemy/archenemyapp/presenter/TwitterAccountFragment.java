package net.archenemy.archenemyapp.presenter;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import net.archenemy.archenemyapp.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.TwitterAdapter;
import net.archenemy.archenemyapp.model.Utility;

public class TwitterAccountFragment extends AccountFragment {
	
	public static final int TITLE = R.string.title_twitter;	
	public static final String TAG = "TwitterAccountFragment";
	
	private TwitterAdapter mTwitterAdapter;
	
	private TwitterLoginButton mTwitterLoginButton;
	
	private OnTwitterLoginListener mOnLoginListener;
	
	public int getTitle() {
		return TITLE;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	public int getIconResId() {
		return R.drawable.twitter;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
            mOnLoginListener = (OnTwitterLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTwitterLoginListener");
        }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mTwitterAdapter = TwitterAdapter.getInstance();
	    mProviderAdapter = mTwitterAdapter;
	  //widget to perform login
  		mTwitterLoginButton = new TwitterLoginButton(getActivity());	
  		mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
  			@Override
  			public void success(Result<TwitterSession> result) {
  				setLoggedIn();
  				mOnLoginListener.onTwitterLogin();
  			}
  			
  			@Override
  			public void failure(TwitterException exception) {
  			// Do something on failure
  			}
  		});	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.twitter_account_fragment, container, false);
	    		
		// Find the twitter login button in the layout
		mLoginButton = (Button) view.findViewById(R.id.twitterButton);
		mLoginButton.setOnClickListener(new OnClickListener());
	    
	    mText = (FrameLayout) view.findViewById(R.id.text);
	    
	    if (mShowHeader) {
	    	View accountInfoView = inflater.inflate(R.layout.account_info, null);
	    	mHeaderText = (TextView) accountInfoView.findViewById(R.id.headerText);
	    	mHeaderText.setText(R.string.twitter_login_header);
	    	mText.addView(accountInfoView, 0, new ViewGroup.LayoutParams(
	    			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	    }
	    
	    if(mShowUserInfo) {	
	    	View userInfoView = inflater.inflate(R.layout.account_user, null);		    
			mUserNameView = (TextView) userInfoView.findViewById(R.id.userNameView);
			mSubtext = (TextView) userInfoView.findViewById(R.id.subTextView);
			if (savedInstanceState != null) {
				mName = savedInstanceState.getString(Constants.TWITTER_USER_NAME, mName);
				mUserNameView.setText(mName);
				fadeIn();
			}
			mText.addView(userInfoView, 0, new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));						
		}

		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	 
	    // Pass the activity result to the login button.
	    mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
	}
	
	
	protected void setLoggedIn() {
		 if(mShowUserInfo) {
		super.setLoggedIn();		    
	    if (Utility.isConnectedToNetwork(getActivity(), false) && mTwitterAdapter.isLoggedIn()) {
	    	mName = mTwitterAdapter.getUserName();
	    	mUserNameView.setText(mName);
	    	fadeIn();
		 } 
		 }
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    bundle.putString(Constants.TWITTER_USER_NAME, mName);
	}
	
	final class OnClickListener implements View.OnClickListener {

	    @Override
	    public void onClick(View view) {
	    	if (mTwitterAdapter.isLoggedIn()) {
	    		String logout = getResources().getString(R.string.button_log_out);
                String cancel = getResources().getString(R.string.button_cancel);
                String message;
                if (mName != null) {
                	message = getResources().getString(R.string.account_logged_in) + ": " + mName;
                } else {
                	message = getResources().getString(R.string.account_logged_in);
                }
	    		
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                
                builder.setMessage(message)
                       .setCancelable(true)
                       .setPositiveButton(logout, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                        	   mTwitterAdapter.logOut();
                        	   setLoggedOut();		
                       		}
                           }
                       )
                       .setNegativeButton(cancel, null);
                
                builder.create().show();
	    		
	    	}else{
	    		mTwitterLoginButton.performClick(); 
	    	}
	    }		
	}
	public interface OnTwitterLoginListener {
		void onTwitterLogin();
	}
}
