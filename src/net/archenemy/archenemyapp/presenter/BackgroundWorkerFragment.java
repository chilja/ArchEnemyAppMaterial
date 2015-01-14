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

import net.archenemy.archenemyapp.model.DataAdapter;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Post;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import net.archenemy.archenemyapp.model.Tweet;
import net.archenemy.archenemyapp.model.TwitterAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.model.GraphUser;

import twitter4j.User;

import java.util.ArrayList;

/**
 * Retained fragment without UI for the handling of background tasks, e.g. user
 * and feed requests from providers.
 * 
 * @author chiljagossow
 * 
 */
public class BackgroundWorkerFragment extends Fragment implements TwitterAdapter.FeedCallback,
    TwitterAdapter.UserCallback, TwitterAdapter.TwitterLoginCallback, FacebookAdapter.FeedCallback,
    FacebookAdapter.UserCallback, FacebookAdapter.OnFacebookLoginListener {

  /**
   * Callbacks for feed requests and log in process.
   * 
   * @author chiljagossow
   * 
   */
  public static interface ProviderRequestCallback {
    public void onFacebookFeedReceived();

    public void onFacebookLogin();

    public void onTwitterFeedReceived();

    public void onTwitterLogin();
  }

  public static final String TAG = "BackgroundWorkerFragment";

  // Twitter
  private static Integer twitterCallbackCount = 0;
  private static Integer twitterCallbackTotal = 0;;

  // Facebook
  private static Integer facebookCallbackCount = 0;
  private static Integer facebookCallbackTotal = 0;

  public static BackgroundWorkerFragment getInstance() {
    if (workerFragment == null) {
      workerFragment = new BackgroundWorkerFragment();
    }
    return workerFragment;
  }

  // callbacks
  private ProviderRequestCallback callback;
  private Activity activity;

  private Context context;
  private ArrayList<SocialMediaUser> users;

  private static BackgroundWorkerFragment workerFragment;
  // flags
  private boolean isAttached = false;
  private boolean pendingTwitterUserRequest = false;
  private boolean pendingFacebookUserRequest = false;
  private boolean pendingTwitterFeedRequest = false;

  private boolean pendingFacebookFeedRequest = false;

  private BackgroundWorkerFragment() {}

  /**
   *
   */
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity != null) {
      callback = (ProviderRequestCallback) activity;
      this.activity = activity;
      users = DataAdapter.getInstance().getSocialMediaUsers(activity);
      isAttached = true;
      context = activity.getApplicationContext();

      // make pending requests
      if (pendingTwitterUserRequest) {
        pendingTwitterUserRequest = false;
        requestTwitterUsers();
      }
      if (pendingFacebookUserRequest) {
        pendingFacebookUserRequest = false;
        requestFacebookUsers();
      }
      if (pendingTwitterFeedRequest) {
        pendingTwitterFeedRequest = false;
        requestTwitterFeed();
      }
      if (pendingFacebookFeedRequest) {
        pendingFacebookFeedRequest = false;
        requestFacebookFeed();
      }
    }
  }

  /**
   * This method will only be called once when the retained Fragment is first
   * created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Retain this fragment across configuration changes.
    setRetainInstance(true);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    // cancel background threads
    BitmapUtility.onDestroy();
    TwitterAdapter.getInstance().onDestroy();
  }

  /**
   * Set the callback to null so we don't accidentally leak the Activity
   * instance.
   */
  @Override
  public void onDetach() {
    super.onDetach();
    callback = null;
    activity = null;
    isAttached = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.archenemy.archenemyapp.model.FacebookAdapter.OnFacebookLoginListener
   * #onFacebookLogin()
   */
  @Override
  public void onFacebookLogin() {
    requestFacebookUsers();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.archenemy.archenemyapp.model.FacebookAdapter.FeedCallback#
   * onFeedRequestCompleted(java.util.ArrayList, java.lang.String,
   * com.facebook.FacebookRequestError)
   */
  @Override
  public void onFeedRequestCompleted(ArrayList<Post> posts, String id, FacebookRequestError error) {
    synchronized (facebookCallbackCount) {
      facebookCallbackCount += 1;
      if (isAttached) {
        if (error != null) {
          if (activity instanceof FacebookActivity) {
            ((FacebookActivity) activity).handleError(error);
          }
          return;
        }
      }
      if ((users != null) && (users.size() > 0)) {

        if ((posts != null) && (id != null) && (posts.size() > 0)) {

          for (SocialMediaUser user : users) {
            if (id.equals(user.getFacebookUserId())) {
              user.setPosts(posts);
              break;
            }
          }
        }

        if ((facebookCallbackCount == facebookCallbackTotal) && (callback != null)) {
          callback.onFacebookFeedReceived();
        }

        Log.i(TAG, "Received Facebook feed for Facebook user id " + id + ".");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.archenemy.archenemyapp.model.TwitterAdapter.FeedCallback#
   * onFeedRequestCompleted(java.util.ArrayList, java.lang.Long)
   */
  @Override
  public void onFeedRequestCompleted(ArrayList<Tweet> tweets, Long id) {
    if ((users != null) && (users.size() > 0)) {
      synchronized (twitterCallbackCount) {
        if ((tweets != null) && (id != null) && (tweets.size() > 0)) {
          for (SocialMediaUser user : users) {
            if (id.equals(user.getTwitterUserId())) {
              user.setTweets(tweets);
              break;
            }
          }
        }
        twitterCallbackCount += 1;
        if ((twitterCallbackCount == twitterCallbackTotal) && (callback != null)) {
          callback.onTwitterFeedReceived();
        }
        Log.i(TAG, "Received Twitter feed for twitter user id " + id.toString() + ".");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.archenemy.archenemyapp.model.TwitterAdapter.TwitterLoginCallback#
   * onTwitterLogin()
   */
  @Override
  public void onTwitterLogin() {
    requestTwitterUsers();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.archenemy.archenemyapp.model.FacebookAdapter.UserCallback#
   * onUserRequestCompleted(com.facebook.model.GraphUser,
   * com.facebook.FacebookRequestError)
   */
  @Override
  public void onUserRequestCompleted(GraphUser graphUser, FacebookRequestError error) {
    if (isAttached) {
      if (activity instanceof FacebookActivity) {
        ((FacebookActivity) activity).handleError(error);
      }
      return;
    }
    if (context != null) {
      String userId = graphUser.getId();
      for (SocialMediaUser user : users) {
        if (user.getFacebookUserId().equals(userId)) {
          user.setFacebookUser(graphUser);
          FacebookAdapter.getInstance().makeFeedRequest(this, user.getFacebookUserId());
          facebookCallbackTotal++;
          break;
        }
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.archenemy.archenemyapp.model.TwitterAdapter.UserCallback#
   * onUserRequestCompleted(twitter4j.User)
   */
  @Override
  public void onUserRequestCompleted(User twitterUser) {
    if ((context != null) && (users != null) && (users.size() > 0)) {
      Long userId = twitterUser.getId();
      for (SocialMediaUser user : users) {
        if (user.getTwitterUserId().equals(userId)) {
          user.setTwitterUser(twitterUser);
          TwitterAdapter.getInstance().makeFeedRequest(userId, this, context);
          twitterCallbackTotal++;
          break;
        }
      }
    }
  }

  /**
   * Make feed request for all enabled {@link SocialMediaUser SocialMediaUser}
   * to Facebook API
   */
  public void requestFacebookFeed() {
    if (context != null) {
      facebookCallbackCount = 0;
      facebookCallbackTotal = 0;
      for (SocialMediaUser user : DataAdapter.getInstance().getEnabledSocialMediaUsers(context)) {
        FacebookAdapter.getInstance().makeFeedRequest(this, user.getFacebookUserId());
        facebookCallbackTotal += 1;
      }
    } else {
      pendingFacebookFeedRequest = true;
    }
  }

  /**
   * Make user data request for all enabled {@link SocialMediaUser
   * SocialMediaUser} to Facebook API
   */
  public void requestFacebookUsers() {
    if ((users != null) && (users.size() > 0)) {
      for (SocialMediaUser user : users) {
        if (user.getFacebookUser() == null) {
          FacebookAdapter.getInstance().makeUserRequest(this, user.getFacebookUserId());
        }
      }
    } else {
      pendingFacebookUserRequest = true;
    }
  }

  /**
   * Make feed request for all enabled {@link SocialMediaUser SocialMediaUser}
   * to Twitter API
   */
  public void requestTwitterFeed() {
    if (context != null) {
      twitterCallbackTotal = 0;
      twitterCallbackCount = 0;
      for (SocialMediaUser user : DataAdapter.getInstance().getEnabledSocialMediaUsers(context)) {
        TwitterAdapter.getInstance().makeFeedRequest(user.getTwitterUserId(), this, context);
        twitterCallbackTotal += 1;
      }
    } else {
      pendingTwitterFeedRequest = true;
    }
  }

  /**
   * Make user data request for all enabled {@link SocialMediaUser
   * SocialMediaUser} to Twitter API
   */
  public void requestTwitterUsers() {
    if ((context != null) && (users != null) && (users.size() > 0)) {
      for (SocialMediaUser user : users) {
        if (user.getTwitterUser() == null) {
          TwitterAdapter.getInstance().makeUserRequest(user.getTwitterUserId(), this, context);
        }
      }
    } else {
      pendingTwitterUserRequest = true;
    }
  }
}
