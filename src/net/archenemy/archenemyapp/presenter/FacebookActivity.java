package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.FacebookAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.FacebookRequestError;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 * Activity that handles Facebook SDK UI lifecycle and user interaction.
 */

public abstract class FacebookActivity extends ActionBarActivity implements
    FacebookAdapter.OnFacebookLoginListener {

  // Redirect URL for authentication errors requiring a user action
  public static final Uri FACEBOOK_URL = Uri.parse("http://m.facebook.com");

  protected boolean pendingLogin = false;

  // Facebook lifecycle helper
  protected UiLifecycleHelper mUiHelper;

  protected Session.StatusCallback callback = new Session.StatusCallback() {
    @Override
    public void call(Session session, SessionState state, Exception exception) {
      onFacebookLogin();
    }
  };

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mUiHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUiHelper = new UiLifecycleHelper(this, callback);
    if (Utility.isConnectedToNetwork(this, false)) {
      mUiHelper.onCreate(savedInstanceState);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mUiHelper.onDestroy();
  }

  @Override
  public void onPause() {
    super.onPause();
    mUiHelper.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    mUiHelper.onResume();
  }

  protected void handleError(FacebookRequestError error) {
    DialogInterface.OnClickListener listener = null;
    String dialogBody = null;

    if (error == null) {
      // There was no response from the server.
      dialogBody = getString(R.string.fb_error_dialog_default_text);

      // error handling
    } else {
      switch (error.getCategory()) {
        case AUTHENTICATION_RETRY:
          // Tell the user what happened by getting the
          // message id, and retry the operation later.
          final String userAction = (error.shouldNotifyUser()) ? "" : getString(error
              .getUserActionMessageId());
          dialogBody = getString(R.string.fb_error_authentication_retry, userAction);
          listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              // Take the user to the mobile site.
              final Intent intent = new Intent(Intent.ACTION_VIEW, FACEBOOK_URL);
              startActivity(intent);
            }
          };
          break;

        case AUTHENTICATION_REOPEN_SESSION:
          // Close the session and reopen it.
          dialogBody = getString(R.string.fb_error_authentication_reopen);
          listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              final Session session = Session.getActiveSession();
              if ((session != null) && !session.isClosed()) {
                session.closeAndClearTokenInformation();
              }
            }
          };
          break;

        case PERMISSION:
          // A permissions-related error
          dialogBody = getString(R.string.fb_error_permission);
          break;

        case SERVER:

        case THROTTLING:
          // This is usually temporary, don't clear the fields, and
          // ask the user to try again.
          dialogBody = getString(R.string.fb_error_server);
          break;

        case BAD_REQUEST:
          // This is likely a coding error, ask the user to file a bug.
          dialogBody = getString(R.string.fb_error_bad_request, error.getErrorMessage());
          break;

        case OTHER:

        case CLIENT:

        default:
          // An unknown issue occurred.
          dialogBody = getString(R.string.fb_error_unknown, error.getErrorMessage());
          break;
      }
    }

    // Show the error
    new AlertDialog.Builder(this).setIcon(R.drawable.facebook_medium)
        .setPositiveButton(R.string.fb_error_dialog_button_text, listener)
        .setTitle(R.string.fb_error_dialog_title).setMessage(dialogBody).show();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mUiHelper.onSaveInstanceState(outState);
  }
}
