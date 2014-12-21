package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.DataAdapter;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Post;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import net.archenemy.archenemyapp.model.Tweet;
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

import com.facebook.FacebookRequestError;
import com.facebook.model.GraphUser;

import twitter4j.User;

import java.util.ArrayList;

/**
 * Main Activity of ArchEnemyApp. Manages which fragment will be displayed and
 * coordinates calls to the provider adapters.
 * 
 * @author chiljagossow
 * 
 */
public class MainActivity extends FacebookActivity implements TwitterAdapter.FeedCallback,
    TwitterAdapter.UserCallback, TwitterAdapter.TwitterLoginCallback,
    TwitterPageFragment.OnRefreshFeedListener, TwitterPageFragment.OnScrolledListener,
    FacebookAdapter.FeedCallback, FacebookAdapter.UserCallback,
    FacebookAdapter.OnFacebookLoginListener, FacebookPageFragment.OnRefreshFeedListener,
    FacebookPageFragment.OnScrolledListener {

  private static final String TAG = "MainActivity";

  // menu positions = main fragment index
  private static final int FACEBOOK = 0;
  private static final int TWITTER = 1;
  private static final int FACEBOOK_LOGIN = 3;
  private static final int TWITTER_LOGIN = 4;
  private static int selectedMenuItem;// initial selection

  private boolean isResumed = false;

  private DataAdapter dataAdapter;

  private Toolbar toolbar;

  private FrameLayout tabsBackground;
  private LinearLayout tabs;
  private ImageView headerImage;

  private int appBarHeight;
  private int tabHeight;
  private Integer maxTabTranslationY;
  private Integer currentTabTranslationY;
  private Integer facebookScrollY = 0;
  private Integer twitterScrollY = 0;

  // fragments
  private FacebookPageFragment facebookPageFragment;
  private FacebookAccountFragment facebookAccountFragment;
  private TwitterPageFragment twitterPageFragment;
  private TwitterAccountFragment twitterAccountFragment;

  // Twitter
  private TwitterAdapter twitterAdapter;
  // flag to prevent repeated automatic refresh
  private static boolean twitterIsRefreshed = false;
  private static Integer twitterCallbackCount = 0;
  private static Integer twitterCallbackTotal = 0;;
  private FrameLayout twitterTab;
  private ImageView twitterIcon;

  // Facebook
  private FacebookAdapter facebookAdapter;
  // flag to prevent repeated automatic refresh
  private static boolean facebookIsRefreshed = false;
  private static Integer facebookCallbackCount = 0;
  private static Integer facebookCallbackTotal = 0;
  private FrameLayout facebookTab;
  private ImageView facebookIcon;

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (twitterAccountFragment != null) {
      twitterAccountFragment.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Checks the orientation of the screen
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      maxTabTranslationY = getResources().getDimensionPixelSize(R.dimen.max_tab_translation_y_land);
    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
      maxTabTranslationY = getResources().getDimensionPixelSize(R.dimen.max_tab_translation_y);
    }
    switch (selectedMenuItem) {
      case TWITTER:
        setCurrentTabTranslationY(twitterScrollY, false);
        break;
      case FACEBOOK:
        setCurrentTabTranslationY(facebookScrollY, false);
        break;
    }

    updateTabsBackgroundAlpha();
    updateToolbar();
    translateTabs();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main_activity);

    facebookIcon = (ImageView) findViewById(R.id.facebookIcon);
    facebookTab = (FrameLayout) findViewById(R.id.facebookTab);
    facebookTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        showMenuItem(FACEBOOK);
      }
    });

    twitterIcon = (ImageView) findViewById(R.id.twitterIcon);
    twitterTab = (FrameLayout) findViewById(R.id.twitterTab);
    twitterTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        showMenuItem(TWITTER);
      }
    });

    facebookAdapter = FacebookAdapter.getInstance();
    twitterAdapter = TwitterAdapter.getInstance();
    dataAdapter = DataAdapter.getInstance();

    if (savedInstanceState != null) {
      // try to retrieve fragments
      twitterPageFragment = (TwitterPageFragment) getSupportFragmentManager().findFragmentByTag(
          TwitterPageFragment.TAG);
      facebookPageFragment = (FacebookPageFragment) getSupportFragmentManager().findFragmentByTag(
          FacebookPageFragment.TAG);
      twitterAccountFragment = (TwitterAccountFragment) getSupportFragmentManager()
          .findFragmentByTag(TwitterAccountFragment.TAG);
      facebookAccountFragment = (FacebookAccountFragment) getSupportFragmentManager()
          .findFragmentByTag(FacebookAccountFragment.TAG);

      twitterIsRefreshed = savedInstanceState.getBoolean(Constants.TWITTER_IS_REFRESHED, false);
      facebookIsRefreshed = savedInstanceState.getBoolean(Constants.FACEBOOK_IS_REFRESHED, false);
      twitterCallbackCount = savedInstanceState.getInt(Constants.TWITTER_CALLBACK_COUNT, 0);
      facebookCallbackCount = savedInstanceState.getInt(Constants.FACEBOOK_CALLBACK_COUNT, 0);
      twitterCallbackTotal = savedInstanceState.getInt(Constants.TWITTER_CALLBACK_TOTAL, 0);
      facebookCallbackTotal = savedInstanceState.getInt(Constants.FACEBOOK_CALLBACK_TOTAL, 0);
    }

    // initialize fragments
    if (twitterPageFragment == null) {
      twitterPageFragment = new TwitterPageFragment();
    }

    if (facebookPageFragment == null) {
      facebookPageFragment = new FacebookPageFragment();
    }

    if (twitterAccountFragment == null) {
      twitterAccountFragment = new TwitterAccountFragment();
      twitterAccountFragment.showHeader(true);
      twitterAccountFragment.showUser(false);
    }

    if (facebookAccountFragment == null) {
      facebookAccountFragment = new FacebookAccountFragment();
      facebookAccountFragment.showHeader(true);
      facebookAccountFragment.showUser(false);
    }

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    // no title
    toolbar.setTitle("");
    setSupportActionBar(toolbar);

    tabs = (LinearLayout) findViewById(R.id.tabs);
    tabsBackground = (FrameLayout) findViewById(R.id.tabsBackground);

    headerImage = (ImageView) findViewById(R.id.headerImage);
    int width = getResources().getDisplayMetrics().widthPixels;
    int height = (width * 3) / 4;
    BitmapUtility.loadBitmap(this, R.drawable.band, headerImage, width, height);

    initTranslationYValues();

    getFacebookUsers();
    getTwitterUsers();

    if (savedInstanceState != null) {
      selectedMenuItem = savedInstanceState.getInt(Constants.MENU_ITEM);
    } else {
      // get start screen from preferences
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      String startScreen = sharedPreferences
          .getString(Constants.PREF_KEY_START, Constants.FACEBOOK);
      selectedMenuItem = (Constants.FACEBOOK.equals(startScreen)) ? FACEBOOK : TWITTER;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    // cancel background threads
    BitmapUtility.onDestroy();
    twitterAdapter.onDestroy();
  }

  @Override
  public void onFacebookLogin() {
    // callback from Facebook log in process
    Log.i(TAG, "Facebook session opened");
    if (facebookAdapter.isLoggedIn()) {
      facebookIsRefreshed = false;
      refreshFacebookMenuItem();
      refreshFacebookFeed();
    }
  }

  @Override
  public void onFacebookPageScrolled(int scrollY, int dy) {
    synchronized (facebookScrollY) {
      facebookScrollY = scrollY;
      onPageScrolled(facebookScrollY, dy);
    }
  }

  // Facebook feed callback
  @Override
  public void onFeedRequestCompleted(ArrayList<Post> posts, String id, FacebookRequestError error) {
    if (error != null) {
      handleError(error);
    }
    synchronized (facebookCallbackCount) {
      if ((posts != null) && (id != null) && (posts.size() > 0)) {

        for (SocialMediaUser user : dataAdapter.getEnabledSocialMediaUsers(this)) {
          if (id.equals(user.getFacebookUserId())) {
            user.setPosts(posts);
            break;
          }
        }
      }

      facebookCallbackCount += 1;
      if (facebookCallbackCount == facebookCallbackTotal) {
        refreshFacebookMenuItem();
      }

      Log.i(TAG, "Received facebook feed for " + id);
    }
  }

  // Twitter feed callback
  @Override
  public void onFeedRequestCompleted(ArrayList<Tweet> tweets, Long id) {
    synchronized (twitterCallbackCount) {
      if ((tweets != null) && (id != null) && (tweets.size() > 0)) {
        for (SocialMediaUser user : dataAdapter.getEnabledSocialMediaUsers(this)) {
          if (id.equals(user.getTwitterUserId())) {
            user.setTweets(tweets);
            break;
          }
        }
      }
      twitterCallbackCount += 1;
      if (twitterCallbackCount == twitterCallbackTotal) {
        refreshTwitterMenuItem();
      }
      Log.i(TAG, "Received twitter feed for " + id.toString());
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
  public void onResume() {
    super.onResume();
    isResumed = true;

    twitterScrollY = twitterPageFragment.getScrollY();
    facebookScrollY = facebookPageFragment.getScrollY();

    switch (selectedMenuItem) {
      case TWITTER:
        setCurrentTabTranslationY(twitterScrollY, false);
        break;
      case FACEBOOK:
        setCurrentTabTranslationY(facebookScrollY, false);
        break;
    }

    showMenuItem(selectedMenuItem);
    updateTabsBackgroundAlpha();
    updateToolbar();
    translateTabs();
  }

  @Override
  public void onRrefeshTwitterFeed() {
    twitterIsRefreshed = false;
    refreshTwitterFeed();
  }

  @Override
  public void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    // save current state
    bundle.putInt(Constants.MENU_ITEM, getVisibleMenuItem());
    bundle.putInt(Constants.TWITTER_CALLBACK_COUNT, twitterCallbackCount);
    bundle.putInt(Constants.FACEBOOK_CALLBACK_COUNT, facebookCallbackCount);
    bundle.putInt(Constants.TWITTER_CALLBACK_TOTAL, twitterCallbackTotal);
    bundle.putInt(Constants.FACEBOOK_CALLBACK_TOTAL, facebookCallbackTotal);
    bundle.putBoolean(Constants.TWITTER_IS_REFRESHED, twitterIsRefreshed);
    bundle.putBoolean(Constants.FACEBOOK_IS_REFRESHED, facebookIsRefreshed);
  }

  @Override
  public void onTwitterLogin() {
    // callback from Twitter login process
    Log.i(TAG, "Twitter session opened");
    if (twitterAdapter.isLoggedIn()) {
      twitterIsRefreshed = false;
      refreshTwitterMenuItem();
      refreshTwitterFeed();
    }
  }

  @Override
  public void onTwitterPageScrolled(int scrollY, int dy) {
    synchronized (twitterScrollY) {
      twitterScrollY = scrollY;
      onPageScrolled(twitterScrollY, dy);
    }
  }

  @Override
  public void onUserRequestCompleted(GraphUser graphUser, FacebookRequestError error) {
    if (error != null) {
      handleError(error);
    }
    String userId = graphUser.getId();
    for (SocialMediaUser user : dataAdapter.getEnabledSocialMediaUsers(this)) {
      if (user.getFacebookUserId().equals(userId)) {
        user.setFacebookUser(graphUser);
        facebookAdapter.makeFeedRequest(this, user.getFacebookUserId());
        facebookCallbackTotal++;
        break;
      }
    }
  }

  @Override
  public void onUserRequestCompleted(User twitterUser) {
    Long userId = twitterUser.getId();
    for (SocialMediaUser user : dataAdapter.getEnabledSocialMediaUsers(this)) {
      if (user.getTwitterUserId().equals(userId)) {
        user.setTwitterUser(twitterUser);
        twitterAdapter.makeFeedRequest(userId, this, this);
        twitterCallbackTotal++;
        break;
      }
    }
  }

  private void animateHeaderTransition(int menuIndex, boolean showTabs) {
    if (isResumed) {
      // set new tab translation
      switch (menuIndex) {
        case FACEBOOK:
          facebookScrollY = facebookPageFragment.getScrollY();
          setCurrentTabTranslationY(facebookScrollY, showTabs);
          break;
        case TWITTER:
          twitterScrollY = twitterPageFragment.getScrollY();
          setCurrentTabTranslationY(twitterScrollY, showTabs);
      }

      // background alpha
      float alpha = (float) (maxTabTranslationY - currentTabTranslationY) / (maxTabTranslationY);

      // tool bar translation
      float translationY = toolbar.getTranslationY();

      // animations
      if (currentTabTranslationY > appBarHeight) {
        if (translationY < 0) {
          // show
          animateYTranslation(toolbar, 0, 300);
        }
      } else {
        // hide
        if (translationY != -appBarHeight) {
          animateYTranslation(toolbar, -appBarHeight, 300);
        }
      }

      animateYTranslation(tabs, currentTabTranslationY, 300);
      animateYTranslation(tabsBackground, currentTabTranslationY - maxTabTranslationY, 300);
      tabsBackground.animate().alpha(alpha).setDuration(300).start();
    }
  }

  private void animateTabsTranslation() {
    if (isResumed) {
      animateYTranslation(tabs, currentTabTranslationY, 300);
      animateYTranslation(tabsBackground, (currentTabTranslationY - maxTabTranslationY), 300);
    }
  }

  private void animateYTranslation(final View view, final int y, int duration) {

    if (view.getAnimation() == null) {

      AnimatorListener listener = new AnimatorListener() {
        @Override
        public void onAnimationCancel(Animator animation) {}

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
        if (facebookAdapter.hasValidToken()) {
          index = FACEBOOK;
        } else {
          index = FACEBOOK_LOGIN;
        }
        break;

      case TWITTER:
        if (twitterAdapter.isLoggedIn()) {
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
      if (!facebookIsRefreshed && facebookAdapter.isLoggedIn()) {
        for (SocialMediaUser member : dataAdapter.getEnabledSocialMediaUsers(this)) {
          if (member.getFacebookUser() == null) {
            facebookAdapter.makeUserRequest(this, member.getFacebookUserId());
          }
        }
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
      if (!twitterIsRefreshed && twitterAdapter.isLoggedIn()) {
        for (SocialMediaUser member : dataAdapter.getEnabledSocialMediaUsers(this)) {
          if (member.getTwitterUser() == null) {
            twitterAdapter.makeUserRequest(member.getTwitterUserId(), this, this);
          }
        }
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
    appBarHeight = getResources().getDimensionPixelSize(R.dimen.app_bar_height);
    switch (getResources().getConfiguration().orientation) {
      case Configuration.ORIENTATION_LANDSCAPE:
        maxTabTranslationY = getResources().getDimensionPixelSize(
            R.dimen.max_tab_translation_y_land);
        break;
      default:
        maxTabTranslationY = getResources().getDimensionPixelSize(R.dimen.max_tab_translation_y);
    }
    currentTabTranslationY = maxTabTranslationY;
  }

  private void onPageScrolled(Integer scrollY, int dy) {
    boolean showTabs = false;
    if (dy < 0) {
      showTabs = true;
    }
    int previousTabTranslation = currentTabTranslationY;
    setCurrentTabTranslationY(scrollY, showTabs);
    updateTabsBackgroundAlpha();
    updateToolbar();
    if (((previousTabTranslation == 0) && (currentTabTranslationY == -tabHeight))
        || ((previousTabTranslation == -tabHeight) && (currentTabTranslationY == 0))) {
      // show or hide tabs
      animateTabsTranslation();
    } else if (previousTabTranslation != currentTabTranslationY) {
      // translation has changed
      translateTabs();
    }
  }

  private void refreshFacebookFeed() {
    // check network connection
    if (Utility.isConnectedToNetwork(this, true)) {
      if (!facebookIsRefreshed && facebookAdapter.isLoggedIn()) {

        facebookCallbackCount = 0;
        facebookCallbackTotal = 0;
        for (SocialMediaUser member : dataAdapter.getEnabledSocialMediaUsers(this)) {
          facebookAdapter.makeFeedRequest(this, member.getFacebookUserId());
          facebookCallbackTotal += 1;
        }

        // set flag
        facebookIsRefreshed = true;
        facebookPageFragment.setRefreshing(true);
      }
    }
  }

  private void refreshFacebookMenuItem() {
    if (facebookCallbackCount == facebookCallbackTotal) {
      if (getVisibleMenuItem() == FACEBOOK) {
        showMenuItem(FACEBOOK);
      }
      facebookPageFragment.refresh();
      facebookCallbackCount = facebookCallbackTotal = 0;
    }
  }

  private void refreshTwitterFeed() {
    // check network connection
    if (Utility.isConnectedToNetwork(this, true)) {
      if (!twitterIsRefreshed && twitterAdapter.isLoggedIn()) {

        twitterCallbackTotal = 0;
        twitterCallbackCount = 0;
        for (SocialMediaUser member : dataAdapter.getEnabledSocialMediaUsers(this)) {
          twitterAdapter.makeFeedRequest(member.getTwitterUserId(), this, this);
          twitterCallbackTotal += 1;
        }

        // set flag
        twitterIsRefreshed = true;
        twitterPageFragment.setRefreshing(true);
      }
    }
  }

  private void refreshTwitterMenuItem() {
    if (getVisibleMenuItem() == TWITTER) {
      showMenuItem(TWITTER);
    } else {
      twitterPageFragment.refresh();
    }
  }

  private Integer setCurrentTabTranslationY(int scrollY, boolean showTabs) {
    synchronized (currentTabTranslationY) {
      int minTranslation = -tabHeight;
      if (showTabs) {
        // scrolling up -> show tabs
        minTranslation = 0;
      }
      if ((maxTabTranslationY + scrollY) <= minTranslation) {
        // hide
        return currentTabTranslationY = minTranslation;
      }
      if ((maxTabTranslationY + scrollY) < 0) {
        // show
        return currentTabTranslationY = 0;
      }
      // translate
      return currentTabTranslationY = maxTabTranslationY + scrollY;
    }
  }

  private void setViewSelected(View view) {
    view.setAlpha(1F);
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

  private void setViewUnselected(View view) {
    view.setAlpha(0.54F);
  }

  private void showFragment(BaseFragment fragment, boolean addToBackStack) {
    if (isResumed) {
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

  private void updateTabsBackgroundAlpha() {
    // animate container background
    float alpha = (float) (maxTabTranslationY - currentTabTranslationY) / (maxTabTranslationY);
    tabsBackground.setAlpha(alpha);
  }

  private void updateToolbar() {
    float translationY = toolbar.getTranslationY();

    if (currentTabTranslationY > appBarHeight) {
      if (translationY < 0) {
        // show
        animateYTranslation(toolbar, 0, 100);
      }
    } else {
      // hide
      if (translationY != -appBarHeight) {
        if (currentTabTranslationY == 0) {
          // animation would be too slow
          if (toolbar.getAnimation() != null) {
            toolbar.getAnimation().cancel();
          }
          toolbar.setTranslationY(-appBarHeight);
        } else {
          animateYTranslation(toolbar, -appBarHeight, 30);
        }
      }
    }
  }
}