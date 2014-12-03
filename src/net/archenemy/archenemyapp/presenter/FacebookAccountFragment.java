package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.FacebookAdapter.OnFacebookLoginListener;
import net.archenemy.archenemyapp.model.Utility;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.model.GraphUser;
import android.support.v7.app.ActionBarActivity;
import android.widget.FrameLayout;

public class FacebookAccountFragment extends AccountFragment 
	implements FacebookAdapter.UserCallback{
	
	public static final String TAG = "FacebookAccountFragment";
	protected static final int TITLE = R.string.title_facebook;

	protected FacebookAdapter mFacebookAdapter;
	
	public int getTitle() {
		return TITLE;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	public int getIconResId() {
		return R.drawable.facebook_medium;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
//		try {
//            mOnLoginListener = (OnFacebookLoginListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement OnFacebookLoginListener");
//        }
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mFacebookAdapter = FacebookAdapter.getInstance();
	    mProviderAdapter = mFacebookAdapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.facebook_account_fragment, container, false);
	        
		// Find the facebook login button
		mLoginButton = (Button) view.findViewById(R.id.facebookButton);
		mLoginButton.setOnClickListener(new OnClickListener());
		
	    mText = (FrameLayout) view.findViewById(R.id.text);
	    	    
	    if (mShowHeader) {
	    	View accountInfoView = inflater.inflate(R.layout.account_info, null);
	    	mHeaderText = (TextView) accountInfoView.findViewById(R.id.headerText);
	    	mHeaderText.setText(R.string.facebook_login_header);;
	    	mText.addView(accountInfoView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	    }
		
		if(mShowUserInfo) {	
			View userInfoView = inflater.inflate(R.layout.account_user, null);		    
			mUserNameView = (TextView) userInfoView.findViewById(R.id.userNameView);
			mSubtext = (TextView) userInfoView.findViewById(R.id.subTextView);
			if (savedInstanceState != null)
				mName = savedInstanceState.getString(Constants.FACEBOOK_USER_NAME, mName);
			
			if (mName != null) { 			
				mUserNameView.setText(mName);
			} else {		
				if (Utility.isConnectedToNetwork(getActivity(), false) && mProviderAdapter.isLoggedIn()) {
				    mFacebookAdapter.makeMeRequest(this, getActivity());
				 }
			}
			mText.addView(userInfoView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		}
		
		return view;
	}
	
	public void onFacebookLogin() {
	    if (mFacebookAdapter.isLoggedIn()) {	    	
	    	//set the logged in state
	    	setLoggedIn();
	        mFacebookAdapter.makeMeRequest(this, getActivity()); 
	        
	    } else {	    	
	    	// set the logged out state
	    	setLoggedOut();
	    }	    
	}
	
	public void onUserRequestCompleted(GraphUser user) {
		if (user != null) {
            // Set the text to the user's name.
			mName = user.getName();
            mUserNameView.setText(mName);
            fadeIn();
        }	
	}
	
	final class OnClickListener implements View.OnClickListener {

	    @Override
	    public void onClick(View view) {
	    	if (mProviderAdapter.isLoggedIn()) {
	    		String logout = getResources().getString(R.string.button_log_out);
                String cancel = getResources().getString(R.string.button_cancel);
                String message;
                if (mName != null) {
                	message = getResources().getString(R.string.account_logged_in) + ": " + mName;
                } else {
                	message = getResources().getString(R.string.account_logged_in);
                }
	    		
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                
                builder.setTitle(message)
                	.setIcon(getIconResId())
                       .setCancelable(true)
                       .setPositiveButton(logout, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                        	   mProviderAdapter.logOut();
                        	   setLoggedOut();		
                       		}
                           }
                       )
                       .setNegativeButton(cancel, null);
                builder.create().show();
	    		
	    	}else{
	    		mFacebookAdapter.logIn(getActivity()); 
	    	}
	    }		
	}	
}
