package net.archenemy.archenemyapp.model;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.presenter.Post;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * <p>Adapter for the Facebook SDK</p>
 * @author chiljagossow
 */

public class FacebookAdapter
	implements ProviderAdapter{

  /**
   * <p>Callback interface for feed requests</p>
   * @author chiljagossow
   */
	public interface FeedCallback {
		void onFeedRequestCompleted(ArrayList<Post> elements, String id);
	}
	
	/**
   * <p>Callback interface for login process</p>
   * @author chiljagossow
   */
	public interface OnFacebookLoginListener {
		void onFacebookLogin();
	}
	
	/**
   * <p>Callback interface for user requests</p>
   * @author chiljagossow
   */
	public interface UserCallback {
		void onUserRequestCompleted(GraphUser user);
	}

	protected static final String TAG = "FacebookAdapter";

	// Activity code to flag an incoming activity result is due
	// to a new permissions request
	public static final int REAUTH_ACTIVITY_CODE = 100;

	/// List of additional write permissions being requested
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions", "public_profile");
	// Redirect URL for authentication errors requiring a user action
	private static final Uri FACEBOOK_URL = Uri.parse("http://m.facebook.com");
	
	// JSON Node names
	private static final String TAG_DATA = "data";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_PICTURE = "picture";
	private static final String TAG_LINK = "link";
	private static final String TAG_DATE = "created_time";
	private static final String TAG_FROM = "from";

	// flag for pending reauthorization request
	private boolean pendingPublishReauthorization = false;

	private static FacebookAdapter facebookAdapter;
	
	private com.facebook.widget.LoginButton facebookLoginButton;
	
	private FacebookAdapter(){
    // prevent instantiation from outside
  }
	
	/**
	 * Parses String containing a timestamp formatted as yyyy-MM-dd'T'hh:mm:ss+'0000' using Locale.US
	 * @param timestamp String formatted as yyyy-MM-dd'T'hh:mm:ss+'0000'
	 * @return Date Date object
	 */	
	public static Date getDate(String timestamp){
		final SimpleDateFormat ft =
			new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss+'0000'", Locale.US);
		Date date = null;
		try {
		  date = ft.parse(timestamp);
		} catch (final ParseException e) {
		  Log.e(TAG, timestamp + " unparseable using " + ft);
		}
		return date;
	}

	/**
	 * Returns singleton
	 * @return FacebookAdapter
	 */
	public static FacebookAdapter getInstance() {
		if (facebookAdapter == null) {
			facebookAdapter = new FacebookAdapter();
		}
		return facebookAdapter;
	}
	
	/**
	 * Checks for a valid token
	 * @return true if valid token is present
	 */
	public boolean hasValidToken() {
		//check for open facebook session
    final Session session = Session.getActiveSession();
    if (session != null) {
      final String token = session.getAccessToken();
      final Date expDate = session.getExpirationDate();
      final Date date = new Date();
      if ((token != null) && (date.before(expDate))) {
        return true;
      }
    }
		return false;
	}
	
	/**
	 * Checks if Facebook is enabled
	 */
	@Override
  public boolean isEnabled() {
		return true;
	}

	/**
	 * Checks if log in status
	 * @return API calls can be made if true
	 */
	@Override
  public boolean isLoggedIn() {
		//check for open facebook session
		final Session session = Session.getActiveSession();
		if ((session != null) && session.isOpened()) {
      return true;
    }
		return false;
	}
	
	/**
	 * Checks for a pending publish action
	 * @return
	 */
	public boolean isPendingPublish() {
		return this.pendingPublishReauthorization;
	}

	/**
	 * Starts log in process
	 * @param activity
	 */
	public void logIn (Activity activity) {
	  //widget to perform login
  	this.facebookLoginButton = new com.facebook.widget.LoginButton(activity);
  	this.facebookLoginButton.performClick();
	}

	/**
	 * Closes session and clears token
	 */
	@Override
	public void logOut() {
		final Session session = Session.getActiveSession();
		if (session!= null) {
			session.closeAndClearTokenInformation();
		}
	}
	
	/**
	 * Makes a feed request to the Facebook API
	 * @param Callback FeedCallback to be called when response arrives
	 * @param id Facebook user Id
	 * @param activity Activity from where the call is made
	 */
	public void makeFeedRequest(
	    final FeedCallback callback, final String id, final Activity activity){
		if (Utility.isConnectedToNetwork(activity, false)){
  		final Session session = Session.getActiveSession();
  		final StringBuffer query = new StringBuffer(id);
  		query.append("/feed");
  		// make the API call
  		final Request request = new Request(
		    session,
		    query.toString(),
		    null,
		    HttpMethod.GET,
		    new Request.Callback() {
	        @Override
          public void onCompleted(Response response) {
	        	Log.i(TAG, "Feeds received");
	        	if (response.getError() != null) {
	            	handleError(response.getError(), activity);
	            	callback.onFeedRequestCompleted(null, id);
	            	return;
	            }
	        	// If the response is successful
	        	if (session == Session.getActiveSession()) {
	        		//Evaluate response
	        	  final JSONObject graphResponse = response
	                                           .getGraphObject()
	                                           .getInnerJSONObject();
	        		final ArrayList<Post> elements = parseJson(graphResponse, activity);
	            callback.onFeedRequestCompleted(elements, id);
	           }
	        }
		    }
  		);
		Log.i(TAG, "Make feed request");
		request.executeAsync();
		}
	}

	/**
	 * Makes user request for the user currently logged in
	 * @param userCallback Callback for the response
	 * @param activity Activity from where the request is made
	 */
	public void makeMeRequest(UserCallback userCallback, final Activity activity) {
		if (Utility.isConnectedToNetwork(activity, false)){
			final Session session = Session.getActiveSession();
			final UserCallback callback = userCallback;
			if ((session != null) && session.isOpened()) {

		    // Make an API call to get user data and define a
		    // new callback to handle the response.
		    final Request request = Request.newMeRequest(session,
		            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	        	Log.i(TAG, "User response received");
            if (session == Session.getActiveSession()) {
                callback.onUserRequestCompleted(user);
            }
            if (response.getError() != null) {
            	handleError(response.getError(), activity);
            }
	        }
			    });
				Log.i(TAG, "Making user request");
			  request.executeAsync();
			}
		}
	}

	/**
	 * Makes user request for the given user id
	 * @param callback Callback for the response
	 * @param id Facebook user id
	 * @param activity Activity from where the request is made
	 */
	public void makeUserRequest(final UserCallback callback, final String id, final Activity activity) {
		
	  if (Utility.isConnectedToNetwork(activity, false)){
			final Session session = Session.getActiveSession();
			
			if ((session != null) && session.isOpened()) {

        final Callback wrapper = new Callback() {
          @Override
          public void onCompleted(Response response) {
          	Log.i(TAG, "User received");
          	if (response.getError() != null) {
              	handleError(response.getError(), activity);
              	return;
              }
          	// If the response is successful
          	if (session == Session.getActiveSession()) {
          		// Evaluate response
              final GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
              if (callback != null) {
              	callback.onUserRequestCompleted(graphUser);
              }
          	}
          }
        };
        
        final Request request = new Request(session, id, null, null, wrapper);
        request.executeAsync();
			}
		}
	}

	/**
	 * Publishes story to user timeline via custom feed dialog
	 * @param params Bundle with story values
	 * @param activity Activity from where the story is being published
	 */
	public void publishFeedDialog(Bundle params, final Activity activity) {
		if (Utility.isConnectedToNetwork(activity, true) && isLoggedIn()){

	    final WebDialog feedDialog = (
        new WebDialog.FeedDialogBuilder(activity,
           Session.getActiveSession(), params))
		       .setOnCompleteListener(new OnCompleteListener() {

          @Override
          public void onComplete(Bundle values,
            FacebookException error) {
            if (error == null) {
              // When the story is posted, echo the success
              // and the post Id.
              final String postId = values.getString("post_id");
              if (postId != null) {
                Toast.makeText(activity,
                    "Posted story, id: "+postId,
                    Toast.LENGTH_SHORT).show();
              } else {
                // User clicked the Cancel button
                Toast.makeText(activity.getApplicationContext(),
                    "Publish cancelled",
                    Toast.LENGTH_SHORT).show();
              }
              
            } else if (error instanceof FacebookOperationCanceledException) {
              // User clicked the "x" button
              Toast.makeText(activity.getApplicationContext(),
                  "Publish cancelled",
                  Toast.LENGTH_SHORT).show();
            } else {
              // Generic, ex: network error
              Toast.makeText(activity.getApplicationContext(),
                  "Error posting story",
                  Toast.LENGTH_SHORT).show();
            }
          }
        }
		  )
      .build();
	    
	    feedDialog.show();
		}
		
		if (!isLoggedIn()) {
			Toast.makeText(activity, R.string.fb_share_error_log_in, Toast.LENGTH_LONG).show();
		}
	}

	public void setPendingPublish(boolean pendingPublishReauthorization) {
		this.pendingPublishReauthorization = pendingPublishReauthorization;
	}

	/**
	 * Starts share dialog using Facebook Native App if installed, feed dialog otherwise
	 * @param shareParams Bundle with share parameters name, link, caption, description, picture
	 * @param activity Activity from where the share dialog is called
	 */
	public void startShareDialog(Bundle shareParams, Activity activity) {
		if (FacebookDialog.canPresentShareDialog(activity.getApplicationContext(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {

			// Publish the post using the Native Facebook Share Dialog
			final FacebookDialog.ShareDialogBuilder shareDialogBuilder = new FacebookDialog.ShareDialogBuilder(activity);
			shareDialogBuilder.setName(shareParams.getString("name"));
			shareDialogBuilder.setLink(shareParams.getString("link"));
			shareDialogBuilder.setCaption(shareParams.getString("caption"));
			shareDialogBuilder.setDescription(shareParams.getString("description"));
			shareDialogBuilder.setPicture(shareParams.getString("picture"));
			final FacebookDialog shareDialog = shareDialogBuilder.build();
			shareDialog.present();

		} else {
			//Publish the post using the custom share dialog
			publishFeedDialog(shareParams, activity);
		}
	}
	private void handleError(FacebookRequestError error, final Activity activity) {
	    DialogInterface.OnClickListener listener = null;
	    String dialogBody = null;

	    if (error == null) {
        // There was no response from the server.
        dialogBody = activity.getString(R.string.fb_error_dialog_default_text);
        
      // error handling  
	    } else {
        switch (error.getCategory()) {
          case AUTHENTICATION_RETRY:
            // Tell the user what happened by getting the
            // message id, and retry the operation later.
            final String userAction = (error.shouldNotifyUser()) ? "" :
            	activity.getString(error.getUserActionMessageId());
            dialogBody = activity.getString(R.string.fb_error_authentication_retry,
                                   userAction);
            listener = new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface,
                                  int i) {
                // Take the user to the mobile site.
                final Intent intent = new Intent(Intent.ACTION_VIEW,
                                           FACEBOOK_URL);
                activity.startActivity(intent);
              }
            };
            break;

          case AUTHENTICATION_REOPEN_SESSION:
            // Close the session and reopen it.
            dialogBody =
            		activity.getString(R.string.fb_error_authentication_reopen);
            listener = new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface,
                                  int i) {
                final Session session = Session.getActiveSession();
                if ((session != null) && !session.isClosed()) {
                    session.closeAndClearTokenInformation();
                }
              }
            };
            break;

          case PERMISSION:
            // A permissions-related error
            dialogBody = activity.getString(R.string.fb_error_permission);
            listener = new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface,
                                  int i) {
              	//new
                  setPendingPublish(true);
                  // Request publish permission
                  requestPublishPermissions(Session.getActiveSession(), activity);
              }
            };
            break;

          case SERVER:
            
          case THROTTLING:
              // This is usually temporary, don't clear the fields, and
              // ask the user to try again.
              dialogBody = activity.getString(R.string.fb_error_server);
              break;

          case BAD_REQUEST:
              // This is likely a coding error, ask the user to file a bug.
              dialogBody = activity.getString(R.string.fb_error_bad_request,
                                     error.getErrorMessage());
              break;

          case OTHER:
            
          case CLIENT:
            
          default:
            // An unknown issue occurred, this could be a code error, or
            // a server side issue, log the issue, and either ask the
            // user to retry, or file a bug.
            dialogBody = activity.getString(R.string.fb_error_unknown,
                                   error.getErrorMessage());
            break;
        }
	    }

	    // Show the error and pass in the listener so action
	    // can be taken, if necessary.
	    new AlertDialog.Builder(activity)
	            .setPositiveButton(R.string.fb_error_dialog_button_text, listener)
	            .setTitle(R.string.fb_error_dialog_title)
	            .setMessage(dialogBody)
	            .show();
	}
		
	private ArrayList<Post> parseJson (JSONObject jsonObj, Activity activity){

		final ArrayList<Post> feedElements = new ArrayList<Post>();
		JSONArray posts = null;
		Log.i(TAG, "Parse response...");
		if (jsonObj != null) {
	        try {
				posts = jsonObj.getJSONArray(TAG_DATA);
				for (int i = 0; i < posts.length(); i++) {
					try {

						final JSONObject object = posts.getJSONObject(i);

						final String date = object.getString(TAG_DATE);
						final String message = object.getString(TAG_MESSAGE);
						final String picture = object.getString(TAG_PICTURE);
						final String link = object.getString(TAG_LINK);
						final JSONObject fromObj = object.getJSONObject(TAG_FROM);
						final String name = fromObj.getString(TAG_NAME);
						final String id = fromObj.getString(TAG_ID);

						final Post element =
								new Post(activity,name, id, message, date, picture, link, null);
						feedElements.add(element);
					} catch (final JSONException e) {
						//ignore objects with missing tags
					}
				}
	        } catch (final JSONException e1) {
	        	//ignore objects with missing tags
	        }

		} else {
			Log.e(TAG, "Couldn't parse response");
		}
	  return feedElements;
	}

	private void requestPublishPermissions(Session session, Activity activity) {
    if (session != null) {
      final Session.NewPermissionsRequest newPermissionsRequest =
          new Session.NewPermissionsRequest(activity, PERMISSIONS).
              setRequestCode(REAUTH_ACTIVITY_CODE);
      session.requestNewPublishPermissions(newPermissionsRequest);
    }
	}
}


