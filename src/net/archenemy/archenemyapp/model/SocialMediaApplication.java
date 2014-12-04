package net.archenemy.archenemyapp.model;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

/**
 * <p>Manager for social media provider SDK</p>
 *
 * @author chiljagossow
 */
public class SocialMediaApplication extends Application {

	@Override
  public void onCreate() {
        super.onCreate();
  	final TwitterAuthConfig authConfig =
  			 new TwitterAuthConfig(TwitterAdapter.KEY, TwitterAdapter.SECRET);
  	Fabric.with(this, new Twitter(authConfig));
  	Fabric.with(this, new TwitterCore(authConfig), new Twitter(authConfig));
	}
}
