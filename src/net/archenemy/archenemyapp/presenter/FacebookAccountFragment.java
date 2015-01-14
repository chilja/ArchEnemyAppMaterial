/**
 * Copyright 2014-present Chilja Gossow.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.model.GraphUser;

/**
 * Facebook account fragment for log in and out.
 * 
 * @author chiljagossow
 * 
 */
public class FacebookAccountFragment extends AccountFragment implements
    FacebookAdapter.UserCallback {

  private final class OnClickListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
      if (FacebookAdapter.getInstance().isLoggedIn()) {
        // Log out dialog
        final String logout = getResources().getString(R.string.button_log_out);
        final String cancel = getResources().getString(R.string.button_cancel);

        String message = getResources().getString(R.string.account_logged_in) + ": "
            + FacebookAccountFragment.this.name;

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(message).setIcon(getIconResId()).setCancelable(true)
            .setPositiveButton(logout, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                FacebookAdapter.getInstance().logOut();
                setLoggedOut();
              }
            }).setNegativeButton(cancel, null);

        builder.create().show();

      } else {
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
    }
    catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must extend FacebookActivity");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    providerAdapter = FacebookAdapter.getInstance();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    super.onCreateView(inflater, container, savedInstanceState);
    view = inflater.inflate(R.layout.facebook_account_fragment, container, false);

    loginButton = (Button) view.findViewById(R.id.facebookButton);
    loginButton.setOnClickListener(new OnClickListener());

    headerText = (TextView) view.findViewById(R.id.headerText);
    userNameView = (TextView) view.findViewById(R.id.userNameView);
    subtext = (TextView) view.findViewById(R.id.subTextView);

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

    return view;
  }

  /**
   * Should be called from activity upon Facebook login to update state
   */
  public void onFacebookLogin() {
    if (FacebookAdapter.getInstance().isLoggedIn()) {
      // request user
      FacebookAdapter.getInstance().makeMeRequest(this);
    }
    updateState();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(Constants.FACEBOOK_USER_NAME, name);
  }

  @Override
  public void onUserRequestCompleted(GraphUser user, FacebookRequestError error) {
    if (error != null) {
      facebookActivity.handleError(error);
    }
    if (user != null) {
      name = user.getName();
      if (userNameView != null) {
        userNameView.setText(name);
      }
    }
  }

  @Override
  protected void setLoggedOut() {
    super.setLoggedOut();
    if (headerText != null) {
      headerText.setText(R.string.facebook_login_header);
    }
  }
}
