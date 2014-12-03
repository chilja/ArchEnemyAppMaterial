package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.ArchEnemyDataAdapter;
import net.archenemy.archenemyapp.model.BitmapUtility;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import net.archenemy.archenemyapp.model.TwitterAdapter;
import net.archenemy.archenemyapp.model.Utility;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.model.GraphUser;

import twitter4j.User;

import java.util.ArrayList;

public class MainActivity 
	extends 
		FacebookActivity 
	implements 
		TwitterAdapter.FeedCallback,		 
		TwitterAdapter.UserCallback, 
		TwitterAdapter.OnTwitterLoginListener,
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
	private static int mSelectedMenuItem;// initial selection
	
	private ArchEnemyDataAdapter mDataAdapter;
	
	private Toolbar mToolbar;

private FrameLayout mTabsBackground;
	private LinearLayout mTabs;
	
    private int mAppBarHeight; 
    private int mTabHeight;
    private Integer mMaxTabTranslationY;
    private Integer mCurrentTabTranslationY;
    private Integer mFacebookScrollY = 0;
    private Integer mTwitterScrollY = 0;
	
	//fragments
	private FacebookPageFragment mFacebookPageFragment;
	private FacebookAccountFragment mFacebookAccountFragment;	
	private TwitterPageFragment mTwitterPageFragment;
	private TwitterAccountFragment mTwitterAccountFragment;
	
	//Twitter
	private TwitterAdapter mTwitterAdapter;
	//flag to prevent repeated automatic refresh
	private static boolean mTwitterIsRefreshed = false;
	private static Integer mTwitterCallbackCount = 0;
	private static Integer mTwitterCallbackTotal = 0;;
	private ImageView mTwitterTab;
	
	
	//Facebook
	private FacebookAdapter mFacebookAdapter;	
	//flag to prevent repeated automatic refresh
	private static boolean mFacebookIsRefreshed = false;
	private static Integer mFacebookCallbackCount = 0;
	private static Integer mFacebookCallbackTotal = 0;
	private ImageView mFacebookTab;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (mTwitterAccountFragment != null) {
	    	mTwitterAccountFragment.onActivityResult(requestCode, resultCode, data);
	    }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);

		mTwitterTab = (ImageView) findViewById(R.id.twitterTab);
		mTwitterTab.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				showMenuItem(TWITTER);					
			}
		});
		
		mFacebookTab = (ImageView) findViewById(R.id.facebookTab);
		mFacebookTab.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				showMenuItem(FACEBOOK);
			}
		});
		                     
	    mFacebookAdapter = FacebookAdapter.getInstance();
	    mTwitterAdapter = TwitterAdapter.getInstance();
	    mDataAdapter = ArchEnemyDataAdapter.getInstance();
	    	    
	    //try to retrieve fragments
	    if (savedInstanceState != null) {
	    	mTwitterPageFragment = (TwitterPageFragment) getSupportFragmentManager().findFragmentByTag(TwitterPageFragment.TAG);
	    	mFacebookPageFragment = (FacebookPageFragment) getSupportFragmentManager().findFragmentByTag(FacebookPageFragment.TAG);
	    	mTwitterAccountFragment = (TwitterAccountFragment) getSupportFragmentManager().findFragmentByTag(TwitterAccountFragment.TAG);
		    mFacebookAccountFragment = (FacebookAccountFragment) getSupportFragmentManager().findFragmentByTag(FacebookAccountFragment.TAG);
	    	
		    mTwitterIsRefreshed = savedInstanceState.getBoolean(Constants.TWITTER_IS_REFRESHED, false);
	    	mFacebookIsRefreshed = savedInstanceState.getBoolean(Constants.FACEBOOK_IS_REFRESHED, false);
	    	mTwitterCallbackCount = savedInstanceState.getInt(Constants.TWITTER_CALLBACK_COUNT, 0);
	    	mFacebookCallbackCount = savedInstanceState.getInt(Constants.FACEBOOK_CALLBACK_COUNT, 0);
	    	mTwitterCallbackTotal = savedInstanceState.getInt(Constants.TWITTER_CALLBACK_TOTAL, 0);
	    	mFacebookCallbackTotal = savedInstanceState.getInt(Constants.FACEBOOK_CALLBACK_TOTAL, 0);
	    } 
	    
	    //create fragments
	    if (mTwitterPageFragment == null)
	    	mTwitterPageFragment = new TwitterPageFragment();
	    
	    if (mFacebookPageFragment == null)
	    	mFacebookPageFragment = new FacebookPageFragment();
	    
	    if (mTwitterAccountFragment == null) {
		    mTwitterAccountFragment = new TwitterAccountFragment();
		    mTwitterAccountFragment.showHeader(true);
		    mTwitterAccountFragment.showUserInfo(false);
	    }
		if (mFacebookAccountFragment == null) {
		    mFacebookAccountFragment = new FacebookAccountFragment();
		    mFacebookAccountFragment.showHeader(true);
		    mFacebookAccountFragment.showUserInfo(false);
		}
		
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		initTranslationYValues();

	    mTabs =  (LinearLayout) findViewById(R.id.tabs);
	    mTabsBackground =  (FrameLayout) findViewById(R.id.tabsBackground);
		
    	if (savedInstanceState != null) {	
	    	restoreFragment(savedInstanceState);
	    	return;
	    } 
    	
    	getFacebookUsers();
    	getTwitterUsers();
    	
    	// get start sceen
    	SharedPreferences sharedPreferences = 
		        PreferenceManager.getDefaultSharedPreferences(this);
        String start = sharedPreferences.getString(Constants.PREF_KEY_START, Constants.FACEBOOK);
        mSelectedMenuItem = (Constants.FACEBOOK.equals(start))? FACEBOOK : TWITTER;
    	showMenuItem(mSelectedMenuItem);	 
    	
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//cancel background threads
		BitmapUtility.onDestroy();
		mTwitterAdapter.onDestroy();
	}

	
	@Override
  	public void onFacebookLogin() {
  		//redirection from facebook 		
		Log.i(TAG, "Facebook session opened");
		if (mFacebookAdapter.isLoggedIn()) {
			mFacebookIsRefreshed = false;
			refreshFacebookMenuItem();
			refreshFacebookFeed();
		}
  	}
	
	//Twitter Callback
    @Override
	public void onFeedRequestCompleted(ArrayList<FeedElement> elements, Long id) { 
    	if (elements != null && id != null && elements.size() >0) {
			for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers(this)) {
				if (id.equals(member.getTwitterUserId())) {
					member.setTweets(elements);
					break;
				}					
			}
		}
    	mTwitterCallbackCount += 1;
		refreshTwitterMenuItem();	
	    Log.i(TAG, "Received twitter feed");	
	}
	
    //Facebook Callback
	@Override
	public void onFeedRequestCompleted(ArrayList<Post> elements, String id) {
		if (elements != null && id != null && elements.size() >0) {
			for (SocialMediaUser user: mDataAdapter.getEnabledSocialMediaUsers(this)) {
				if (id.equals(user.getFacebookUserId())) {
					user.setPosts(elements);
					break;
				}					
			}
		}
		mFacebookCallbackCount += 1;
		refreshFacebookMenuItem();
		Log.i(TAG, "Received facebook feed");
	}    
    
  	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    //handle action bar events
	    if (item.getItemId() == R.id.actionRefreshTwitter) {
	    	//set flag to false to allow manual refresh
	    	mTwitterIsRefreshed = false;
	    	refreshTwitterFeed();
	    	return true;
	    }
	    if (item.getItemId() == R.id.actionRefreshFacebook) {
	    	//set flag to false to allow manual refresh
	    	mFacebookIsRefreshed = false;
	    	refreshFacebookFeed();
	    	return true;
	    }
	    if (item.getItemId() == R.id.actionSettings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
	    }
	    if (item.getItemId() == R.id.actionAccounts) {
			Intent intent = new Intent(this, AccountActivity.class);
			startActivity(intent);
			return true;
	    }
	    //if event has not been handled, then pass it on
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		MenuInflater inflater = getMenuInflater();
		menu.clear();
		if (getVisibleFragment()==mTwitterPageFragment) {
		    inflater.inflate(R.menu.twitter, menu);
		    return true;
		}
		if (getVisibleFragment()==mFacebookPageFragment) {
		    inflater.inflate(R.menu.facebook, menu);
		    return true;
		}
		//default
		inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public void onRefeshFacebookFeed() {
		mFacebookIsRefreshed = false;
		refreshFacebookFeed();
	}
	
	@Override
	public void onRefeshTwitterFeed() {
		mTwitterIsRefreshed = false;
		refreshTwitterFeed();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getVisibleFragment()!=null)
			getVisibleFragment().refresh();
		mTwitterScrollY = 0;
		mFacebookScrollY = 0;
		switch (getVisibleMenuItem()) {
		case TWITTER:
			setCurrentTabTranslationY(mTwitterScrollY);
			break;
		case FACEBOOK:
			setCurrentTabTranslationY(mFacebookScrollY);
			break;
		}
		
		updateBackground();
    	updateToolbar() ;
	    updateTabs();
	}
	
	
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    //save current state
		bundle.putInt(Constants.FRAGMENT, getVisibleMenuItem());
		bundle.putInt(Constants.TWITTER_CALLBACK_COUNT, mTwitterCallbackCount);
		bundle.putInt(Constants.FACEBOOK_CALLBACK_COUNT, mFacebookCallbackCount);
		bundle.putInt(Constants.TWITTER_CALLBACK_TOTAL, mTwitterCallbackTotal);
		bundle.putInt(Constants.FACEBOOK_CALLBACK_TOTAL, mFacebookCallbackTotal);
		bundle.putBoolean(Constants.TWITTER_IS_REFRESHED, mTwitterIsRefreshed);
		bundle.putBoolean(Constants.FACEBOOK_IS_REFRESHED, mFacebookIsRefreshed);		
	}
    
    @Override
	public void onTwitterLogin() {
  		//redirection from twitter
		Log.i(TAG, "Twitter session opened");
		if (mTwitterAdapter.isLoggedIn()) {
  			mTwitterIsRefreshed = false;	
  			refreshTwitterMenuItem();
			refreshTwitterFeed();
		}
	}

	@Override
	public void onUserRequestCompleted(GraphUser user) {
		String userId = user.getId();
		for (SocialMediaUser member : mDataAdapter.getEnabledSocialMediaUsers(this)) {
			if (member.getFacebookUserId().equals(userId)) {
				member.setFacebookUser(user);
				mFacebookAdapter.makeFeedRequest(this, member.getFacebookUserId(), this);
				break;
			}
		}	
	}
	
	@Override
	public void onUserRequestCompleted(User user) {
		Long userId = user.getId();
		for (SocialMediaUser member : mDataAdapter.getEnabledSocialMediaUsers(this)) {
			if (member.getTwitterUserId().equals(userId)) {
				member.setTwitterUser(user);
				mTwitterAdapter.makeFeedRequest(member.getTwitterUserId(), this);
				break;
			}
		}
	}
    
    @Override
	public void onFacebookPageScrolled(int scrollY, int dy) {
		synchronized (mFacebookScrollY) {
			mFacebookScrollY = scrollY;
			setCurrentTabTranslationY(mFacebookScrollY);
			updateBackground();
	    	updateToolbar() ;
		    updateTabs();
		}
	}
    
    @Override
	public void onTwitterPageScrolled(int scrollY, int dy) {
		synchronized (mTwitterScrollY) {
			mTwitterScrollY = scrollY;
	        setCurrentTabTranslationY(mTwitterScrollY);
	        updateBackground();
	    	updateToolbar() ;
		    updateTabs();
		}
	}
    
    public void onFacebookScrollStateChanged(int newState) {
    	if(newState == RecyclerView.SCROLL_STATE_IDLE) {
    		
    	}   	
    }
    
    public void onTwitterScrollStateChanged(int newState) {
    	
    }

    private void showTabs() {
//		if (mTabs.getTranslationY()<0) {
			animateYTranslation(mTabs, 0);
			animateYTranslation(mTabsBackground, - mMaxTabTranslationY);
//		}
	}

	private void hideTabs() {
//		if (mTabs.getTranslationY() < 0 && mTabs.getTranslationY() > -mTabHeight) {
			animateYTranslation(mTabs, -mTabHeight);
			animateYTranslation(mTabsBackground, - mTabHeight - mMaxTabTranslationY);
//		}
	}
	

	private void initTranslationYValues() {		
		mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
		mAppBarHeight = getResources().getDimensionPixelSize(R.dimen.app_bar_height);
		mMaxTabTranslationY = getResources().getDimensionPixelSize(R.dimen.max_tab_translation_y);				
		mCurrentTabTranslationY = mMaxTabTranslationY;
	}

	private void animateHeaderTransition(int menuIndex) {		
		// set new tab translation
		switch (menuIndex) {
		case FACEBOOK:
			setCurrentTabTranslationY(mFacebookScrollY);
			break;
		case TWITTER:
			setCurrentTabTranslationY(mTwitterScrollY);
		}	
		
        //background alpha
		float alpha = (float)
				(mMaxTabTranslationY - mCurrentTabTranslationY)/(mMaxTabTranslationY) ;
			
		// tool bar translation
		float translationY = mToolbar.getTranslationY();
		
		// animations		
        if (mCurrentTabTranslationY > mAppBarHeight) {  
        	if (translationY < 0) {
        	//show
        		animateYTranslation(mToolbar, 0);	    		
        	}
        } else {
        	//hide
        	if (translationY != -mAppBarHeight) {        		
        		animateYTranslation(mToolbar, -mAppBarHeight);
        	}
        }				

		animateYTranslation(mTabs, mCurrentTabTranslationY);
		animateYTranslation(mTabsBackground,mCurrentTabTranslationY - mMaxTabTranslationY);		
		mTabsBackground.animate().alpha(alpha).setDuration(300).start();
	}
    
    private void updateBackground() {
    	// animate container background
		float alpha = (float)
				(mMaxTabTranslationY - mCurrentTabTranslationY)/(mMaxTabTranslationY) ;		
		mTabsBackground.setAlpha(alpha);
    }
    
    private void updateToolbar() {
    	float translationY = mToolbar.getTranslationY();
		
	    if (mCurrentTabTranslationY > mAppBarHeight) {  
	    	if (translationY < 0) {
	    	//show
	    		animateYTranslation(mToolbar, 0);	    		
	    	}
	    } else {
	    	//hide
	    	if (translationY != -mAppBarHeight) {
	    		if (mCurrentTabTranslationY == 0) {
	    			//animation would be too slow
	    			mToolbar.setTranslationY(-mAppBarHeight);
	    		} else {
	    			animateYTranslation(mToolbar, -mAppBarHeight);
	    		}
	    	}
	    }
    }

	private void updateTabs () {
		mTabs.setTranslationY(mCurrentTabTranslationY);
		mTabsBackground.setTranslationY(mCurrentTabTranslationY - mMaxTabTranslationY);
	}

	private Integer setCurrentTabTranslationY(int scrollY){
		return mCurrentTabTranslationY = 
				( mMaxTabTranslationY + scrollY < 0 ) ? 0 : mMaxTabTranslationY + scrollY;
	}


	private void animateYTranslation(final View view, final int y) {
		
		if (view.getAnimation() == null) {
			AnimatorListener listener = new AnimatorListener() {

				@Override
				public void onAnimationCancel(Animator animation) {						
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					view.setTranslationY(y);						
				}

				@Override
				public void onAnimationRepeat(Animator animation) {						
				}

				@Override
				public void onAnimationStart(Animator animation) {					
				}
    			
    		};
		view.animate().translationY(y)
			.setInterpolator(new LinearInterpolator())
			.setDuration(100)
			.setListener(listener)
			.start();
		}		
	}
	
	private int getColorAlpha(int color, float alpha) {
		 if (alpha < 1) {
			 Integer value = (int) (alpha * 127);
			 if (value < 127) {
				 byte byteValue =  Byte.parseByte(value.toString());	
				 return Color.argb(byteValue, Color.red(color), Color.green(color), Color.blue(color));
			 }
		 }
		 return color;
	 }
	
	private void getFacebookUsers() {
		//check network connection
        if (Utility.isConnectedToNetwork(this, true)) { 	  				
			if (!mFacebookIsRefreshed && mFacebookAdapter.isLoggedIn()) { 
				for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers(this)) {
	        	if (member.getFacebookUser() == null) {
	        		mFacebookAdapter.makeUserRequest(this, member.getFacebookUserId(), this);
	        	}
	        }
			}
        }
	}

	private void getTwitterUsers() {
	 	//check network connection
        if (Utility.isConnectedToNetwork(this, true)) { 	  	
			if (!mTwitterIsRefreshed && mTwitterAdapter.isLoggedIn()) {
				for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers(this)) {
			    	if (member.getTwitterUser() == null) {
			    		mTwitterAdapter.makeUserRequest(member.getTwitterUserId(), this);
			    	}
			    }
			}
        }
	}

	private void refreshFacebookFeed() {
    	//check network connection
        if (Utility.isConnectedToNetwork(this, true)) { 	  				
			if (!mFacebookIsRefreshed && mFacebookAdapter.isLoggedIn()) { 
				
				mFacebookCallbackCount = 0;				
				for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers(this)) {
					mFacebookAdapter.makeFeedRequest(this, member.getFacebookUserId(), this);	
					mFacebookCallbackTotal += 1;
				}
				
				//set flag
				mFacebookIsRefreshed = true;
		        mFacebookPageFragment.setRefreshing(true);
			}
        }
    }
	
	private void refreshTwitterFeed() {
    	//check network connection
        if (Utility.isConnectedToNetwork(this, true)) { 	  	
			if (!mTwitterIsRefreshed && mTwitterAdapter.isLoggedIn()) {

				mTwitterCallbackCount = 0;
				for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers(this)) {
					mTwitterAdapter.makeFeedRequest(member.getTwitterUserId(), this);
					mTwitterCallbackCount += 1;
				}

				//set flag
				mTwitterIsRefreshed = true;
				mTwitterPageFragment.setRefreshing(true);
			}
        }
    }
	
	private void setTabSelection(int menuIndex) {	
		switch (menuIndex) {
		case FACEBOOK:
			setTabSelected(mFacebookTab);
			setTabUnselected(mTwitterTab);
			break;
		case TWITTER:			
			setTabSelected(mTwitterTab);
			setTabUnselected(mFacebookTab);
		}
	}

	private void setTabSelected(ImageView tab) {		
    	tab.setAlpha(1f);
	}
	
	private void setTabUnselected(ImageView tab) {		
		tab.setAlpha(0.54f);
	}
	
	private void refreshFacebookMenuItem() {
		if (getVisibleMenuItem() == FACEBOOK){
			showMenuItem(FACEBOOK);
		}
		
		if (mFacebookCallbackCount == mFacebookCallbackTotal) {
			if (getVisibleMenuItem() == FACEBOOK){
				mFacebookScrollY = 0;
				animateHeaderTransition(FACEBOOK);
			}
			mFacebookPageFragment.refresh();
			mFacebookCallbackCount = mFacebookCallbackTotal = 0;			
		}
	}

	private void refreshTwitterMenuItem() {
		if (getVisibleMenuItem() == TWITTER){
			showMenuItem(TWITTER);				
		}	
		
		if (mTwitterCallbackCount == mTwitterCallbackTotal) {	
			if (getVisibleMenuItem() == TWITTER){
				mTwitterScrollY = 0;
				animateHeaderTransition(TWITTER);
			}
			mTwitterPageFragment.refresh();
			mTwitterCallbackCount = mTwitterCallbackTotal = 0;			
		}
	}

	private int getVisibleMenuItem() {  
		if (mTwitterPageFragment.isVisible() || mTwitterAccountFragment.isVisible()) return TWITTER;
		if (mFacebookPageFragment.isVisible()|| mFacebookAccountFragment.isVisible()) return FACEBOOK;
		return -1;
	}

	private BaseFragment getVisibleFragment() {  
		if (mTwitterPageFragment.isVisible()) return mTwitterPageFragment;
		if (mTwitterAccountFragment.isVisible()) return mTwitterAccountFragment;
		if (mFacebookPageFragment.isVisible()) return mFacebookPageFragment;
		if (mFacebookAccountFragment.isVisible()) return mFacebookAccountFragment;
		return null;
	}

	private BaseFragment getFragment(int index) {
		BaseFragment fragment = null;
		switch (index) {
			case FACEBOOK:
					fragment = mFacebookPageFragment;
					break;
			case FACEBOOK_LOGIN:
					fragment = mFacebookAccountFragment;
					break;
			case TWITTER:
					fragment = mTwitterPageFragment;
					break;
			case TWITTER_LOGIN:
					fragment = mTwitterAccountFragment;
					break;
		}
		return fragment;
	}

	private int determineFragmentIndex(int menuIndex) {
		int index = 0;
		switch (menuIndex) {
			case FACEBOOK:
				if (mFacebookAdapter.hasValidToken()) {
					index = FACEBOOK;
				} else {
					index = FACEBOOK_LOGIN;
				}
				break;
			case TWITTER:
				if (mTwitterAdapter.isLoggedIn()) { 
					index = TWITTER;
				} else {
					index = TWITTER_LOGIN;
				}
				break;
		}
		return index;
	}

	private void showMenuItem(int menuIndex) {
		setTabSelection(menuIndex);
		showFragment(getFragment(determineFragmentIndex(menuIndex)), false);
		animateHeaderTransition(menuIndex);
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

	private void restoreFragment(Bundle savedInstanceState){
		int fragmentIndex = savedInstanceState.getInt(Constants.FRAGMENT);
		showMenuItem(fragmentIndex);
	}
}

