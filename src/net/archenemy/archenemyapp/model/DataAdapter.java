package net.archenemy.archenemyapp.model;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;

public abstract class DataAdapter {

	protected static final String TAG = "DataAdapter";
	
	protected TreeMap<Integer,SocialMediaUser> mSocialMediaUsers = createSocialMediaUsers();
		
	public abstract TreeMap<Integer,SocialMediaUser> createSocialMediaUsers();
		
	public SocialMediaUser getSocialMediaUser(int userId) {		
		return mSocialMediaUsers.get(userId);
	}
	
	public TreeMap<Integer,SocialMediaUser> getSocialMediaUsers(){
		return mSocialMediaUsers;
	}
	
	public ArrayList<SocialMediaUser> getEnabledSocialMediaUsers(Activity activity){
		TreeMap<Integer,SocialMediaUser> users = getSocialMediaUsers();
		ArrayList<SocialMediaUser> enabledMembers = new ArrayList<SocialMediaUser>();
		Set<Integer> keys = users.keySet();
		for (Integer key: keys) {
			if (users.get(key).isEnabled(activity)) enabledMembers.add(users.get(key));
		}
		return enabledMembers;
	}
}
