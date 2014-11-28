package net.archenemy.archenemyapp.presenter;

import java.util.ArrayList;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.ArchEnemyDataAdapter;
import net.archenemy.archenemyapp.model.BitmapUtility;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import net.archenemy.archenemyapp.model.TwitterAdapter;
import net.archenemy.archenemyapp.model.Utility;
import twitter4j.User;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity 
	extends 
		FacebookActivity 
	implements 
		TwitterAdapter.FeedCallback,		 
		TwitterAdapter.UserCallback, 
		TwitterAccountFragment.OnTwitterLoginListener,
		TwitterPageFragment.OnRefreshFeedListener,
		TwitterPageFragment.OnScrolledListener,
		FacebookAdapter.FeedCallback,
		FacebookAccountFragment.OnFacebookLoginListener,
		FacebookPageFragment.OnRefreshFeedListener,
		FacebookPageFragment.OnScrolledListener{
	
	private static final String TAG = "MainActivity";
	
	//menu positions = main fragment index
	private static final int FACEBOOK = 0;
	private static final int TWITTER = 1;
	private static final int FACEBOOK_LOGIN = 3;
	private static final int TWITTER_LOGIN = 4;
	private static int mSelectedMenuItem;// initial selection
	
	private FragmentManager mFragmentManager;
	private ArchEnemyDataAdapter mDataAdapter;
	
	private Toolbar mToolbar;
	

//header translation Y
	private FrameLayout mContainer;
	private LinearLayout mTabs;
	
    private int mAppBarHeight; 
    private int mHeaderHeightFull;
    private int mHeaderHeight;
    private int mTabHeight;
    private Integer mMaxTabTranslationY;
    private Integer mMinTabTranslationY;
    private Integer mCurrentTabTranslationY;
    private Integer mFacebookScrollY;
    private Integer mTwitterScrollY;
    private TypedValue mTypedValue = new TypedValue();

	
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
		                     
		mFragmentManager = getSupportFragmentManager();
		    
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

	    mContainer =  (FrameLayout) findViewById(R.id.fragmentContainer);
	    mTabs =  (LinearLayout) findViewById(R.id.tabs);
		
    	if (savedInstanceState != null) {	
	    	restoreFragment(savedInstanceState);
	    	return;
	    } 
    	
    	refreshFacebookFeed();
    	refreshTwitterFeed();
    	
    	SharedPreferences sharedPreferences = 
		        PreferenceManager.getDefaultSharedPreferences(this);
        String start = sharedPreferences.getString(Constants.PREF_KEY_START, Constants.FACEBOOK);
        mSelectedMenuItem = (Constants.FACEBOOK.equals(start))? FACEBOOK : TWITTER;
    	showMenuItem(mSelectedMenuItem);	 
    	initHeaderTranslationY();
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
	public void onResume() {
		super.onResume();
		if (getVisibleFragment()!=null)
			getVisibleFragment().refresh();
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	 
	    // Pass the activity result to the fragment, which will
	    // then pass the result to the login button.
	    if (mTwitterAccountFragment != null) {
	    	mTwitterAccountFragment.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//cancel background threads
		BitmapUtility.onDestroy();
		mTwitterAdapter.onDestroy();
	}

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
  	public void onFacebookLogin() {
  		//redirection from facebook 		
		Log.i(TAG, "Facebook session opened");
		if (mFacebookAdapter.isLoggedIn()) {
			mFacebookIsRefreshed = false;
			refreshFacebookMenuItem();
			refreshFacebookFeed();
		}
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
	
	private void refreshTwitterMenuItem() {
		if (getVisibleMenuItem() == TWITTER){
			showMenuItem(TWITTER);	
		}		
	}
	
	private void refreshFacebookMenuItem() {
		if (getVisibleMenuItem() == FACEBOOK){
			showMenuItem(FACEBOOK);
		}
	}

	//Facebook Callback
	@Override
	public void onFeedRequestCompleted(ArrayList<FeedElement> elements, String id) {
		if (elements != null && id != null && elements.size() >0) {
			for (SocialMediaUser user: mDataAdapter.getEnabledSocialMediaUsers(this)) {
				if (id.equals(user.getFacebookUserId())) {
					user.setPosts(elements);
					break;
				}					
			}
		}

		refreshFacebookMenuItem();
		Log.i(TAG, "Received facebook feed");
	}
	
	
	
	//Twitter Callback
    public void onFeedRequestCompleted(ArrayList<FeedElement> elements, Long id) { 
    	if (elements != null && id != null && elements.size() >0) {
			for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers(this)) {
				if (id.equals(member.getTwitterUserId())) {
					member.setTweets(elements);
					break;
				}					
			}
		}

		refreshTwitterMenuItem();	
	    Log.i(TAG, "Received twitter feed");	
	}
    
    @Override
	public void onUserRequestCompleted(User user) {
		Long userId = user.getId();
		for (SocialMediaUser member : mDataAdapter.getEnabledSocialMediaUsers(this)) {
			if (member.getTwitterUserId().equals(userId)) {
				member.setTwitterUser(user);
				break;
			}
		}
		refreshTwitterFeed();
	}

	private void refreshFacebookFeed() {
    	//check network connection
        if (Utility.isConnectedToNetwork(this, true)) { 	  				
			if (!mFacebookIsRefreshed && mFacebookAdapter.isLoggedIn()) { 
				
				mFacebookCallbackCount = 0;				
				for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers(this)) {
					mFacebookAdapter.makeFeedRequest(this, member.getFacebookUserId(), this);	
					mFacebookCallbackCount += 1;
				}
				mFacebookCallbackTotal = mFacebookCallbackCount;
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
				
		        for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers(this)) {
		        	if (member.getTwitterUser() == null) {
		        		mTwitterAdapter.makeUserRequest(member.getTwitterUserId(), this);
		        	}
		        }
		        mTwitterCallbackTotal = mTwitterCallbackCount;
				//set flag
				mTwitterIsRefreshed = true;
				mTwitterPageFragment.setRefreshing(true);
			}
        }
    }
	
    private void setTabSelected(ImageView tab) {		
    	tab.setAlpha(1f);
	}
	
	private void setTabUnselected(ImageView tab) {		
		tab.setAlpha(0.54f);
	}
	
	void showMenuItem(int menuIndex) {
		int fragmentIndex = determineFragmentIndex(menuIndex);
		BaseFragment fragment = getFragment(fragmentIndex);
		setTabSelection(menuIndex);
		showFragment(fragment, false);
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

	BaseFragment getFragment(int index) {
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

	private void showFragment (BaseFragment fragment, boolean addToBackStack) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
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

	private void restoreFragment(Bundle savedInstanceState){
		int fragmentIndex = savedInstanceState.getInt(Constants.FRAGMENT);
		showMenuItem(fragmentIndex);
	}
	
	private void clearBackStack() {
		// Get the number of entries in the back stack
		int backStackSize = mFragmentManager.getBackStackEntryCount();
		// Clear the back stack
		if (backStackSize > 0) {
			for (int i = 0; i < backStackSize; i++) {
			    mFragmentManager.popBackStack();
			}
		}
	}
	
	BaseFragment getVisibleFragment() {  
		if (mTwitterPageFragment.isVisible()) return mTwitterPageFragment;
		if (mTwitterAccountFragment.isVisible()) return mTwitterAccountFragment;
		if (mFacebookPageFragment.isVisible()) return mFacebookPageFragment;
		if (mFacebookAccountFragment.isVisible()) return mFacebookAccountFragment;
		return null;
	}
	
	private int getVisibleMenuItem() {  
		if (mTwitterPageFragment.isVisible() || mTwitterAccountFragment.isVisible()) return TWITTER;
		if (mFacebookPageFragment.isVisible()|| mFacebookAccountFragment.isVisible()) return FACEBOOK;
		return -1;
	}
	
	private void initHeaderTranslationY() {
		mHeaderHeightFull = getResources().getDimensionPixelSize(R.dimen.header_height_full);		
		mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
		mAppBarHeight = getResources().getDimensionPixelSize(R.dimen.app_bar_height);
		mMaxTabTranslationY = getResources().getDimensionPixelSize(R.dimen.max_tab_translation_y);
		
		mMinTabTranslationY = mAppBarHeight;
		mHeaderHeight = mAppBarHeight + mTabHeight;
		mCurrentTabTranslationY = mMaxTabTranslationY;
	    mTwitterScrollY = 0;
	}
	
	private Integer updateTwitterScrollY(int dy) {
		return mTwitterScrollY = mTwitterScrollY - dy;
	}
	
	private Integer updateFacebookScrollY(int dy) {
		return mFacebookScrollY = mFacebookScrollY - dy;
	}
	
	private Integer updateCurrentTabTranslationY(int scrollY){
		return mCurrentTabTranslationY = (mMaxTabTranslationY + scrollY < mMinTabTranslationY)? mMinTabTranslationY :
			(mMaxTabTranslationY + scrollY > mMaxTabTranslationY)? mMaxTabTranslationY : mMaxTabTranslationY + scrollY;
	}

	@Override
	public void onFacebookPageScrolled(int dy) {
		synchronized (mFacebookScrollY) {
			updateFacebookScrollY(dy);
	        mTabs.setTranslationY(updateCurrentTabTranslationY(mFacebookScrollY));
	        if (mCurrentTabTranslationY > mMinTabTranslationY) {
	        	mToolbar.setBackgroundColor(getResources().getColor(Constants.TRANSPARENT));
	        	mTabs.setBackgroundColor(getResources().getColor(Constants.TRANSPARENT));
	        } else {
	        	mToolbar.setBackgroundColor(getResources().getColor(Constants.PRIMARY));
	        	mTabs.setBackgroundColor(getResources().getColor(Constants.PRIMARY));
	        }
		}
	}

	@Override
	public void onRefeshFacebookFeed() {
		refreshFacebookFeed();
	}

	@Override
	public void onTwitterPageScrolled( int dy) {
//		synchronized (mTwitterScrollY) {
//			updateTwitterScrollY(dy);
//	        mTabs.setTranslationY(updateCurrentTabTranslationY(mTwitterScrollY));
//	        if (mCurrentTabTranslationY > mMinTabTranslationY) {
//	        	mToolbar.setBackgroundColor(getResources().getColor(Constants.TRANSPARENT));
//	        	mTabs.setBackgroundColor(getResources().getColor(Constants.TRANSPARENT));
//	        } else {
//	        	mToolbar.setBackgroundColor(getResources().getColor(Constants.PRIMARY));
//	        	mTabs.setBackgroundColor(getResources().getColor(Constants.PRIMARY));
//	        }
//		}		
	}

	@Override
	public void onRefeshTwitterFeed() {
		refreshTwitterFeed();
	}
}

