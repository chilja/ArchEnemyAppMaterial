package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.ArchEnemyDataAdapter;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Post;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import net.archenemy.archenemyapp.model.Tweet;
import net.archenemy.archenemyapp.model.TwitterAdapter;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Intent;
import android.content.SharedPreferences;
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
 * Main Activity of ArchEnemyApp. Manages which fragment will be displayed, and the fragment transition.
 * @author chiljagossow
 *
 */
public class MainActivity
	extends
		FacebookActivity
	implements
		TwitterAdapter.FeedCallback,
		TwitterAdapter.UserCallback,
		TwitterAdapter.TwitterLoginCallback,
		TwitterPageFragment.OnRefreshFeedListener,
		TwitterPageFragment.OnScrolledListener,
		FacebookAdapter.FeedCallback,
		FacebookAdapter.UserCallback,
		FacebookAdapter.OnFacebookLoginListener,
		FacebookPageFragment.OnRefreshFeedListener,
		FacebookPageFragment.OnScrolledListener{

	private static final String TAG = "MainActivity";

	//menu positions = main fragment index
	private static final int FACEBOOK = 0;
	private static final int TWITTER = 1;
	private static final int FACEBOOK_LOGIN = 3;
	private static final int TWITTER_LOGIN = 4;
	private static int selectedMenuItem;// initial selection

	private ArchEnemyDataAdapter dataAdapter;

	private Toolbar toolbar;

	private FrameLayout tabsBackground;
	private LinearLayout tabs;

  private int appBarHeight;
  private int tabHeight;
  private Integer maxTabTranslationY;
  private Integer currentTabTranslationY;
  private Integer facebookScrollY = 0;
  private Integer twitterScrollY = 0;

	//fragments
	private FacebookPageFragment facebookPageFragment;
	private FacebookAccountFragment facebookAccountFragment;
	private TwitterPageFragment twitterPageFragment;
	private TwitterAccountFragment twitterAccountFragment;

	//Twitter
	private TwitterAdapter twitterAdapter;
	//flag to prevent repeated automatic refresh
	private static boolean twitterIsRefreshed = false;
	private static Integer twitterCallbackCount = 0;
	private static Integer twitterCallbackTotal = 0;;
	private ImageView twitterTab;

	//Facebook
	private FacebookAdapter facebookAdapter;
	//flag to prevent repeated automatic refresh
	private static boolean facebookIsRefreshed = false;
	private static Integer facebookCallbackCount = 0;
	private static Integer facebookCallbackTotal = 0;
	private ImageView facebookTab;

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

		setContentView(R.layout.main_activity);

		twitterTab = (ImageView) findViewById(R.id.twitterTab);
		twitterTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showMenuItem(TWITTER);
			}
		});

		facebookTab = (ImageView) findViewById(R.id.facebookTab);
		facebookTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showMenuItem(FACEBOOK);
			}
		});

	    facebookAdapter = FacebookAdapter.getInstance();
	    twitterAdapter = TwitterAdapter.getInstance();
	    dataAdapter = ArchEnemyDataAdapter.getInstance();

	    //try to retrieve fragments
	    if (savedInstanceState != null) {
	    	twitterPageFragment = (TwitterPageFragment) getSupportFragmentManager().findFragmentByTag(TwitterPageFragment.TAG);
	    	facebookPageFragment = (FacebookPageFragment) getSupportFragmentManager().findFragmentByTag(FacebookPageFragment.TAG);
	    	twitterAccountFragment = (TwitterAccountFragment) getSupportFragmentManager().findFragmentByTag(TwitterAccountFragment.TAG);
		    facebookAccountFragment = (FacebookAccountFragment) getSupportFragmentManager().findFragmentByTag(FacebookAccountFragment.TAG);

		    twitterIsRefreshed = savedInstanceState.getBoolean(Constants.TWITTER_IS_REFRESHED, false);
	    	facebookIsRefreshed = savedInstanceState.getBoolean(Constants.FACEBOOK_IS_REFRESHED, false);
	    	twitterCallbackCount = savedInstanceState.getInt(Constants.TWITTER_CALLBACK_COUNT, 0);
	    	facebookCallbackCount = savedInstanceState.getInt(Constants.FACEBOOK_CALLBACK_COUNT, 0);
	    	twitterCallbackTotal = savedInstanceState.getInt(Constants.TWITTER_CALLBACK_TOTAL, 0);
	    	facebookCallbackTotal = savedInstanceState.getInt(Constants.FACEBOOK_CALLBACK_TOTAL, 0);
	    }

	    //initialize fragments
	    if (twitterPageFragment == null) {
        twitterPageFragment = new TwitterPageFragment();
      }

	    if (facebookPageFragment == null) {
        facebookPageFragment = new FacebookPageFragment();
      }

	    if (twitterAccountFragment == null) {
		    twitterAccountFragment = new TwitterAccountFragment();
		    twitterAccountFragment.showHeader(true);
		    twitterAccountFragment.showUserInfo(false);
	    }

	    if (facebookAccountFragment == null) {
		    facebookAccountFragment = new FacebookAccountFragment();
		    facebookAccountFragment.showHeader(true);
		    facebookAccountFragment.showUserInfo(false);
		}

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		initTranslationYValues();

    tabs =  (LinearLayout) findViewById(R.id.tabs);
    tabsBackground =  (FrameLayout) findViewById(R.id.tabsBackground);

  	if (savedInstanceState != null) {
    	restoreFragment(savedInstanceState);
    	return;
    }

  	getFacebookUsers();
  	getTwitterUsers();

  	// get start screen
  	SharedPreferences sharedPreferences =
	        PreferenceManager.getDefaultSharedPreferences(this);
    String startScreen = sharedPreferences.getString(Constants.PREF_KEY_START, Constants.FACEBOOK);
      selectedMenuItem = (Constants.FACEBOOK.equals(startScreen))? FACEBOOK : TWITTER;
  	showMenuItem(selectedMenuItem);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//cancel background threads
		BitmapUtility.onDestroy();
		twitterAdapter.onDestroy();
	}


	@Override
	public void onFacebookLogin() {
		//redirection from Facebook log in process
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

  //Facebook Callback
  @Override
  public void onFeedRequestCompleted(ArrayList<Post> posts, String id, FacebookRequestError error) {
    if (error != null) {
      handleError(error);
    }

  	if ((posts != null) && (id != null) && (posts.size() >0)) {
  	  Log.i(TAG, "Received facebook feed");
  		for (SocialMediaUser user: dataAdapter.getEnabledSocialMediaUsers(this)) {
  			if (id.equals(user.getFacebookUserId())) {
  				user.setPosts(posts);
  				break;
  			}
  		}
  	}

  	facebookCallbackCount += 1;
  	refreshFacebookMenuItem();

  }

	//Twitter Callback
  @Override
  public void onFeedRequestCompleted(ArrayList<Tweet> tweets, Long id) {
  	if ((tweets != null) && (id != null) && (tweets.size() >0)) {
    	for (SocialMediaUser member: dataAdapter.getEnabledSocialMediaUsers(this)) {
    		if (id.equals(member.getTwitterUserId())) {
    			member.setTweets(tweets);
    			break;
    		}
    	}
    }
  	twitterCallbackCount += 1;
  	refreshTwitterMenuItem();
    Log.i(TAG, "Received twitter feed");
  }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    //handle toolbar events
	  switch (item.getItemId()){

	    case R.id.actionRefreshTwitter:
      	//set flag to false to allow manual refresh
      	twitterIsRefreshed = false;
      	refreshTwitterFeed();
      	return true;

	    case R.id.actionRefreshFacebook:
      	//set flag to false to allow manual refresh
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
        //if event has not been handled, then pass it on
        return super.onOptionsItemSelected(item);
	  }
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		MenuInflater inflater = getMenuInflater();
		menu.clear();

		if (getVisibleFragment()==twitterPageFragment) {
	    inflater.inflate(R.menu.twitter, menu);
	    return true;
		}

		if (getVisibleFragment()==facebookPageFragment) {
	    inflater.inflate(R.menu.facebook, menu);
	    return true;
		}

		//default
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

  	if (getVisibleFragment()!=null) {
      getVisibleFragment().refresh();
    }

  	twitterScrollY = 0;
  	facebookScrollY = 0;

  	switch (getVisibleMenuItem()) {
  	case TWITTER:
  		setCurrentTabTranslationY(twitterScrollY, 0);
  		break;
  	case FACEBOOK:
  		setCurrentTabTranslationY(facebookScrollY, 0);
  		break;
  	}

  	updateTabsBackgroundAlpha();
    updateToolbar() ;
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
    //save current state
		bundle.putInt(Constants.FRAGMENT, getVisibleMenuItem());
		bundle.putInt(Constants.TWITTER_CALLBACK_COUNT, twitterCallbackCount);
		bundle.putInt(Constants.FACEBOOK_CALLBACK_COUNT, facebookCallbackCount);
		bundle.putInt(Constants.TWITTER_CALLBACK_TOTAL, twitterCallbackTotal);
		bundle.putInt(Constants.FACEBOOK_CALLBACK_TOTAL, facebookCallbackTotal);
		bundle.putBoolean(Constants.TWITTER_IS_REFRESHED, twitterIsRefreshed);
		bundle.putBoolean(Constants.FACEBOOK_IS_REFRESHED, facebookIsRefreshed);
	}

	@Override
  public void onTwitterLogin() {
    //redirection from twitter
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
  public void onUserRequestCompleted(GraphUser user, FacebookRequestError error) {
    if (error != null) {
      handleError(error);
    }
  	String userId = user.getId();
  	for (SocialMediaUser member : dataAdapter.getEnabledSocialMediaUsers(this)) {
  		if (member.getFacebookUserId().equals(userId)) {
  			member.setFacebookUser(user);
  			facebookAdapter.makeFeedRequest(this, member.getFacebookUserId());
  			break;
  		}
  	}
  }

  @Override
  public void onUserRequestCompleted(User user) {
  	Long userId = user.getId();
  	for (SocialMediaUser member : dataAdapter.getEnabledSocialMediaUsers(this)) {
  		if (member.getTwitterUserId().equals(userId)) {
  			member.setTwitterUser(user);
  			twitterAdapter.makeFeedRequest(member.getTwitterUserId(), this);
  			break;
  		}
  	}
  }

  private void animateHeaderTransition(int menuIndex) {
  	// set new tab translation
  	switch (menuIndex) {
  	case FACEBOOK:
  		setCurrentTabTranslationY(facebookScrollY, -1);
  		break;
  	case TWITTER:
  		setCurrentTabTranslationY(twitterScrollY, -1);
  	}

    //background alpha
  	float alpha = (float)
  			(maxTabTranslationY - currentTabTranslationY)/(maxTabTranslationY) ;

  	// tool bar translation
  	float translationY = toolbar.getTranslationY();

  	// animations
    if (currentTabTranslationY > appBarHeight) {
    	if (translationY < 0) {
    	//show
    		animateYTranslation(toolbar, 0, 300);
    	}
    } else {
    	//hide
    	if (translationY != -appBarHeight) {
    		animateYTranslation(toolbar, -appBarHeight, 300);
    	}
    }

  	animateYTranslation(tabs, currentTabTranslationY, 300);
  	animateYTranslation(tabsBackground,currentTabTranslationY - maxTabTranslationY, 300);
  	tabsBackground.animate().alpha(alpha).setDuration(300).start();
  }

  private void animateTabsTranslation() {
	  animateYTranslation(tabs, currentTabTranslationY, 300);
	  animateYTranslation(tabsBackground, (currentTabTranslationY - maxTabTranslationY), 300);
  }

	private void animateYTranslation(final View view, final int y, int duration) {

		if (view.getAnimation() == null) {

  		AnimatorListener listener = new AnimatorListener() {
  			@Override
  			public void onAnimationCancel(Animator animation) {}

  			@Override
  			public void onAnimationEnd(Animator animation) {
  			  //make sure the animation ends in the correct end position
  			  if (view == tabs) {
  			    view.setTranslationY(currentTabTranslationY);
  			    return;
  			  }
  			  if (view == tabs) {
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

    	view.animate().translationY(y)
  		.setInterpolator(new AccelerateDecelerateInterpolator())
  		.setDuration(duration)
  		.setListener(listener)
  		.start();
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
		//check network connection
    if (Utility.isConnectedToNetwork(this, true)) {
			if (!facebookIsRefreshed && facebookAdapter.isLoggedIn()) {
				for (SocialMediaUser member: dataAdapter.getEnabledSocialMediaUsers(this)) {
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
	 	//check network connection
    if (Utility.isConnectedToNetwork(this, true)) {
		if (!twitterIsRefreshed && twitterAdapter.isLoggedIn()) {
			for (SocialMediaUser member: dataAdapter.getEnabledSocialMediaUsers(this)) {
		    	if (member.getTwitterUser() == null) {
		    		twitterAdapter.makeUserRequest(member.getTwitterUserId(), this);
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
		if (facebookPageFragment.isVisible()|| facebookAccountFragment.isVisible()) {
      return FACEBOOK;
    }
		return -1;
	}

	private void initTranslationYValues() {
    tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
    appBarHeight = getResources().getDimensionPixelSize(R.dimen.app_bar_height);
    maxTabTranslationY = getResources().getDimensionPixelSize(R.dimen.max_tab_translation_y);
    currentTabTranslationY = maxTabTranslationY;
  }

	private void onPageScrolled(Integer scrollY, int dy) {
    int previousTabTranslation = currentTabTranslationY;
    setCurrentTabTranslationY(scrollY, dy);
    updateTabsBackgroundAlpha();
    updateToolbar();
    if ( ((previousTabTranslation == 0) && (currentTabTranslationY == -tabHeight) )
        || ( (previousTabTranslation == -tabHeight) && (currentTabTranslationY == 0))) {
      // show or hide tabs
      animateTabsTranslation();
    } else if (previousTabTranslation != currentTabTranslationY ) {
      //translation has changed
      translateTabs();
    }
  }

	private void refreshFacebookFeed() {
    //check network connection
    if (Utility.isConnectedToNetwork(this, true)) {
      if (!facebookIsRefreshed && facebookAdapter.isLoggedIn()) {

  			facebookCallbackCount = 0;
  			for (SocialMediaUser member: dataAdapter.getEnabledSocialMediaUsers(this)) {
  				facebookAdapter.makeFeedRequest(this, member.getFacebookUserId());
  				facebookCallbackTotal += 1;
  			}

  			//set flag
  			facebookIsRefreshed = true;
	      facebookPageFragment.setRefreshing(true);
		  }
    }
  }

	private void refreshFacebookMenuItem() {
		if (getVisibleMenuItem() == FACEBOOK){
			showMenuItem(FACEBOOK);
		}

		if (facebookCallbackCount == facebookCallbackTotal) {
			if (getVisibleMenuItem() == FACEBOOK){
				facebookScrollY = 0;
				animateHeaderTransition(FACEBOOK);
			}
			facebookPageFragment.refresh();
			facebookCallbackCount = facebookCallbackTotal = 0;
		}
	}

	private void refreshTwitterFeed() {
  	//check network connection
    if (Utility.isConnectedToNetwork(this, true)) {
  		if (!twitterIsRefreshed && twitterAdapter.isLoggedIn()) {

  			twitterCallbackCount = 0;
  			for (SocialMediaUser member: dataAdapter.getEnabledSocialMediaUsers(this)) {
  				twitterAdapter.makeFeedRequest(member.getTwitterUserId(), this);
  				twitterCallbackCount += 1;
  			}

  			//set flag
  			twitterIsRefreshed = true;
  			twitterPageFragment.setRefreshing(true);
  		}
    }
  }

	private void refreshTwitterMenuItem() {
		if (getVisibleMenuItem() == TWITTER){
			showMenuItem(TWITTER);
		}

		if (twitterCallbackCount == twitterCallbackTotal) {
			if (getVisibleMenuItem() == TWITTER){
				twitterScrollY = 0;
				animateHeaderTransition(TWITTER);
			}
			twitterPageFragment.refresh();
			twitterCallbackCount = twitterCallbackTotal = 0;
		}
	}

	private void restoreFragment(Bundle savedInstanceState){
		int fragmentIndex = savedInstanceState.getInt(Constants.FRAGMENT);
		showMenuItem(fragmentIndex);
	}

	private Integer setCurrentTabTranslationY(int scrollY, int dy){
	  synchronized (currentTabTranslationY) {
  	  int minTranslation = -tabHeight;
      if (dy < 0) {
        // scrolling up -> show tabs
        minTranslation = 0;
      }
  	  if ( (maxTabTranslationY + scrollY) <= minTranslation ) {
        //hide
  	    return currentTabTranslationY = minTranslation;
      }
  	  if ( (maxTabTranslationY + scrollY) < 0 ) {
        //show
        return currentTabTranslationY = 0;
      }
  	  //translate
  		return currentTabTranslationY = maxTabTranslationY + scrollY;
  	  }
	}

	private void setTabSelected(ImageView tab) {
    tab.setAlpha(1f);
	}

	private void setTabSelection(int menuIndex) {
		switch (menuIndex) {
		case FACEBOOK:
			setTabSelected(facebookTab);
			setTabUnselected(twitterTab);
			break;
		case TWITTER:
			setTabSelected(twitterTab);
			setTabUnselected(facebookTab);
			break;
		default:
		}
	}

	private void setTabUnselected(ImageView tab) {
		tab.setAlpha(0.54f);
	}

	private void showFragment (BaseFragment fragment, boolean addToBackStack) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, fragment, fragment.getTAG());
		transaction.show(fragment);

		fragment.refresh();

		clearBackStack();

    //back navigation
  	if (addToBackStack) {
  		transaction.addToBackStack(null);
  	}

  	transaction.commit();
  	invalidateOptionsMenu();
	}

	private void showMenuItem(int menuIndex) {
		setTabSelection(menuIndex);
		showFragment(getFragment(determineFragmentIndex(menuIndex)), false);
		animateHeaderTransition(menuIndex);
	}

	private void translateTabs() {
		tabs.setTranslationY(currentTabTranslationY);
		tabsBackground.setTranslationY(currentTabTranslationY - maxTabTranslationY);
	}

	private void updateTabsBackgroundAlpha() {
  	// animate container background
    float alpha = (float)
    		(maxTabTranslationY - currentTabTranslationY)/(maxTabTranslationY);
    tabsBackground.setAlpha(alpha);
  }

	private void updateToolbar() {
  	float translationY = toolbar.getTranslationY();

    if (currentTabTranslationY > appBarHeight) {
    	if (translationY < 0) {
    	//show
    		animateYTranslation(toolbar, 0, 100);
    	}
    } else {
    	//hide
    	if (translationY != -appBarHeight) {
    		if (currentTabTranslationY == 0) {
    			//animation would be too slow
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