package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.FacebookAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

/**
 * Activity that handles Facebook user interaction
 */

public abstract class FacebookActivity extends ActionBarActivity implements
    FacebookAdapter.OnFacebookLoginListener {

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

  /**
   * Starts share dialog using Facebook Native App if installed, feed dialog
   * otherwise
   * 
   * @param shareParams
   *          Bundle with share parameters name, link, caption, description,
   *          picture
   */
  public void startShareDialog(Bundle shareParams) {
    if (FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {

      // Publish the post using the Native Facebook Share Dialog
      final FacebookDialog.ShareDialogBuilder shareDialogBuilder = new FacebookDialog.ShareDialogBuilder(
          this);
      shareDialogBuilder.setName(shareParams.getString("name"));
      shareDialogBuilder.setLink(shareParams.getString("link"));
      shareDialogBuilder.setCaption(shareParams.getString("caption"));
      shareDialogBuilder.setDescription(shareParams.getString("description"));
      shareDialogBuilder.setPicture(shareParams.getString("picture"));
      final FacebookDialog shareDialog = shareDialogBuilder.build();
      shareDialog.present();

    } else {
      // Publish the post using the custom share dialog
      publishFeedDialog(shareParams, this);
    }
  }

  /**
   * Publishes story to user timeline via custom feed dialog
   * 
   * @param params
   *          Bundle with story values
   * @param context
   */
  private void publishFeedDialog(Bundle params, final Context context) {
    if (FacebookAdapter.getInstance().isLoggedIn()) {

      final WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(context,
          Session.getActiveSession(), params)).setOnCompleteListener(new OnCompleteListener() {

        @Override
        public void onComplete(Bundle values, FacebookException error) {
          if (error == null) {
            // When the story is posted, echo the success
            // and the post Id.
            final String postId = values.getString("post_id");
            if (postId != null) {
              Toast.makeText(context, "Posted story, id: " + postId, Toast.LENGTH_SHORT).show();
            } else {
              // User clicked the Cancel button
              Toast.makeText(context, "Publish cancelled", Toast.LENGTH_SHORT).show();
            }

          } else if (error instanceof FacebookOperationCanceledException) {
            // User clicked the "x" button
            Toast.makeText(context, "Publish cancelled", Toast.LENGTH_SHORT).show();
          } else {
            // Generic, ex: network error
            Toast.makeText(context, "Error posting story", Toast.LENGTH_SHORT).show();
          }
        }
      }).build();

      feedDialog.show();
    }

    if (!FacebookAdapter.getInstance().isLoggedIn()) {
      Toast.makeText(context, R.string.fb_share_error_log_in, Toast.LENGTH_LONG).show();
    }
  }

  private void requestPublishPermissions(Session session) {
    if (session != null) {
      final Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
          this, FacebookAdapter.PERMISSIONS).setRequestCode(FacebookAdapter.REAUTH_ACTIVITY_CODE);
      session.requestNewPublishPermissions(newPermissionsRequest);
    }
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
              final Intent intent = new Intent(Intent.ACTION_VIEW, FacebookAdapter.FACEBOOK_URL);
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
          listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              // new
              FacebookAdapter.getInstance().setPendingPublish(true);
              // Request publish permission
              requestPublishPermissions(Session.getActiveSession());
            }
          };
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
