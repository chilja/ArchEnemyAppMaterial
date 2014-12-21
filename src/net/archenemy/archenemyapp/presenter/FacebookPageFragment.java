package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.DataAdapter;
import net.archenemy.archenemyapp.model.Post;
import net.archenemy.archenemyapp.model.SocialMediaUser;

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
 * Page fragment displaying Facebook posts.
 * 
 * @author chiljagossow
 * 
 */

public class FacebookPageFragment extends PageFragment implements Serializable {

  /**
   * Passes refresh event.
   */
  public interface OnRefreshFeedListener {
    public void onRefreshFacebookFeed();
  }

  /**
   * Passes scrolling events.
   */
  public interface OnScrolledListener {
    public void onFacebookPageScrolled(int scrollY, int dy);
  }

  public static final String TAG = "FacebookPageFragment";

  private static final long serialVersionUID = 1L;

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
    }
    catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnScrolledListener");
    }
    try {
      onRefreshFeedListener = (OnRefreshFeedListener) activity;
    }
    catch (ClassCastException e) {
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
  protected List<FeedElement> getFeedElements() {
    List<FeedElement> list = new ArrayList<FeedElement>();
    socialMediaUsers = DataAdapter.getInstance().getEnabledSocialMediaUsers(getActivity());
    for (SocialMediaUser user : socialMediaUsers) {
      for (Post post : user.getPosts()) {
        list.add(new PostElement(post));
      }
    }
    Collections.sort(list);
    if (list.size() > 0) {
      // header placeholder
      list.add(0, new PostElement());
      return list;
    }
    return null;
  }

  @Override
  protected ViewHolder getViewHolder(ViewGroup parent) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
    return new PostElement.ViewHolder(view);
  }

  @Override
  protected void onFeedRefresh() {
    onRefreshFeedListener.onRefreshFacebookFeed();
  }

  @Override
  protected void onScrolled(RecyclerView recyclerView, int dy) {
    onScrolledListener.onFacebookPageScrolled(getScrollY(), dy);
  }
}
