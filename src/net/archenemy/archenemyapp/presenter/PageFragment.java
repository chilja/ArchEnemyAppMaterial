package net.archenemy.archenemyapp.presenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.ArchEnemyDataAdapter;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public abstract class PageFragment extends BaseFragment 
	implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TAG = "FacebookPageFragment";
	
	public static final String USER_ID = TAG + "mUserId";
	
	protected transient RecyclerView mRecyclerView;
	protected transient FeedAdapter mAdapter;
	protected transient RecyclerView.LayoutManager mLayoutManager;
	protected List<FeedElement> mListElements = new ArrayList<FeedElement>();
	protected static ArrayList<SocialMediaUser> mSocialMediaUsers;
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	
	public abstract void setRefreshing(boolean isRefreshing);
	
	protected abstract void onFeedRefresh();
	
	protected abstract ViewHolder getViewHolder(ViewGroup parent);	
	
	protected abstract void onScrolled(RecyclerView recyclerView, int dy);
	
	protected abstract void onScrollStateChanged(int newState);
	
	protected abstract List<FeedElement> getListElements();
	
	View mParentView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
		mParentView = inflater.inflate(R.layout.page_fragment, container, false);
    	
		mSocialMediaUsers = ArchEnemyDataAdapter.getInstance().getEnabledSocialMediaUsers(getActivity());
	    
        mLayoutManager = new LinearLayoutManager(getActivity());
	 	mRecyclerView = (RecyclerView) mParentView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager); 
        mRecyclerView.setOnScrollListener(new ScrollListener());
        
        mSwipeRefreshLayout = (SwipeRefreshLayout) mParentView.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeColors(
                getActivity().getResources().getColor(R.color.accent),
                getActivity().getResources().getColor(R.color.accent_dark));
        
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onFeedRefresh();
            }
        });
        
        refresh();
  	
	    return mParentView;
	}
	
	@Override
	public void refresh() {
		if (mIsAttached) {
			int start = mRecyclerView.getBottom();
			if (mListElements == null || mListElements.size() == 1)
				mSwipeRefreshLayout.setTranslationY(start);
				mListElements = getListElements();
			if (mListElements != null && mListElements.size() > 0 && getActivity() != null && mRecyclerView != null) {
				// specify an adapter (see also next example)
		        mAdapter = new FeedAdapter(mListElements);
		        mRecyclerView.setAdapter(mAdapter);
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
				animateEnterTransition();
			}
			 // Stop the refreshing indicator
	        setRefreshing(false);
		}
	}
	
	private void animateEnterTransition() {
		
		AnimatorListener listener = new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) { }

			@Override
			public void onAnimationEnd(Animator animation) {
				mSwipeRefreshLayout.setTranslationY(0);						
			}

			@Override
			public void onAnimationCancel(Animator animation) {	}

			@Override
			public void onAnimationRepeat(Animator animation) {	}
			
		};
		mSwipeRefreshLayout.animate().translationY(0)
		.setInterpolator(new DecelerateInterpolator())
		.setDuration(300)
		.setListener(listener)
		.start();			
	}
	
	protected int getScrollY(RecyclerView recyclerView) {
	    View firstChild = recyclerView.getChildAt(0);
	    if (firstChild == null) {
	        return 0;
	    }
	    int firstVisiblePosition = recyclerView.getChildPosition(recyclerView.findChildViewUnder(0.0F, 0.0F));
	    int top = firstChild.getTop();

	    int headerHeight = 0;
	    if (firstVisiblePosition >= 1) {
	        headerHeight = 154 * 3;
	    }

	    return top - firstVisiblePosition * firstChild.getHeight() - headerHeight;
	}
		
	class ScrollListener extends RecyclerView.OnScrollListener {

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			PageFragment.this.onScrolled(recyclerView, dy);
		}

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
			PageFragment.this.onScrollStateChanged(newState);
		}			
	}
	
	class FeedAdapter extends RecyclerView.Adapter<ViewHolder> {

		private List<FeedElement> mListElements;

	    public FeedAdapter(List<FeedElement> listElements) {
	    	mListElements = listElements;
	    }

	    @Override
	    public ViewHolder onCreateViewHolder(ViewGroup parent,
	                                                   int viewType) {	        
	        return getViewHolder(parent);
	    }

	    @Override
	    public void onBindViewHolder(ViewHolder holder, int position) {
	    	mListElements.get(position).bindViewHolder(holder, getActivity());
	    }

	    @Override
	    public int getItemCount() {
	        return mListElements.size();
	    }
	}  
}
