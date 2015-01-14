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

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

/**
 * <p>
 * Initializes social media provider SDK.
 * </p>
 * 
 * @author chiljagossow
 */
public class SocialMediaApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    final TwitterAuthConfig authConfig = new TwitterAuthConfig(TwitterAdapter.getKey(this),
        TwitterAdapter.getSecret(this));
    Fabric.with(this, new Twitter(authConfig));
    Fabric.with(this, new TwitterCore(authConfig), new Twitter(authConfig));
  }
}
