package net.archenemy.archenemyapp.model;

import android.app.Activity;

import java.util.TreeMap;

public class ArchEnemyDataAdapter extends DataAdapter {

  private static ArchEnemyDataAdapter mDataAdapter;

  public static final String PREF_KEY_MICHAEL = "pref_key_michael_amott";
  public static final String PREF_KEY_ALYSSA = "pref_key_alyssa_white_gluz";

  public static ArchEnemyDataAdapter getInstance () {
    if (mDataAdapter == null) {
      mDataAdapter = new ArchEnemyDataAdapter();
    }
  	return mDataAdapter;
  }

  private ArchEnemyDataAdapter (){
  	//prevent instantiation
  }

  @Override
  public TreeMap<Integer,SocialMediaUser> createSocialMediaUsers(){
		final TreeMap<Integer,SocialMediaUser> users = new TreeMap<Integer,SocialMediaUser>();
		// String name, String prefKey, int userId,
		// String twitterUserId,
		// String facebookUserId)
  	users.put(1,
  		new SocialMediaUser("Arch Enemy", null, 1, "19564489","142695605765331") {
			  @Override
			  public boolean isEnabled(Activity activity) {
			    return true;
				}
  	});

  		users.put(2,
  				new SocialMediaUser("Alyssa White-Gluz", PREF_KEY_ALYSSA, 2,
  						"383472626",
  						 "49373264983"));

  		users.put(3,
  				new SocialMediaUser("Michael Amott", PREF_KEY_MICHAEL, 3,
  						 "88349752",
  						"116270908441437"));

  		return users;
  	}
  }
