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

public class AccountActivity extends FacebookActivity
 implements 
   FacebookAdapter.OnFacebookLoginListener,
   TwitterAdapter.TwitterLoginCallback{

	private static final int FACEBOOK = 0;
	private static final int TWITTER = 1;
	public static final String TAG = "FacebookActivity";
	
	private FacebookAccountFragment facebookAccount;
	private TwitterAccountFragment twitterAccount;

  private SlidingTabLayout slidingTabLayout;
  private ViewPager viewPager;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (this.twitterAccount != null) {
    	this.twitterAccount.onActivityResult(requestCode, resultCode, data);
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

    this.twitterAccount = new TwitterAccountFragment();
    this.facebookAccount = new FacebookAccountFragment();
    this.twitterAccount.showHeader(false);
    this.facebookAccount.showHeader(false);

    final BaseFragment[] fragments = new BaseFragment[2];
    fragments[FACEBOOK] = facebookAccount;
    fragments[TWITTER] = twitterAccount;
    
		this.viewPager = (ViewPager) findViewById(R.id.viewpager);
    this.viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), fragments));
    this.slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    this.slidingTabLayout.setIndicatorColor(getResources().getColor(R.color.accent));
    this.slidingTabLayout.setCustomTabView(R.layout.tab, R.id.tabIcon);
    this.slidingTabLayout.setViewPager(this.viewPager);
	}

	@Override
	public void onFacebookLogin() {
		 this.facebookAccount.onFacebookLogin();
	}

	@Override
	public void onTwitterLogin() {}
}
