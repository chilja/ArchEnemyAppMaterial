package net.archenemy.archenemyapp.model;

import net.archenemy.archenemyapp.R;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

/**
 * <p>
 * Provides access to {@link SocialMediaUser SocialMediaUser}. User data must be provided as string
 * arrays with the names:
 * social_media_user_names (name strings)
 * twitter_user_ids (numerical IDs)
 * facebook_user_ids (numerical IDs)
 * preference_keys (same strings as preferences.xml).
 * </p>
 * 
 * @author chiljagossow
 */

public class DataAdapter {

  protected static final String TAG = "DataAdapter";
  private static DataAdapter dataAdapter;

  private static TreeMap<Integer, SocialMediaUser> socialMediaUsers;

  /**
   * Creates Singleton
   * @return DataAdapter instance
   */
  public static DataAdapter getInstance() {
    if (dataAdapter == null) {
      dataAdapter = new DataAdapter();
    }
    return dataAdapter;
  }

  /**
   * Creates {@link SocialMediaUser SocialMediaUser} from string arrays named
   * social_media_user_names (name strings)
   * twitter_user_ids (numerical IDs)
   * facebook_user_ids (numerical IDs)
   * preference_keys (same strings as preferences.xml).
   * @param context Context to access resources.
   * @return TreeMap with {@link SocialMediaUser SocialMediaUser}, key is array index + 1.
   * @throws Exception Throws exception if provided data is inconsistent.
   */
  protected static TreeMap<Integer, SocialMediaUser> createSocialMediaUsers(Context context)
      throws Exception {
    final TreeMap<Integer, SocialMediaUser> users = new TreeMap<Integer, SocialMediaUser>();

    String[] names = context.getResources().getStringArray(R.array.social_media_user_names);
    String[] twitterUserIds = context.getResources().getStringArray(R.array.twitter_user_ids);
    String[] facebookUserIds = context.getResources().getStringArray(R.array.facebook_user_ids);
    String[] preferenceKeys = context.getResources().getStringArray(R.array.preference_keys);

    // check if data is consistent
    if ((names.length != twitterUserIds.length) 
        || (names.length != facebookUserIds.length)
        || (names.length != preferenceKeys.length)) {
      throw new Exception();
    }

    for (int i = 0; i < names.length; i++) {
      int key = i + 1;
      String name = names[i];
      Long twitterUserId = Long.parseLong(twitterUserIds[i]);
      String facebookUserId = facebookUserIds[i];
      String preferenceKey = preferenceKeys[i];
      users.put(key, new SocialMediaUser(name, preferenceKey, key, twitterUserId, facebookUserId));
    }

    return users;
  }

  private DataAdapter() {
    // prevent instantiation from outside
  }

  /**
   * Returns {@link SocialMediaUser SocialMediaUser} that are enabled via
   * preferences.
   * 
   * @param context Context to access resources
   * @return array of {@link SocialMediaUser SocialMediaUser}
   */
  public ArrayList<SocialMediaUser> getEnabledSocialMediaUsers(Context context) {
    TreeMap<Integer, SocialMediaUser> users = getSocialMediaUsers(context);
    ArrayList<SocialMediaUser> enabledMembers = new ArrayList<SocialMediaUser>();
    Set<Integer> keys = users.keySet();
    for (Integer key : keys) {
      if (users.get(key).isEnabled(context)) {
        enabledMembers.add(users.get(key));
      }
    }
    return enabledMembers;
  }
  
  /**
   * Returns {@link SocialMediaUser SocialMediaUser} as provided in string arrays named
   * social_media_user_names (name strings)
   * twitter_user_ids (numerical IDs)
   * facebook_user_ids (numerical IDs)
   * preference_keys (same strings as preferences.xml).
   * @param context Context to access resources
   * @return TreeMap with {@link SocialMediaUser SocialMediaUser}, key is array index + 1.
   */
  public TreeMap<Integer, SocialMediaUser> getSocialMediaUsers(Context context) {
    if (socialMediaUsers == null) {
      try {
        socialMediaUsers = createSocialMediaUsers(context);
      }
      catch (Exception e) {
        Log.e(TAG, e.toString());
      }
    }
    return socialMediaUsers;
  }
}