package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.ArchEnemyDataAdapter;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import net.archenemy.archenemyapp.model.Tweet;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Page displaying Tweets
 * @author chiljagossow
 *
 */
public class TwitterPageFragment extends PageFragment
	implements Serializable {

  /**
   * Passes refresh event
   */
	public interface OnRefreshFeedListener {
    public void onRrefeshTwitterFeed();
  }

	/**
   * Passes scrolling events
   */
  public interface OnScrolledListener {
  	public void onTwitterPageScrolled(int scrollY, int dy);
  }

	private static final long serialVersionUID = 1L;
	public static final String TAG = "TwitterPageFragment";
	private static ArrayList<SocialMediaUser> socialMediaUsers;

	private OnScrolledListener onScrolledListener;
	private OnRefreshFeedListener onRefreshFeedListener;

	@Override
	public String getTAG() {
		return TAG;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onScrolledListener = (OnScrolledListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnScrolledListener");
    }
		try {
			onRefreshFeedListener = (OnRefreshFeedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnRefreshFeedListener");
    }
	}

	@Override
	public void setRefreshing(boolean isRefreshing) {
		if (swipeRefreshLayout != null) {
			swipeRefreshLayout.setRefreshing(isRefreshing);
		}
	}

	@Override
	protected List<FeedElement> getListElements() {
		List<FeedElement> list = new ArrayList<FeedElement>();
		socialMediaUsers = ArchEnemyDataAdapter.getInstance().getEnabledSocialMediaUsers(getActivity());
		for (SocialMediaUser user:socialMediaUsers) {
		  for (Tweet tweet : user.getTweets()) {
		    list.add(new TweetElement(tweet));
		  }
		}
		Collections.sort(list);
		//header placeholder
		list.add(0,new TweetElement());
		return list;
	}

	@Override
	protected ViewHolder getViewHolder(ViewGroup parent) {
    View view = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.tweet, parent, false);
    return new TweetElement.ViewHolder(view);
	}


	@Override
	protected void onFeedRefresh() {
		onRefreshFeedListener.onRrefeshTwitterFeed();
	}

	@Override
	protected void onScrolled(RecyclerView recyclerView,int dy) {
		onScrolledListener.onTwitterPageScrolled(getScrollY(), dy);
	}
}
