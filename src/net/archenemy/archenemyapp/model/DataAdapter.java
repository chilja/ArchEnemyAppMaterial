package net.archenemy.archenemyapp.model;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;

/**
 * <p>Provides access to {@link SocialMediaUser SocialMediaUser}</p>
 * 
 * @author chiljagossow
 */

public abstract class DataAdapter {

	protected static final String TAG = "DataAdapter";
	
	protected TreeMap<Integer,SocialMediaUser> socialMediaUsers = createSocialMediaUsers();
		
	protected abstract TreeMap<Integer,SocialMediaUser> createSocialMediaUsers();
	
	/**
	 * Returns {@link SocialMediaUser SocialMediaUser} that are enabled via preferences
	 * @param activity
	 * @return array of {@link SocialMediaUser SocialMediaUser}
	 */
		
	public ArrayList<SocialMediaUser> getEnabledSocialMediaUsers(Activity activity){
		TreeMap<Integer,SocialMediaUser> users = getSocialMediaUsers();
		ArrayList<SocialMediaUser> enabledMembers = new ArrayList<SocialMediaUser>();
		Set<Integer> keys = users.keySet();
		for (Integer key: keys) {
			if (users.get(key).isEnabled(activity)) enabledMembers.add(users.get(key));
		}
		return enabledMembers;
	}
	
	public SocialMediaUser getSocialMediaUser(int userId) {		
		return socialMediaUsers.get(userId);
	}
	
	public TreeMap<Integer,SocialMediaUser> getSocialMediaUsers(){
		return socialMediaUsers;
	}
}
