package net.archenemy.archenemyapp.presenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.ArchEnemyDataAdapter;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FacebookPageFragment extends PageFragment 
	implements Serializable{
	
	public static final String TAG = "FacebookPageFragment";
	private static final long serialVersionUID = 1L;
	
	public interface OnScrolledListener {
        public void onFacebookPageScrolled(int scrollY, int dy);
        public void onFacebookScrollStateChanged(int newState);
    }
   
    public interface OnRefreshFeedListener {
	    public void onRefeshFacebookFeed();
    }
		
	private static ArrayList<SocialMediaUser> mSocialMediaUsers;
	private OnScrolledListener mOnScrolledListener;
	private OnRefreshFeedListener mOnRefreshFeedListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOnScrolledListener = (OnScrolledListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnScrolledListener");
        }
		try {
			mOnRefreshFeedListener = (OnRefreshFeedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnRefreshFeedListener");
        }
	}
			
	@Override
	public String getTAG() {
		return TAG;
	}

	@Override
	protected List<FeedElement> getListElements() {
		List<FeedElement> list = new ArrayList<FeedElement>();	
		mSocialMediaUsers = ArchEnemyDataAdapter.getInstance().getEnabledSocialMediaUsers(getActivity());
		for (SocialMediaUser user:mSocialMediaUsers) {
			list.addAll(user.getPosts());	
		}
		Collections.sort(list);
		//header placeholder
		list.add(0,new Post());
		return list;
	}

	@Override
	protected ViewHolder getViewHolder(ViewGroup parent) {
		// create a new view
        View view = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.post, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new Post.ViewHolder(view);
	}

	@Override
	public void setRefreshing(boolean isRefreshing) {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(isRefreshing);	
		}
	}

	@Override
	protected void onFeedRefresh() {
		mOnRefreshFeedListener.onRefeshFacebookFeed();
	}

	@Override
	protected void onScrolled(RecyclerView recyclerView, int dy) {
		mOnScrolledListener.onFacebookPageScrolled(getScrollY(recyclerView), dy);	
	}

	@Override
	protected void onScrollStateChanged(int newState) {
		mOnScrolledListener.onFacebookScrollStateChanged(newState);
	}
}
