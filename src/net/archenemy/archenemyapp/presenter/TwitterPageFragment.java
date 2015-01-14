/**
 * Copyright 2014-present Chilja Gossow.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.DataAdapter;
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
 * Page fragment displaying Twitter Tweets.
 * 
 * @author chiljagossow
 * 
 */
public class TwitterPageFragment extends PageFragment implements Serializable {

  /**
   * Passes refresh event
   */
  public interface OnRefreshFeedListener {
    public void onRefreshTwitterFeed();
  }

  /**
   * Passes scrolling events
   */
  public interface OnScrolledListener {
    public void onTwitterPageScrolled(int scrollY, int dy);

    public void onTwitterPageScrollStateChanged(int scrollY, int dy);
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
      for (Tweet tweet : user.getTweets()) {
        list.add(new TweetElement(tweet));
      }
    }
    Collections.sort(list);
    if (list.size() > 0) {
      // header placeholder
      list.add(0, new TweetElement());
      return list;
    }
    return null;
  }

  @Override
  protected ViewHolder getViewHolder(ViewGroup parent) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet, parent, false);

    int containerWidth = 0;
    if (getActivity() instanceof MainActivity) {
      containerWidth = ((MainActivity) getActivity()).getContainerWidth();
    } else {
      containerWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
    }
    return new TweetElement.ViewHolder(view, containerWidth, getActivity());
  }

  @Override
  protected void onFeedRefresh() {
    onRefreshFeedListener.onRefreshTwitterFeed();
  }

  @Override
  protected void onScrolled(RecyclerView recyclerView, int dy) {
    onScrolledListener.onTwitterPageScrolled(getScrollY(), dy);
  }

  @Override
  protected void onScrollStateChanged(RecyclerView recyclerView, int dy) {
    onScrolledListener.onTwitterPageScrollStateChanged(getScrollY(), dy);
  }
}
