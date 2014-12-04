package net.archenemy.archenemyapp.model;

/**
 * <p>Entity that holds data from social media</p>
 *
 * @author chiljagossow
 */

import net.archenemy.archenemyapp.presenter.Post;
import net.archenemy.archenemyapp.presenter.Tweet;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.model.GraphUser;

import twitter4j.User;

import java.util.ArrayList;
import java.util.Collections;

public class SocialMediaUser {

	private final String name;
	private final Long twitterUserId;
	private final String facebookUserId;
	private final String prefKey;
	private final int userId;
	private GraphUser facebookUser;
	private User twitterUser;

	private ArrayList<Post> posts = new ArrayList<Post>();
	private ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	
	/**
	 * Creates new instance
	 * @param name Name of user
	 * @param prefKey preference key to enable or disable user
	 * @param userId unique id 
	 * @param twitterUserId user id of Twitter account (numeric)
	 * @param facebookUserId user id of Facebook account (numeric)
	 */
	public SocialMediaUser(String name,
			String prefKey,
			int userId,
			Long twitterUserId,
			String facebookUserId){

		this.name = name;
		this.userId = userId;
		this.prefKey = prefKey;
		this.twitterUserId = twitterUserId;
		this.facebookUserId = facebookUserId;
	}

	public GraphUser getFacebookUser() {
		return this.facebookUser;
	}

	public String getFacebookUserId() {
		return this.facebookUserId;
	}

	public String getName() {
		return this.name;
	}

	public ArrayList<Post> getPosts() {
		return this.posts;
	}

	public ArrayList<Tweet> getTweets() {
		return this.tweets;
	}

	public User getTwitterUser() {
		return this.twitterUser;
	}

	public Long getTwitterUserId() {
		return this.twitterUserId;
	}

	public int getUserId() {
		return this.userId;
	}

	/**
	 * Checks if user is enabled vie shared preferences
	 * @param activity Activity for access to shared preferences
	 * @return
	 */
	public boolean isEnabled(Activity activity) {
		final SharedPreferences pref =
		        PreferenceManager.getDefaultSharedPreferences(activity);
			return pref.getBoolean(this.prefKey, true);
	}
	
	public void setFacebookUser(GraphUser facebookUser) {
		this.facebookUser = facebookUser;
	}

	/**
	 * Sets the posts and sorts them ascending according to date
	 * @param posts
	 */
	public void setPosts(ArrayList<Post> posts) {
		this.posts = posts;
		Collections.sort(this.posts);
	}
	
	/**
   * Sets the tweets and sorts them ascending according to date
   * @param posts
   */
	public void setTweets(ArrayList<Tweet> tweets) {
		this.tweets = tweets;
		Collections.sort(this.tweets);
	}

	public void setTwitterUser(User twitterUser) {
		this.twitterUser = twitterUser;
	}
}
