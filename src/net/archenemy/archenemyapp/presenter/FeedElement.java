package net.archenemy.archenemyapp.presenter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Interface for feed elements
 * @author chiljagossow
 *
 */
public interface FeedElement extends Comparable<FeedElement>{
  /**
   * Binds  {@link RecyclerView.ViewHolder RecyclerView.ViewHolder} to data
   * @param holder
   * @param activity
   */
	public void bindViewHolder(ViewHolder holder, Activity activity);

	/**
	 *
	 * @return Link that should be opened on click event
	 */
	public String getLink();
}
