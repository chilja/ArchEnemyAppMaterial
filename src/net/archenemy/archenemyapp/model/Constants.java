package net.archenemy.archenemyapp.model;

import net.archenemy.archenemyapp.R;

public abstract class Constants {
	
	//colors
	public static final int ACCENT = R.color.accent;
	public static final int ACCENT_TRANSP = R.color.accent_transparent;
	public static final int PRIMARY = R.color.primary;
	
	public static final int TEXT_PRIMARY = R.color.text_primary;
	public static final int TEXT_SECONDARY = R.color.text_secondary;
	public static final int TEXT_DISABLED = R.color.text_disabled;
	public static final int DIVIDER = R.color.divider;
	
	public static final int BLACK_TRANSP = R.color.black_transparent;
	
	public static final int TRANSPARENT = android.R.color.transparent;
		
	public static final String FACEBOOK = "FACEBOOK";
	public static final String TWITTER = "TWITTER";
	
	//Keys for saving instance state
	public static final String TWITTER_USER_NAME = "net.archenemy.archenemyapp.TWITTER_USER_NAME";
	public static final String FACEBOOK_USER_NAME = "net.archenemy.archenemyapp.FACEBOOK_USER_NAME";
	
	public static final String TWITTER_IS_REFRESHED = "net.archenemy.archenemyapp.TWITTER_IS_REFRESHED";
	public static final String FACEBOOK_IS_REFRESHED = "net.archenemy.archenemyapp.FACEBOOK_IS_REFRESHED";
	
	public static final String TWITTER_CALLBACK_COUNT = "net.archenemy.archenemyapp.TWITTER_CALLBACK_COUNT";
	public static final String FACEBOOK_CALLBACK_COUNT = "net.archenemy.archenemyapp.FACEBOOK_CALLBACK_COUNT";
	
	public static final String TWITTER_CALLBACK_TOTAL = "net.archenemy.archenemyapp.TWITTER_CALLBACK_TOTAL";
	public static final String FACEBOOK_CALLBACK_TOTAL = "net.archenemy.archenemyapp.FACEBOOK_CALLBACK_TOTAL";
	
	public static final String TWITTER_PROGRESS_BAR_VISIBLE = "net.archenemy.archenemyapp.TWITTER_PROGRESS_BAR_VISIBLE";
	public static final String FACEBOOK_PROGRESS_BAR_VISIBLE = "net.archenemy.archenemyapp.FACEBOOK_PROGRESS_BAR_VISIBLE";
	
	public static final String POPUP_VISIBLE = "net.archenemy.archenemyapp.POPUP_VISIBLE";
	public static final String FRAGMENT = "net.archenemy.archenemyapp.FRAGMENT";
	public static final String DRAWER_OPEN = "net.archenemy.archenemyapp.DRAWER_OPEN";
	public static final String SHARE_PARAMS = "net.archenemy.archenemyapp.SHARE_PARAMS";
	
	//Preference keys
	public static final String PREF_KEY_START = "pref_startMenuItem";
}
