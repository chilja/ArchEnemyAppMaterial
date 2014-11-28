package net.archenemy.archenemyapp.model;

import io.fabric.sdk.android.Fabric;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import android.app.Application;

public class SocialMediaApplication extends Application {

	public void onCreate() {
        super.onCreate();
 
	 TwitterAuthConfig authConfig = 
			 new TwitterAuthConfig(TwitterAdapter.KEY, TwitterAdapter.SECRET);
	 Fabric.with(this, new Twitter(authConfig));
	 Fabric.with(this, new TwitterCore(authConfig), new Twitter(authConfig));
 
    }

}
