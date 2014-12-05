package net.archenemy.archenemyapp.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

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
		void onFeedRequestCompleted(ArrayList<Post> posts, String id, FacebookRequestError error);
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
		void onUserRequestCompleted(GraphUser user, FacebookRequestError error);
	}

	protected static final String TAG = "FacebookAdapter";

	// Activity code to flag an incoming activity result is due
	// to a new permissions request
	public static final int REAUTH_ACTIVITY_CODE = 100;

	/// List of additional write permissions being requested
	public static final List<String> PERMISSIONS = Arrays.asList("publish_actions", "public_profile");
	// Redirect URL for authentication errors requiring a user action
	public static final Uri FACEBOOK_URL = Uri.parse("http://m.facebook.com");

	// JSON Node names
	private static final String TAG_DATA = "data";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_PICTURE = "picture";
	private static final String TAG_LINK = "link";
	private static final String TAG_DATE = "created_time";
	private static final String TAG_FROM = "from";

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

	// flag for pending reauthorization request
	private boolean pendingPublishReauthorization = false;

	private static FacebookAdapter facebookAdapter;

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

	private com.facebook.widget.LoginButton facebookLoginButton;

	private FacebookAdapter(){
    // prevent instantiation from outside
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
		return pendingPublishReauthorization;
	}

	/**
	 * Starts log in process
	 * @param activity
	 */
	public void logIn (Context context) {
	  //widget to perform login
  	facebookLoginButton = new com.facebook.widget.LoginButton(context);
  	facebookLoginButton.performClick();
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
	 */
	public void makeFeedRequest(
    final FeedCallback callback, final String id){
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
            	callback.onFeedRequestCompleted(null, id, response.getError());
            	return;
            }
        	// If the response is successful
        	if (session == Session.getActiveSession()) {
        		//Evaluate response
        	  final JSONObject graphResponse = response
                                           .getGraphObject()
                                           .getInnerJSONObject();
        		final ArrayList<Post> elements = parseJson(graphResponse);
            callback.onFeedRequestCompleted(elements, id, null);
           }
        }
	    }
		);
  	Log.i(TAG, "Make feed request");
  	request.executeAsync();
  }

	/**
	 * Makes user request for the user currently logged in
	 * @param userCallback Callback for the response
	 */
	public void makeMeRequest(UserCallback userCallback) {
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
              callback.onUserRequestCompleted(user, null);
          }
          if (response.getError() != null) {
            callback.onUserRequestCompleted(null, response.getError());
          }
        }
		    });
			Log.i(TAG, "Making user request");
		  request.executeAsync();
		}
	}


	/**
	 * Makes user request for the given user id
	 * @param callback Callback for the response
	 * @param id Facebook user id
	 */
	public void makeUserRequest(final UserCallback callback, final String id) {
		final Session session = Session.getActiveSession();

		if ((session != null) && session.isOpened()) {

      final Callback wrapper = new Callback() {
        @Override
        public void onCompleted(Response response) {
        	Log.i(TAG, "User received");
        	if (response.getError() != null) {
        	  if (callback != null) {
        	    callback.onUserRequestCompleted(null, response.getError());
        	  }
            return;
          }
        	// If the response is successful
        	if (session == Session.getActiveSession()) {
        		// Evaluate response
            final GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
            if (callback != null) {
            	callback.onUserRequestCompleted(graphUser, null);
            }
        	}
        }
      };

      final Request request = new Request(session, id, null, null, wrapper);
      request.executeAsync();
		}
	}

	public void setPendingPublish(boolean pendingPublishReauthorization) {
		this.pendingPublishReauthorization = pendingPublishReauthorization;
	}

	private ArrayList<Post> parseJson (JSONObject jsonObj){

		final ArrayList<Post> posts = new ArrayList<Post>();
		JSONArray data = null;
		Log.i(TAG, "Parse response...");
		if (jsonObj != null) {
	        try {
				data = jsonObj.getJSONArray(TAG_DATA);
				for (int i = 0; i < data.length(); i++) {
					try {

						final JSONObject object = data.getJSONObject(i);

						final String date = object.getString(TAG_DATE);
						final String message = object.getString(TAG_MESSAGE);
						final String picture = object.getString(TAG_PICTURE);
						final String link = object.getString(TAG_LINK);
						final JSONObject fromObj = object.getJSONObject(TAG_FROM);
						final String name = fromObj.getString(TAG_NAME);
						final String id = fromObj.getString(TAG_ID);

						final Post post =
								new Post(name, id, message, date, picture, link, null);
						posts.add(post);
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
	  return posts;
	}
}


