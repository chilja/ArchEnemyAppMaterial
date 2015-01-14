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

package net.archenemy.archenemyapp.model;

import android.content.Context;
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
import java.util.Date;
import java.util.Locale;

/**
 * <p>
 * Adapter for the Facebook SDK: Logs in and out, makes calls to the Graph API
 * and parses response.
 * </p>
 * 
 * @author chiljagossow
 */

public class FacebookAdapter implements ProviderAdapter {

  /**
   * <p>
   * Callback interface for feed requests.
   * </p>
   * 
   * @author chiljagossow
   */
  public interface FeedCallback {
    void onFeedRequestCompleted(ArrayList<Post> posts, String id, FacebookRequestError error);
  }

  /**
   * <p>
   * Callback interface for login process.
   * </p>
   * 
   * @author chiljagossow
   */
  public interface OnFacebookLoginListener {
    void onFacebookLogin();
  }

  /**
   * <p>
   * Callback interface for user requests.
   * </p>
   * 
   * @author chiljagossow
   */
  public interface UserCallback {
    void onUserRequestCompleted(GraphUser user, FacebookRequestError error);
  }

  protected static final String TAG = "FacebookAdapter";

  // JSON Node names
  private static final String TAG_DATA = "data";
  private static final String TAG_MESSAGE = "message";
  private static final String TAG_ID = "id";
  private static final String TAG_NAME = "name";
  private static final String TAG_PICTURE = "picture";
  private static final String TAG_LINK = "link";
  private static final String TAG_DATE = "created_time";
  private static final String TAG_FROM = "from";

  // singleton
  private static FacebookAdapter facebookAdapter;

  /**
   * Parses String containing a timestamp formatted as yyyy-MM-dd'T'hh:mm:ssZZZ
   * using Locale.US. Adds time zone offset.
   * 
   * @param timestamp
   *          String formatted as yyyy-MM-dd'T'hh:mm:ss+ZZZ
   * @return Date parsed from string
   */
  public static Date getDate(String timestamp) {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZ", Locale.US);
    Date date = null;
    try {
      date = dateFormat.parse(timestamp);
    }
    catch (final ParseException e) {
      Log.e(TAG, timestamp + " unparseable using " + dateFormat);
    }
    return date;
  }

  /**
   * Returns singleton, creates instance if needed.
   * 
   * @return FacebookAdapter
   */
  public static FacebookAdapter getInstance() {
    if (facebookAdapter == null) {
      facebookAdapter = new FacebookAdapter();
    }
    return facebookAdapter;
  }

  private com.facebook.widget.LoginButton facebookLoginButton;

  private FacebookAdapter() {
    // prevent instantiation from outside
  }

  /**
   * Checks for a valid token.
   * 
   * @return true if valid token is present
   */
  public boolean hasValidToken() {
    // check for open facebook session
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
   * Checks whether Facebook is enabled.
   */
  @Override
  public boolean isEnabled() {
    // may be used in later release
    return true;
  }

  /**
   * Checks log in status. Returns true if session is opened and API calls can
   * be made.
   * 
   * @return True if API calls can be made.
   */
  @Override
  public boolean isLoggedIn() {
    // check for open facebook session
    final Session session = Session.getActiveSession();
    if ((session != null) && session.isOpened()) {
      return true;
    }
    return false;
  }

  /**
   * Starts log in process.
   * 
   * @param context
   *          Context for log in widget.
   */
  public void logIn(Context context) {
    // widget to perform login
    facebookLoginButton = new com.facebook.widget.LoginButton(context);
    facebookLoginButton.performClick();
  }

  /**
   * Closes session and clears token.
   */
  @Override
  public void logOut() {
    final Session session = Session.getActiveSession();
    if (session != null) {
      session.closeAndClearTokenInformation();
    }
  }

  /**
   * Makes a feed request to the Facebook Graph API.
   * 
   * @param Callback
   *          FeedCallback to be called when response arrives.
   * @param userId
   *          Facebook user Id for whom feed should be requested.
   */
  public void makeFeedRequest(final FeedCallback callback, final String userId) {
    final Session session = Session.getActiveSession();
    final StringBuffer query = new StringBuffer(userId);
    query.append("/feed");
    // make the API call
    final Request request = new Request(session, query.toString(), null, HttpMethod.GET,
        new Request.Callback() {
          @Override
          public void onCompleted(Response response) {
            Log.i(TAG, "Received feed for user id " + userId + ".");
            if (response.getError() != null) {
              callback.onFeedRequestCompleted(null, userId, response.getError());
              return;
            }
            // If the response is successful
            if (session == Session.getActiveSession()) {
              // Evaluate response
              final JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
              final ArrayList<Post> elements = parseJson(graphResponse, userId);
              callback.onFeedRequestCompleted(elements, userId, null);
            }
          }
        });
    Log.i(TAG, "Make feed request for user id " + userId + ".");
    request.executeAsync();
  }

  /**
   * Makes user request for the user currently logged in.
   * 
   * @param userCallback
   *          Callback for the response.
   */
  public void makeMeRequest(UserCallback userCallback) {
    final Session session = Session.getActiveSession();
    final UserCallback callback = userCallback;
    if ((session != null) && session.isOpened()) {

      // Make an API call to get user data and define a
      // new callback to handle the response.
      final Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
        @Override
        public void onCompleted(GraphUser user, Response response) {
          // If the response is successful
          Log.i(TAG, "Received response for user data request for logged in user.");
          if (session == Session.getActiveSession()) {
            callback.onUserRequestCompleted(user, null);
          }
          if (response.getError() != null) {
            callback.onUserRequestCompleted(null, response.getError());
          }
        }
      });
      Log.i(TAG, "Making user data request for logged in user.");
      request.executeAsync();
    }
  }

  /**
   * Makes user request for the given Facebook user id.
   * 
   * @param callback
   *          Callback for the response.
   * @param id
   *          Facebook user id.
   */
  public void makeUserRequest(final UserCallback callback, final String id) {
    final Session session = Session.getActiveSession();

    if ((session != null) && session.isOpened()) {

      final Callback wrapper = new Callback() {
        @Override
        public void onCompleted(Response response) {
          Log.i(TAG, "Received user data for user id " + id + ".");
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
      Log.i(TAG, "Make user data request for user id " + id + ".");
      request.executeAsync();
    }
  }

  // public void setPendingPublish(boolean pendingPublishReauthorization) {
  // this.pendingPublishReauthorization = pendingPublishReauthorization;
  // }

  private ArrayList<Post> parseJson(JSONObject jsonObj, String userId) {

    final ArrayList<Post> posts = new ArrayList<Post>();
    JSONArray data = null;
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
            final String postId = object.getString(TAG_ID);
            final JSONObject fromObj = object.getJSONObject(TAG_FROM);
            final String userName = fromObj.getString(TAG_NAME);
            final String fromUserId = fromObj.getString(TAG_ID);

            if (userId.equals(fromUserId)) {
              final Post post = new Post(userName, userId, message, getDate(date), picture, link,
                  postId);
              posts.add(post);
            }
          }
          catch (final JSONException e) {
            // ignore objects with missing tags
          }
        }
      }
      catch (final JSONException e1) {
        // ignore objects with missing tags
      }

    } else {
      Log.e(TAG, "Couldn't parse response.");
    }
    return posts;
  }
}
