package net.archenemy.archenemyapp.model;

import net.archenemy.archenemyapp.R;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import twitter4j.MediaEntity;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * Adapter for the Twitter SDK and other: Logs in and out, makes calls to the API.
 * </p>
 * 
 * @author chiljagossow
 */
public class TwitterAdapter implements ProviderAdapter {
  /**
   * <p>
   * Callback interface for feed requests.
   * </p>
   * 
   * @author chiljagossow
   */
  public interface FeedCallback {
    void onFeedRequestCompleted(ArrayList<Tweet> elements, Long id);
  }

  /**
   * <p>
   * Callback interface for login process.
   * </p>
   * 
   * @author chiljagossow
   */
  public interface TwitterLoginCallback {
    void onTwitterLogin();
  }

  /**
   * <p>
   * Callback interface for user requests.
   * </p>
   * 
   * @author chiljagossow
   */
  public interface UserCallback {
    void onUserRequestCompleted(User user);
  }

  private class FeedTask extends AsyncTask<Void, Void, ArrayList<Tweet>> {

    private final FeedCallback callback;
    private final Long id;
    private Twitter twitter;

    private FeedTask(FeedCallback callback, Long id, Context context) {
      this.callback = callback;
      this.id = id;
      twitter = getAuthorizedTwitterInstance(context);
    }

    private ArrayList<Tweet> getTweets(List<twitter4j.Status> statuses) {

      final ArrayList<Tweet> tweets = new ArrayList<Tweet>();

      for (final twitter4j.Status status : statuses) {
        if (status.isRetweet()) {
          // ignore retweets
          continue;
        }

        final URLEntity[] urlEntities = status.getURLEntities();

        String link = null;
        if ((urlEntities != null) && (urlEntities.length > 0)) {
          final URLEntity url = urlEntities[0];
          link = url.getExpandedURL();
        }

        String url = null;
        final MediaEntity[] media = status.getMediaEntities();
        for (final MediaEntity entity : media) {
          url = entity.getMediaURL();
          entity.getType();
          break;
        }

        // use media url as link if no other link is provided
        if (link == null) {
          link = url;
        }

        Tweet tweet = new Tweet(status.getId(), status.getUser().getScreenName(), status.getText(),
            status.getCreatedAt(), link, status.getUser().getBiggerProfileImageURL());

        if (url != null) {
          tweet.setImageUrl(url);
        }
        tweets.add(tweet);
      }
      return tweets;
    }

    @Override
    protected ArrayList<Tweet> doInBackground(Void... params) {
      try {
        return getTweets(twitter.getUserTimeline(id));
      }
      catch (final TwitterException te) {
        te.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Tweet> elements) {
      feedTasks.remove(this);
      callback.onFeedRequestCompleted(elements, id);
    }
  }

  private class UserTask extends AsyncTask<Long, Void, User> {

    private UserCallback callback;
    private Twitter twitter;

    private UserTask(UserCallback callback, Context context) {
      this.callback = callback;
      twitter = getAuthorizedTwitterInstance(context);
    }

    @Override
    protected User doInBackground(Long... params) {
      try {
        return twitter.showUser(params[0]);
      }
      catch (final TwitterException exception) {
        exception.printStackTrace();
        Log.e(TAG, "User could not be retrieved: " + params[0]);
      }
      return null;
    }

    @Override
    protected void onPostExecute(User user) {
      userTasks.remove(this);
      callback.onUserRequestCompleted(user);
    }
  }

  public static final String TAG = "TwitterAdapter";

  private static String key;

  private static String secret;

  /**
   * Returns singleton
   * 
   * @return TwitterAdapter
   */
  public static TwitterAdapter getInstance() {
    if (twitterAdapter == null) {
      twitterAdapter = new TwitterAdapter();
    }
    return twitterAdapter;
  }

  private final ArrayList<FeedTask> feedTasks = new ArrayList<FeedTask>();

  private final ArrayList<UserTask> userTasks = new ArrayList<UserTask>();

  private TwitterLoginButton twitterLoginButton;

  private static TwitterAdapter twitterAdapter;

  /**
   * Returns the application key.
   * @return the key
   */
  public static String getKey(Context context) {
    if (key == null) {
      key = context.getString(R.string.twitter_key);
    }
    return key;
  }

  /**
   * Returns the application secret.
   * @return the secret
   */
  public static String getSecret(Context context) {
    if (secret == null) {
      secret = context.getString(R.string.twitter_secret);
    }
    return secret;
  }

  private TwitterAdapter() {
    // prevent instantiation from outside
  }

  /**
   * Gets the name of logged in user from session.
   * @return String with user name
   */
  public String getUserName() {
    final TwitterSession session = com.twitter.sdk.android.Twitter.getSessionManager()
        .getActiveSession();
    return session.getUserName();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean isLoggedIn() {
    if (com.twitter.sdk.android.Twitter.getSessionManager().getActiveSession() != null) {
      return true;
    }
    return false;
  }

  /**
   * Starts log in process.
   * 
   * @param context
   *          Context for log in window
   * @param loginListener
   */
  public void logIn(Context context, final TwitterLoginCallback loginListener) {
    twitterLoginButton = new TwitterLoginButton(context);

    twitterLoginButton.setCallback(new Callback<TwitterSession>() {
      @Override
      public void failure(com.twitter.sdk.android.core.TwitterException arg0) {}

      @Override
      public void success(Result<TwitterSession> result) {
        loginListener.onTwitterLogin();
      }
    });

    twitterLoginButton.performClick();
  }

  @Override
  public void logOut() {
    com.twitter.sdk.android.Twitter.getSessionManager().clearActiveSession();
  }

  /**
   * Makes a feed request to the Twitter API for the given user id.
   * 
   * @param id
   *          Twitter user id
   * @param callback
   *          Callback for response
   * @param context
   *          Context of request
   */
  public void makeFeedRequest(Long id, final FeedCallback callback, Context context) {
    if (isEnabled() && isLoggedIn()) {
      Log.d(TAG, "Requesting feed for id " + id);
      final FeedTask task = new FeedTask(callback, id, context);
      feedTasks.add(task);
      task.execute();
    }
  }

  /**
   * Makes a user request to the Twitter API for the given user id.
   * 
   * @param userId
   *          Twitter user id
   * @param callback
   *          Callback for response
   * @param context
   *          Context of request
   */
  public void makeUserRequest(Long userId, UserCallback callback, Context context) {
    if (isLoggedIn()) {
      Log.i(TAG, "Requesting user ...");
      final UserTask task = new UserTask(callback, context);
      userTasks.add(task);
      task.execute(userId);
    }
  }

  /**
   * Passes the activity's result for the handling of the active session after
   * log in.
   * 
   * @param requestCode
   * @param resultCode
   * @param data
   */
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Pass the activity result to the login button.
    if (twitterLoginButton != null) {
      twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
  }

  /**
   * Cancels all background tasks. Should be called from the activity's
   * onDestroy() method
   */
  public void onDestroy() {
    if (!feedTasks.isEmpty()) {
      for (FeedTask task : feedTasks) {
        task.cancel(true);
      }
      feedTasks.clear();
    }
    Log.i(TAG, "All feed tasks cancelled");

    if (!userTasks.isEmpty()) {
      for (UserTask task : userTasks) {
        task.cancel(true);
      }
      userTasks.clear();
    }
    Log.i(TAG, "All user tasks cancelled");
  }

  private Twitter getAuthorizedTwitterInstance(Context context) {

    TwitterAuthToken authToken = com.twitter.sdk.android.Twitter.getSessionManager()
        .getActiveSession().getAuthToken();

    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.setDebugEnabled(true).setOAuthConsumerKey(getKey(context))
        .setOAuthConsumerSecret(getSecret(context)).setOAuthAccessToken(authToken.token)
        .setOAuthAccessTokenSecret(authToken.secret);

    return new TwitterFactory(configurationBuilder.build()).getInstance();
  }
}