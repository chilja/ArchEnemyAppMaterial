package net.archenemy.archenemyapp.presenter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.ViewHolder;

public interface FeedElement extends Comparable<FeedElement>{
	public String getLink();
	public void bindViewHolder(ViewHolder holder, Activity activity);
}
