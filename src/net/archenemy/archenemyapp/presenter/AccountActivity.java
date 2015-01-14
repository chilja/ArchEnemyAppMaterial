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

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.TwitterAdapter;
import net.archenemy.archenemyapp.view.SlidingTabLayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Activity holding fragments for log in/out processes with providers.
 * 
 * @author chiljagossow
 * 
 */
public class AccountActivity extends FacebookActivity implements
    FacebookAdapter.OnFacebookLoginListener, TwitterAdapter.TwitterLoginCallback {

  private static final int FACEBOOK = 0;
  private static final int TWITTER = 1;
  public static final String TAG = "AccountActivity";

  private FacebookAccountFragment facebookAccount;
  private TwitterAccountFragment twitterAccount;

  private SlidingTabLayout slidingTabLayout;
  private ViewPager viewPager;
  private BaseFragmentPagerAdapter adapter;

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (twitterAccount != null) {
      twitterAccount.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.account_activity);

    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    setSupportActionBar(toolbar);

    final TextView title = (TextView) findViewById(R.id.title);
    title.setText(R.string.title_activity_accounts);

    twitterAccount = new TwitterAccountFragment();
    facebookAccount = new FacebookAccountFragment();

    final BaseFragment[] fragments = new BaseFragment[2];
    fragments[FACEBOOK] = facebookAccount;
    fragments[TWITTER] = twitterAccount;

    viewPager = (ViewPager) findViewById(R.id.viewpager);
    adapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragments);
    viewPager.setAdapter(adapter);

    slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    slidingTabLayout.setIndicatorColor(getResources().getColor(R.color.accent));
    slidingTabLayout.setCustomTabView(R.layout.tab, R.id.tabIcon);
    slidingTabLayout.setViewPager(viewPager);
  }

  @Override
  public void onFacebookLogin() {
    if (adapter.getItem(FACEBOOK) instanceof FacebookAccountFragment) {
      ((FacebookAccountFragment) (adapter.getItem(FACEBOOK))).onFacebookLogin();
    }
  }

  @Override
  public void onTwitterLogin() {}
}
