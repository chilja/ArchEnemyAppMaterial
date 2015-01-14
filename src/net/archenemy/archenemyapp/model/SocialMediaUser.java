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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.model.GraphUser;

import twitter4j.User;

import java.util.ArrayList;

/**
 * <p>
 * Entity that holds data from social media, e.g. IDs, feeds and preference
 * keys.
 * </p>
 * 
 * @author chiljagossow
 */
public class SocialMediaUser {

  private final String name;
  private final Long twitterUserId;
  private final String facebookUserId;
  private final String prefKey;
  private GraphUser facebookUser;
  private User twitterUser;

  private ArrayList<Post> posts = new ArrayList<Post>();
  private ArrayList<Tweet> tweets = new ArrayList<Tweet>();

  /**
   * Creates new instance.
   * 
   * @param name
   *          Name of user
   * @param prefKey
   *          preference key to enable or disable user, user is enabled by
   *          default
   * @param userId
   *          unique id
   * @param twitterUserId
   *          user id of Twitter account (numeric)
   * @param facebookUserId
   *          user id of Facebook account (numeric)
   */
  public SocialMediaUser(String name, String prefKey, Long twitterUserId, String facebookUserId) {

    this.name = name;
    this.prefKey = prefKey;
    this.twitterUserId = twitterUserId;
    this.facebookUserId = facebookUserId;
  }

  public GraphUser getFacebookUser() {
    return facebookUser;
  }

  public String getFacebookUserId() {
    return facebookUserId;
  }

  public String getName() {
    return name;
  }

  public ArrayList<Post> getPosts() {
    return posts;
  }

  public ArrayList<Tweet> getTweets() {
    return tweets;
  }

  public User getTwitterUser() {
    return twitterUser;
  }

  public Long getTwitterUserId() {
    return twitterUserId;
  }

  /**
   * Checks if user is enabled via shared preferences
   * 
   * @param activity
   *          Activity for access to shared preferences
   * @return
   */
  public boolean isEnabled(Context context) {
    final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
    return pref.getBoolean(prefKey, true);
  }

  public void setFacebookUser(GraphUser facebookUser) {
    this.facebookUser = facebookUser;
  }

  /**
   * Sets the posts
   * 
   * @param posts
   */
  public void setPosts(ArrayList<Post> posts) {
    this.posts = posts;
  }

  /**
   * Sets the tweets
   * 
   * @param tweets
   */
  public void setTweets(ArrayList<Tweet> tweets) {
    this.tweets = tweets;
  }

  public void setTwitterUser(User twitterUser) {
    this.twitterUser = twitterUser;
  }
}
