package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.ProviderAdapter;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Abstract class with basic logic for account fragments, e.g. handling state of
 * button and text.
 * 
 * @author chiljagossow
 * 
 */
public abstract class AccountFragment extends BaseFragment {

  protected TextView userNameView;
  protected TextView subtext;
  protected TextView headerText;
  protected Button loginButton;
  protected String name;
  protected View view;
  protected FrameLayout text;

  protected ProviderAdapter providerAdapter;

  @Override
  public void onResume() {
    super.onResume();
    updateState();
  }

  /**
   * Sets corresponding texts for logged in state
   */
  protected void setLoggedIn() {
    if (loginButton != null) {
      loginButton.setEnabled(true);
      loginButton.setText(R.string.button_log_out);
    }
    if (subtext != null) {
      subtext.setText(R.string.account_logged_in);
      subtext.setTextColor(getResources().getColor(R.color.text_primary));
    }
    if (userNameView != null) {
      userNameView.setText(name);
    }
    if (headerText != null) {
      headerText.setText(null);
    }
  }

  /**
   * Sets corresponding texts for logged out state
   */
  protected void setLoggedOut() {
    name = null;
    if (loginButton != null) {
      loginButton.setEnabled(true);
      loginButton.setText(R.string.button_log_in);
    }
    if (subtext != null) {
      subtext.setText(null);
    }
    if (userNameView != null) {
      userNameView.setText(null);
    }
  }

  /**
   * Disables login button and sets corresponding text for off line state
   */
  protected void setOffline() {
    if (loginButton != null) {
      loginButton.setEnabled(false);
    }
    if (subtext != null) {
      subtext.setText(null);
    }
    if (userNameView != null) {
      userNameView.setText(null);
    }

  }

  protected void setOnline() {
    if (loginButton != null) {
      loginButton.setEnabled(true);
    }
  }

  /**
   * Updates state to logged in/out or off line.
   */
  protected void updateState() {
    if (isResumed) {
      if ((providerAdapter != null) && providerAdapter.isLoggedIn()) {
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
  }
}
