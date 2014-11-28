package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.view.SlidingTabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountActivity extends FacebookActivity 
 implements FacebookAccountFragment.OnFacebookLoginListener,
 TwitterAccountFragment.OnTwitterLoginListener{
	
	private FacebookAccountFragment mFacebookAccount;
	private TwitterAccountFragment mTwitterAccount;
	private ImageView mTwitterTab;
	private ImageView mFacebookTab;
	private static final int FACEBOOK = 0;
	private static final int TWITTER = 1;
	public static final String TAG = null;
	
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.account_activity);
	    
	    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	    toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		setSupportActionBar(toolbar);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_activity_accounts);

	    mTwitterAccount = new TwitterAccountFragment();
	    mFacebookAccount = new FacebookAccountFragment();
	    mTwitterAccount.showHeader(false);
	    mFacebookAccount.showHeader(false);
		
	    BaseFragment[] fragments = new BaseFragment[2];
	    fragments[FACEBOOK] = getFragment(FACEBOOK);
	    fragments[TWITTER] = getFragment(TWITTER);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), fragments));
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);      
        mSlidingTabLayout.setIndicatorColor(getResources().getColor(R.color.accent));
        mSlidingTabLayout.setCustomTabView(R.layout.tab, R.id.tabIcon);
        mSlidingTabLayout.setViewPager(mViewPager);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (mTwitterAccount != null) {
	    	mTwitterAccount.onActivityResult(requestCode, resultCode, data);
	    }
	}

	@Override
	public void onFacebookLogin() {
		 mFacebookAccount.onFacebookLogin();
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
	
	void showMenuItem(int menuIndex) {
		setTabSelection(menuIndex);
		BaseFragment fragment = getFragment(menuIndex);
		showFragment(fragment, false);
	}
	
	private void setTabSelected(ImageView tab) {		
    	tab.setAlpha(1f);
	}
	
	private void setTabUnselected(ImageView tab) {		
		tab.setAlpha(0.54f);
	}
	
	private void showFragment (BaseFragment fragment, boolean addToBackStack) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, fragment, fragment.getTAG());
		transaction.show(fragment);
    	transaction.commit();
    	invalidateOptionsMenu();
	}
	
	BaseFragment getFragment(int index) {
		BaseFragment fragment = null;
		switch (index) {
			case FACEBOOK:
					fragment = mFacebookAccount;
					break;
			case TWITTER:
					fragment = mTwitterAccount;
					break;

		}
		return fragment;
	}

	@Override
	public void onTwitterLogin() {
		// TODO Auto-generated method stub		
	}
}
