package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.FacebookAdapter;

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

import com.facebook.FacebookRequestError;
import com.facebook.model.GraphUser;

/**
 * Facebook account
 * @author chiljagossow
 *
 */
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
    	  FacebookAdapter.getInstance().logIn(getActivity());
    	}
    }
	}

	public static final String TAG = "FacebookAccountFragment";

	private FacebookActivity facebookActivity;

	@Override
  public int getIconResId() {
		return R.drawable.facebook_medium;
	}

	@Override
	public String getTAG() {
		return TAG;
	}

	@Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      facebookActivity = (FacebookActivity) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must extend FacebookActivity");
    }
  }

	@Override
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    providerAdapter = FacebookAdapter.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
	        ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
	  final View view = inflater.inflate(R.layout.facebook_account_fragment, container, false);

		loginButton = (Button) view.findViewById(R.id.facebookButton);
		loginButton.setOnClickListener(new OnClickListener());

	  text = (FrameLayout) view.findViewById(R.id.text);

	  if (showHeader) {
    	final View accountInfoView = inflater.inflate(R.layout.account_info, null);
    	headerText = (TextView) accountInfoView.findViewById(R.id.headerText);
    	headerText.setText(R.string.facebook_login_header);;
    	text.addView(accountInfoView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

		if (showUserInfo) {
			final View userInfoView = inflater.inflate(R.layout.account_user, null);
			userNameView = (TextView) userInfoView.findViewById(R.id.userNameView);
			subtext = (TextView) userInfoView.findViewById(R.id.subTextView);
			if (savedInstanceState != null) {
        name = savedInstanceState.getString(Constants.FACEBOOK_USER_NAME, name);
      }

			if (name != null) {
				userNameView.setText(name);
			} else {
				if (Utility.isConnectedToNetwork(getActivity(), false) && providerAdapter.isLoggedIn()) {
				  FacebookAdapter.getInstance().makeMeRequest(this);
				}
			}
			text.addView(userInfoView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		}

		return view;
	}

	/**
	 * Should be called from activity upon Facebook login to update state
	 */
	public void onFacebookLogin() {
    if (FacebookAdapter.getInstance().isLoggedIn()) {
    	//set the logged in state and request user
    	setLoggedIn();
      FacebookAdapter.getInstance().makeMeRequest(this);

    } else {
    	// set the logged out state
    	setLoggedOut();
    }
	}

  @Override
  public void onUserRequestCompleted(GraphUser user, FacebookRequestError error) {
    if (error != null) {
      facebookActivity.handleError(error);
    }
		if (user != null) {
			name = user.getName();
      userNameView.setText(name);
    }
	}
}
