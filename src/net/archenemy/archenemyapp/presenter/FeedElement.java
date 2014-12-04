package net.archenemy.archenemyapp.presenter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Interface for feed elements
 * @author chiljagossow
 *
 */
public interface FeedElement extends Comparable<FeedElement>{
	public void bindViewHolder(ViewHolder holder, Activity activity);
	public String getLink();
}
