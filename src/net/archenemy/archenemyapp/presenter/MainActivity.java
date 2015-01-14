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
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.TwitterAdapter;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * Main Activity of ArchEnemyApp. Manages which fragment will be displayed,
 * controls animation, and coordinates feed requests.
 * 
 * @author chiljagossow
 * 
 */
public class MainActivity extends FacebookActivity implements
    TwitterPageFragment.OnRefreshFeedListener, TwitterPageFragment.OnScrolledListener,
    FacebookAdapter.OnFacebookLoginListener, FacebookPageFragment.OnRefreshFeedListener,
    FacebookPageFragment.OnScrolledListener, BackgroundWorkerFragment.ProviderRequestCallback {

  public static final String TAG = "MainActivity";

  // menu positions = main fragment index
  private static final int FACEBOOK = 0;
  private static final int TWITTER = 1;
  private static final int FACEBOOK_LOGIN = 3;
  private static final int TWITTER_LOGIN = 4;
  private static int selectedMenuItem;

  private boolean isResumed = false;

  private Toolbar toolbar;

  private FrameLayout tabsBackground;
  private FrameLayout background;
  private LinearLayout tabs;
  private ImageView headerImage;
  private FrameLayout fragmentContainer;

  private int toolbarHeight;
  private int tabHeight;
  private Integer maxTabTranslationY;
  private Integer currentTabTranslationY;

  private static final float maxAlpha = 0.8F;

  // fragments
  private FacebookPageFragment facebookPageFragment;
  private FacebookAccountFragment facebookAccountFragment;
  private TwitterPageFragment twitterPageFragment;
  private TwitterAccountFragment twitterAccountFragment;

  private BackgroundWorkerFragment workerFragment;

  // Twitter
  // flag to prevent repeated automatic refresh
  private static boolean twitterIsRefreshed = false;
  private FrameLayout twitterTab;
  private ImageView twitterIcon;

  // Facebook
  // flag to prevent repeated automatic refresh
  private static boolean facebookIsRefreshed = false;
  private FrameLayout facebookTab;
  private ImageView facebookIcon;

  public int getContainerWidth() {
    return fragmentContainer.getWidth();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (twitterAccountFragment != null) {
      twitterAccountFragment.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    assignViews();

    twitterPageFragment = (TwitterPageFragment) getSupportFragmentManager().findFragmentByTag(
        TwitterPageFragment.TAG);
    facebookPageFragment = (FacebookPageFragment) getSupportFragmentManager().findFragmentByTag(
        FacebookPageFragment.TAG);
    twitterAccountFragment = (TwitterAccountFragment) getSupportFragmentManager()
        .findFragmentByTag(TwitterAccountFragment.TAG);
    facebookAccountFragment = (FacebookAccountFragment) getSupportFragmentManager()
        .findFragmentByTag(FacebookAccountFragment.TAG);
    workerFragment = (BackgroundWorkerFragment) getSupportFragmentManager().findFragmentByTag(
        BackgroundWorkerFragment.TAG);

    // initialize fragments
    if (twitterPageFragment == null) {
      twitterPageFragment = new TwitterPageFragment();
    }

    if (facebookPageFragment == null) {
      facebookPageFragment = new FacebookPageFragment();
    }

    if (twitterAccountFragment == null) {
      twitterAccountFragment = new TwitterAccountFragment();
    }

    if (facebookAccountFragment == null) {
      facebookAccountFragment = new FacebookAccountFragment();
    }

    if (workerFragment == null) {
      workerFragment = BackgroundWorkerFragment.getInstance();
      getSupportFragmentManager().beginTransaction()
          .add(workerFragment, BackgroundWorkerFragment.TAG).commit();
    }

    initTranslationYValues();

    if (savedInstanceState != null) {
      selectedMenuItem = savedInstanceState.getInt(Constants.MENU_ITEM);
      twitterIsRefreshed = savedInstanceState.getBoolean(Constants.TWITTER_IS_REFRESHED, false);
      facebookIsRefreshed = savedInstanceState.getBoolean(Constants.FACEBOOK_IS_REFRESHED, false);
      currentTabTranslationY = savedInstanceState.getInt(Constants.TAB_TRANSLATION, -tabHeight);
    } else {
      // get start screen from preferences
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      String startScreen = sharedPreferences
          .getString(Constants.PREF_KEY_START, Constants.FACEBOOK);
      selectedMenuItem = (Constants.FACEBOOK.equals(startScreen)) ? FACEBOOK : TWITTER;
      currentTabTranslationY = maxTabTranslationY;
      getFacebookUsers();
      getTwitterUsers();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.archenemy.archenemyapp.presenter.BackgroundWorkerFragment.
   * ProviderRequestCallback#onFacebookFeedReceived()
   */
  @Override
  public void onFacebookFeedReceived() {
    refreshFacebookMenuItem();
    Log.i(TAG, "Received facebook feeds");
  }

  @Override
  public void onFacebookLogin() {
    // callback from Facebook log in process
    refreshFacebookMenuItem();
    facebookIsRefreshed = false;
    refreshFacebookFeed();
  }

  @Override
  public void onFacebookPageScrolled(int scrollY, int dy) {
    if (selectedMenuItem == FACEBOOK) {
      onPageScrolled(scrollY, dy);
    }
  }

  @Override
  public void onFacebookPageScrollStateChanged(int scrollY, int dy) {
    if (selectedMenuItem == FACEBOOK) {
      onPageScrollStateChanged(scrollY, dy);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // handle toolbar events
    switch (item.getItemId()) {

      case R.id.actionRefreshTwitter:
        // set flag to false to allow manual refresh
        twitterIsRefreshed = false;
        refreshTwitterFeed();
        return true;

      case R.id.actionRefreshFacebook:
        // set flag to false to allow manual refresh
        facebookIsRefreshed = false;
        refreshFacebookFeed();
        return true;

      case R.id.actionSettings:
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
        return true;

      case R.id.actionAccounts:
        Intent accountsIntent = new Intent(this, AccountActivity.class);
        startActivity(accountsIntent);
        return true;

      default:
        // if event has not been handled, then pass it on
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    isResumed = false;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    menu.clear();

    if (getVisibleFragment() == twitterPageFragment) {
      inflater.inflate(R.menu.twitter, menu);
      return true;
    }

    if (getVisibleFragment() == facebookPageFragment) {
      inflater.inflate(R.menu.facebook, menu);
      return true;
    }

    // default
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public void onRefreshFacebookFeed() {
    facebookIsRefreshed = false;
    refreshFacebookFeed();
  }

  @Override
  public void onRefreshTwitterFeed() {
    twitterIsRefreshed = false;
    refreshTwitterFeed();
  }

  @Override
  public void onResume() {
    super.onResume();
    isResumed = true;
    showMenuItem(selectedMenuItem);
  }

  @Override
  public void onSaveInstanceState(Bundle bundle) {
    // save current state
    bundle.putInt(Constants.TAB_TRANSLATION, currentTabTranslationY);
    bundle.putInt(Constants.MENU_ITEM, getVisibleMenuItem());
    bundle.putBoolean(Constants.TWITTER_IS_REFRESHED, twitterIsRefreshed);
    bundle.putBoolean(Constants.FACEBOOK_IS_REFRESHED, facebookIsRefreshed);

    super.onSaveInstanceState(bundle);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.archenemy.archenemyapp.presenter.BackgroundWorkerFragment.
   * ProviderRequestCallback#onTwitterFeedReceived()
   */
  @Override
  public void onTwitterFeedReceived() {
    refreshTwitterMenuItem();
    Log.i(TAG, "Received twitter feeds");
  }

  @Override
  public void onTwitterLogin() {
    // callback from Twitter log in process
    refreshTwitterMenuItem();
    getTwitterUsers();
  }

  @Override
  public void onTwitterPageScrolled(int scrollY, int dy) {
    if (selectedMenuItem == TWITTER) {
      onPageScrolled(scrollY, dy);
    }
  }

  @Override
  public void onTwitterPageScrollStateChanged(int scrollY, int dy) {
    if (selectedMenuItem == TWITTER) {
      onPageScrollStateChanged(scrollY, dy);
    }
  }

  private void animateHeaderTransition(int menuIndex, boolean showTabs) {
    if (isResumed) {

      // tabs background alpha
      float alpha = (float) (maxTabTranslationY - currentTabTranslationY) / (maxTabTranslationY);

      // background alpha
      float backgroundAlpha = (alpha > maxAlpha) ? maxAlpha : alpha;

      // tool bar translation
      float translationY = toolbar.getTranslationY();

      if (toolbar.getAnimation() != null) {
        toolbar.getAnimation().cancel();
      }

      // animations
      if (currentTabTranslationY > toolbarHeight) {
        if (translationY < 0) {
          // show

          animateYTranslation(toolbar, 0, 300);
        }
      } else {
        // hide
        if (translationY != -toolbarHeight) {
          animateYTranslation(toolbar, -toolbarHeight, 300);
        }
      }
      cancelTabsAnimation();
      animateTabsTranslation();
      tabsBackground.animate().alpha(alpha).setDuration(300).start();
      background.animate().alpha(backgroundAlpha).setDuration(300).start();
      updateToolbar();
    }
  }

  private void animateTabsTranslation() {
    if (isResumed) {
      animateYTranslation(tabs, currentTabTranslationY, 300);
      animateYTranslation(tabsBackground, (currentTabTranslationY - maxTabTranslationY), 300);
    }
  }

  private void animateYTranslation(final View view, final int y, int duration) {

    if (view.getAnimation() != null) {
      return;
    }

    AnimatorListener listener = new AnimatorListener() {
      @Override
      public void onAnimationCancel(Animator animation) {
        view.setTranslationY(y);
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        // make sure the animation ends in the correct end position
        if (view == tabs) {
          view.setTranslationY(currentTabTranslationY);
          return;
        }
        if (view == tabsBackground) {
          view.setTranslationY(currentTabTranslationY - maxTabTranslationY);
          return;
        }
        view.setTranslationY(y);
      }

      @Override
      public void onAnimationRepeat(Animator animation) {}

      @Override
      public void onAnimationStart(Animator animation) {}

    };

    view.animate().translationY(y).setInterpolator(new AccelerateDecelerateInterpolator())
        .setDuration(duration).setListener(listener).start();

  }

  /**
   * 
   */
  private void assignViews() {
    setContentView(R.layout.main_activity);

    facebookIcon = (ImageView) findViewById(R.id.facebookIcon);
    facebookTab = (FrameLayout) findViewById(R.id.facebookTab);
    facebookTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        currentTabTranslationY = calculateTabTranslationY(facebookPageFragment.getScrollY(), false);
        showMenuItem(FACEBOOK);
      }
    });

    twitterIcon = (ImageView) findViewById(R.id.twitterIcon);
    twitterTab = (FrameLayout) findViewById(R.id.twitterTab);
    twitterTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        currentTabTranslationY = calculateTabTranslationY(twitterPageFragment.getScrollY(), false);
        showMenuItem(TWITTER);
      }
    });

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    // no title
    toolbar.setTitle("");
    setSupportActionBar(toolbar);

    tabs = (LinearLayout) findViewById(R.id.tabs);
    tabsBackground = (FrameLayout) findViewById(R.id.tabsBackground);
    background = (FrameLayout) findViewById(R.id.background);

    fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);

    headerImage = (ImageView) findViewById(R.id.headerImage);
    int width = 0;
    int height = 0;
    // in portrait orientation, display band picture in 16:9 format
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      width = getResources().getDisplayMetrics().widthPixels;
      height = (width * 9) / 16;
    } else {
      // in landscape orientation, display band picture full screen
      width = getResources().getDisplayMetrics().widthPixels;
      height = getResources().getDisplayMetrics().heightPixels;
    }
    LayoutParams params = new LayoutParams(width, height);
    headerImage.setLayoutParams(params);
    BitmapUtility.loadBitmap(this, R.drawable.band, headerImage, width, height);

  }

  private Integer calculateTabTranslationY(int scrollY, boolean showTabs) {
    int minTranslation = -tabHeight;
    if (showTabs) {
      // scrolling up -> show tabs
      minTranslation = 0;
    }
    if ((maxTabTranslationY + scrollY) <= minTranslation) {
      return minTranslation;
    }
    if ((maxTabTranslationY + scrollY) < 0) {
      // show
      return 0;
    }
    // translate
    return maxTabTranslationY + scrollY;
  }

  private void cancelTabsAnimation() {
    if (tabs.getAnimation() != null) {
      tabs.getAnimation().cancel();
    }
    if (tabsBackground.getAnimation() != null) {
      tabsBackground.getAnimation().cancel();
    }
  }

  private void clearBackStack() {
    // Get the number of entries in the back stack
    int backStackSize = getSupportFragmentManager().getBackStackEntryCount();
    // Clear the back stack
    if (backStackSize > 0) {
      for (int i = 0; i < backStackSize; i++) {
        getSupportFragmentManager().popBackStack();
      }
    }
  }

  private int determineFragmentIndex(int menuIndex) {
    int index;

    switch (menuIndex) {
      case FACEBOOK:
        if (FacebookAdapter.getInstance().hasValidToken()) {
          index = FACEBOOK;
        } else {
          index = FACEBOOK_LOGIN;
        }
        break;

      case TWITTER:
        if (TwitterAdapter.getInstance().isLoggedIn()) {
          index = TWITTER;
        } else {
          index = TWITTER_LOGIN;
        }
        break;

      default:
        index = -1;
    }

    return index;
  }

  private void getFacebookUsers() {
    // check network connection
    if (Utility.isConnectedToNetwork(this, true)) {
      if (!facebookIsRefreshed && FacebookAdapter.getInstance().isLoggedIn()) {
        workerFragment.requestFacebookUsers();
      }
    }
  }

  private BaseFragment getFragment(int index) {
    BaseFragment fragment;
    switch (index) {
      case FACEBOOK:
        fragment = facebookPageFragment;
        break;
      case FACEBOOK_LOGIN:
        fragment = facebookAccountFragment;
        break;
      case TWITTER:
        fragment = twitterPageFragment;
        break;
      case TWITTER_LOGIN:
        fragment = twitterAccountFragment;
        break;
      default:
        fragment = null;
    }
    return fragment;
  }

  private void getTwitterUsers() {
    // check network connection
    if (Utility.isConnectedToNetwork(this, true)) {
      if (!twitterIsRefreshed && TwitterAdapter.getInstance().isLoggedIn()) {
        workerFragment.requestTwitterUsers();
      }
    }
  }

  private BaseFragment getVisibleFragment() {
    if (twitterPageFragment.isVisible()) {
      return twitterPageFragment;
    }
    if (twitterAccountFragment.isVisible()) {
      return twitterAccountFragment;
    }
    if (facebookPageFragment.isVisible()) {
      return facebookPageFragment;
    }
    if (facebookAccountFragment.isVisible()) {
      return facebookAccountFragment;
    }
    return null;
  }

  private int getVisibleMenuItem() {
    if (twitterPageFragment.isVisible() || twitterAccountFragment.isVisible()) {
      return TWITTER;
    }
    if (facebookPageFragment.isVisible() || facebookAccountFragment.isVisible()) {
      return FACEBOOK;
    }
    return -1;
  }

  private void initTranslationYValues() {
    tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
    toolbarHeight = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
    maxTabTranslationY = getResources().getDimensionPixelSize(R.dimen.max_tab_translation_y);
  }

  private void onPageScrolled(Integer scrollY, int dy) {
    boolean showTabs = false;
    if (dy < 0) {
      showTabs = true;
    }
    int previousTabTranslation = currentTabTranslationY;
    currentTabTranslationY = calculateTabTranslationY(scrollY, showTabs);
    updateTabsBackgroundAlpha();
    updateBackgroundAlpha();
    updateToolbar();
    if (((previousTabTranslation == 0) && (currentTabTranslationY == -tabHeight))
        || ((previousTabTranslation == -tabHeight) && (currentTabTranslationY == 0))) {
      // show or hide tabs
      animateTabsTranslation();
    } else if (previousTabTranslation != currentTabTranslationY) {
      // translation has changed
      cancelTabsAnimation();
      translateTabs();
    }
  }

  private void onPageScrollStateChanged(Integer scrollY, int dy) {}

  private void refreshFacebookFeed() {
    // check network connection
    if (Utility.isConnectedToNetwork(this, true)) {
      if (!facebookIsRefreshed && FacebookAdapter.getInstance().isLoggedIn()) {
        workerFragment.requestFacebookFeed();
        // set flag
        facebookIsRefreshed = true;
        facebookPageFragment.setRefreshing(true);
      }
    }
  }

  private void refreshFacebookMenuItem() {
    if (getVisibleMenuItem() == FACEBOOK) {
      showMenuItem(FACEBOOK);
    }
  }

  private void refreshTwitterFeed() {
    // check network connection
    if (Utility.isConnectedToNetwork(this, true)) {
      if (!twitterIsRefreshed && TwitterAdapter.getInstance().isLoggedIn()) {
        workerFragment.requestTwitterFeed();
        // set flag
        twitterIsRefreshed = true;
        twitterPageFragment.setRefreshing(true);
      }
    }
  }

  private void refreshTwitterMenuItem() {
    if (getVisibleMenuItem() == TWITTER) {
      showMenuItem(TWITTER);
    }
  }

  private void setTabSelection(int menuIndex) {
    switch (menuIndex) {
      case FACEBOOK:
        setViewSelected(facebookIcon);
        setViewUnselected(twitterIcon);
        break;
      case TWITTER:
        setViewSelected(twitterIcon);
        setViewUnselected(facebookIcon);
        break;
      default:
    }
  }

  private void setViewSelected(View view) {
    view.setAlpha(1F);
  }

  private void setViewUnselected(View view) {
    view.setAlpha(0.54F);
  }

  private void showFragment(BaseFragment fragment, boolean addToBackStack) {
    if (isResumed && (fragment != null)) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.fragmentContainer, fragment, fragment.getTAG());
      transaction.show(fragment);

      fragment.refresh();

      clearBackStack();

      // back navigation
      if (addToBackStack) {
        transaction.addToBackStack(null);
      }

      transaction.commit();
      invalidateOptionsMenu();
    }
  }

  private void showMenuItem(int menuItem) {
    if (isResumed) {
      selectedMenuItem = menuItem;
      setTabSelection(menuItem);
      showFragment(getFragment(determineFragmentIndex(menuItem)), false);
      animateHeaderTransition(menuItem, false);
    }
  }

  private void translateTabs() {
    tabs.setTranslationY(currentTabTranslationY);
    tabsBackground.setTranslationY(currentTabTranslationY - maxTabTranslationY);
  }

  /**
   * 
   */
  private void updateBackgroundAlpha() {
    // background alpha
    float alpha = (float) (maxTabTranslationY - currentTabTranslationY) / (maxTabTranslationY);
    alpha = (alpha > maxAlpha) ? maxAlpha : alpha;
    background.setAlpha(alpha);
  }

  /**
   * 
   */
  private void updateTabsBackgroundAlpha() {
    // animate container background
    float alpha = (float) (maxTabTranslationY - currentTabTranslationY) / (maxTabTranslationY);
    tabsBackground.setAlpha(alpha);
  }

  private void updateToolbar() {
    // show toolbar if tab translation leaves sufficient room for tool bar
    float translationY = toolbar.getTranslationY();
    if (currentTabTranslationY >= toolbarHeight) {
      if (translationY <= 0) {
        // show
        animateYTranslation(toolbar, 0, 100);
      }
    } else {
      // hide
      if (translationY != -toolbarHeight) {
        if (currentTabTranslationY == 0) {
          // animation would be too slow
          if (toolbar.getAnimation() != null) {
            toolbar.getAnimation().cancel();
          }
          toolbar.setTranslationY(-toolbarHeight);
        } else {
          animateYTranslation(toolbar, -toolbarHeight, 30);
        }
      }
    }
  }
}