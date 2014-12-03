package net.archenemy.archenemyapp.model;

import net.archenemy.archenemyapp.presenter.FeedElement;
import net.archenemy.archenemyapp.presenter.Post;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.model.GraphUser;

import twitter4j.User;

import java.util.ArrayList;
import java.util.Collections;

public class SocialMediaUser {

	private final String mName;
	private final String mTwitterUserId;
	private final String mFacebookUserId;
	private final String mPrefKey;
	private final int mUserId;
	private GraphUser mFacebookUser;
	private User mTwitterUser;

	private ArrayList<Post> mPosts = new ArrayList<Post>();
	private ArrayList<FeedElement> mTweets = new ArrayList<FeedElement>();

	public SocialMediaUser(String name,
			String prefKey,
			int userId,
			String twitterUserId,
			String facebookUserId){

		this.mName = name;
		this.mUserId = userId;
		this.mPrefKey = prefKey;
		this.mTwitterUserId = twitterUserId;
		this.mFacebookUserId = facebookUserId;
	}

	public GraphUser getFacebookUser() {
		return this.mFacebookUser;
	}

	public String getFacebookUserId() {
		return this.mFacebookUserId;
	}

	public String getName() {
		return this.mName;
	}

	public ArrayList<Post> getPosts() {
		return this.mPosts;
	}

	public ArrayList<FeedElement> getTweets() {
		return this.mTweets;
	}

	public User getTwitterUser() {
		return this.mTwitterUser;
	}

	public Long getTwitterUserId() {
		return Long.valueOf(this.mTwitterUserId);
	}

	public int getUserId() {
		return this.mUserId;
	}

	public boolean isEnabled(Activity activity) {
		final SharedPreferences pref =
		        PreferenceManager.getDefaultSharedPreferences(activity);
			return pref.getBoolean(this.mPrefKey, true);
	}

	public void setFacebookUser(GraphUser facebookUser) {
		this.mFacebookUser = facebookUser;
	}

	public void setPosts(ArrayList<Post> posts) {
		this.mPosts = posts;
		Collections.sort(this.mPosts);
	}

	public void setTweets(ArrayList<FeedElement> tweets) {
		this.mTweets = tweets;
		Collections.sort(this.mTweets);
	}

	public void setTwitterUser(User twitterUser) {
		this.mTwitterUser = twitterUser;
	}
}
