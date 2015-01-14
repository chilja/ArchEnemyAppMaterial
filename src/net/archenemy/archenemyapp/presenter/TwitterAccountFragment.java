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
import net.archenemy.archenemyapp.model.TwitterAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Twitter account fragment for log in and out.
 * 
 * @author chiljagossow
 * 
 */
public class TwitterAccountFragment extends AccountFragment implements
    TwitterAdapter.TwitterLoginCallback {

  final class OnClickListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
      if (TwitterAdapter.getInstance().isLoggedIn()) {

        String logout = getResources().getString(R.string.button_log_out);
        String cancel = getResources().getString(R.string.button_cancel);

        String message;
        if (TwitterAccountFragment.this.name != null) {
          message = getResources().getString(R.string.account_logged_in) + ": "
              + TwitterAccountFragment.this.name;
        } else {
          message = getResources().getString(R.string.account_logged_in);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(message).setIcon(getIconResId()).setCancelable(true)
            .setPositiveButton(logout, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                TwitterAccountFragment.this.providerAdapter.logOut();
                setLoggedOut();
              }
            }).setNegativeButton(cancel, null);

        builder.create().show();

      } else {
        TwitterAdapter.getInstance().logIn(getActivity(), BackgroundWorkerFragment.getInstance());
      }
    }
  }

  public static final String TAG = "TwitterAccountFragment";

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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    providerAdapter = TwitterAdapter.getInstance();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    super.onCreateView(inflater, container, savedInstanceState);
    view = inflater.inflate(R.layout.twitter_account_fragment, container, false);

    loginButton = (Button) view.findViewById(R.id.twitterButton);
    loginButton.setOnClickListener(new OnClickListener());
    headerText = (TextView) view.findViewById(R.id.headerText);
    userNameView = (TextView) view.findViewById(R.id.userNameView);
    subtext = (TextView) view.findViewById(R.id.subTextView);

    if (savedInstanceState != null) {
      name = savedInstanceState.getString(Constants.TWITTER_USER_NAME, name);
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
  }

  @Override
  protected void setLoggedIn() {
    super.setLoggedIn();
    if (Utility.isConnectedToNetwork(getActivity(), false)
        && TwitterAdapter.getInstance().isLoggedIn()) {
      name = TwitterAdapter.getInstance().getUserName();
      userNameView.setText(name);
    }
  }

  @Override
  protected void setLoggedOut() {
    super.setLoggedOut();
    if (headerText != null) {
      headerText.setText(R.string.twitter_login_header);
    }
  }
}
