package net.archenemy.archenemyapp.model;

public interface ProviderAdapter {
	public boolean isEnabled();
	public boolean isLoggedIn();
	public void logOut();
}

