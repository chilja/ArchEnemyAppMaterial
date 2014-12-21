package net.archenemy.archenemyapp.model;

/**
 * <p>
 * Interface for common methods of social media providers.
 * </p>
 * 
 * @author chiljagossow
 */
public interface ProviderAdapter {
  public boolean isEnabled();

  public boolean isLoggedIn();

  public void logOut();
}
