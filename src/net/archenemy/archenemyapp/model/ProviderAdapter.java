package net.archenemy.archenemyapp.model;

/**
 * <p>Interface for social media provider</p>
 *
 * @author chiljagossow
 */
public interface ProviderAdapter {
	public boolean isEnabled();
	public boolean isLoggedIn();
	public void logOut();
}

