package net.archenemy.archenemyapp.model;

import android.content.Context;

import java.util.TreeMap;

/**
 * <p>Defines Arch Enemy's band members</p>
 *
 * @author chiljagossow
 */

public class ArchEnemyDataAdapter extends DataAdapter {

  private static ArchEnemyDataAdapter dataAdapter;

  public static final String PREF_KEY_MICHAEL = "pref_key_michael_amott";
  public static final String PREF_KEY_ALYSSA = "pref_key_alyssa_white_gluz";

  /**
   * Create singleton
   * @return Singleton
   */

  public static ArchEnemyDataAdapter getInstance() {
    if (dataAdapter == null) {
      dataAdapter = new ArchEnemyDataAdapter();
    }
  	return dataAdapter;
  }

  private ArchEnemyDataAdapter(){
  	//prevent instantiation from outside
  }

  @Override
  protected TreeMap<Integer,SocialMediaUser> createSocialMediaUsers(){
		final TreeMap<Integer,SocialMediaUser> users = new TreeMap<Integer,SocialMediaUser>();
  	users.put(1,
  		new SocialMediaUser("Arch Enemy", null, 1, 19564489L,"142695605765331") {
			  @Override
			  public boolean isEnabled(Context context) {
			    return true;
				}
  	});

		users.put(2,
			new SocialMediaUser("Alyssa White-Gluz", PREF_KEY_ALYSSA, 2,
				383472626L,
				"49373264983"));

  	users.put(3,
  		new SocialMediaUser("Michael Amott", PREF_KEY_MICHAEL, 3,
  				88349752L,
  				"116270908441437"));

  	return users;
  }
}