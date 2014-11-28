package net.archenemy.archenemyapp.presenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.ArchEnemyDataAdapter;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
	protected abstract void onScrolled(int dy);
	protected abstract List<FeedElement> getListElements();
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.page_fragment, container, false);
    	
		mSocialMediaUsers = ArchEnemyDataAdapter.getInstance().getEnabledSocialMediaUsers(getActivity());
	    
        mLayoutManager = new LinearLayoutManager(getActivity());
	 	mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager); 
        mRecyclerView.setOnScrollListener(new ScrollListener());
        
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.accent);
        
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onFeedRefresh();
            }
        });
        
        refresh();
  	
	    return view;
	}
			
	public void refresh() {
		if (mIsAttached) {
			mListElements = getListElements();
			if (mListElements != null && mListElements.size() > 0 && getActivity() != null && mRecyclerView != null) {
				// specify an adapter (see also next example)
		        mAdapter = new FeedAdapter(mListElements);
		        mRecyclerView.setAdapter(mAdapter);
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			 // Stop the refreshing indicator
//	        setRefreshing(false);
		}
	}
		
	class ScrollListener extends RecyclerView.OnScrollListener {

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			// TODO Auto-generated method stub
			super.onScrollStateChanged(recyclerView, newState);
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			// TODO Auto-generated method stub
			super.onScrolled(recyclerView, dx, dy);
			onScrolled(recyclerView, dx, dy);
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
	    	mListElements.get(position).bindViewHolder(holder);
	    }

	    @Override
	    public int getItemCount() {
	        return mListElements.size();
	    }
	}  
}
