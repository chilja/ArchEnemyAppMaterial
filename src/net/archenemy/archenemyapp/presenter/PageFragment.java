package net.archenemy.archenemyapp.presenter;

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
import android.view.animation.DecelerateInterpolator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class PageFragment extends BaseFragment
	implements Serializable {

	class FeedAdapter extends RecyclerView.Adapter<ViewHolder> {

		private final List<FeedElement> listElements;

    public FeedAdapter(List<FeedElement> listElements) {
    	this.listElements = listElements;
    }

    @Override
    public int getItemCount() {
      return this.listElements.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    	this.listElements.get(position).bindViewHolder(holder, getActivity());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return getViewHolder(parent);
    }
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

	private static final long serialVersionUID = 1L;
	public static final String TAG = "FacebookPageFragment";
	
	protected transient RecyclerView recyclerView;
	protected transient FeedAdapter adapter;
	protected transient RecyclerView.LayoutManager layoutManager;
	protected List<FeedElement> listElements = new ArrayList<FeedElement>();
	protected static ArrayList<SocialMediaUser> socialMediaUsers;
	protected SwipeRefreshLayout swipeRefreshLayout;
	protected View parentView;

	@Override
	public View onCreateView(LayoutInflater inflater,
	    ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		this.parentView = inflater.inflate(R.layout.page_fragment, container, false);

		socialMediaUsers = ArchEnemyDataAdapter.getInstance().getEnabledSocialMediaUsers(getActivity());

    this.layoutManager = new LinearLayoutManager(getActivity());
	 	this.recyclerView = (RecyclerView) this.parentView.findViewById(R.id.recyclerView);
    this.recyclerView.setLayoutManager(this.layoutManager);
    this.recyclerView.setOnScrollListener(new ScrollListener());

    this.swipeRefreshLayout = (SwipeRefreshLayout) this.parentView.findViewById(R.id.swipeRefresh);
    this.swipeRefreshLayout.setColorSchemeColors(
      getActivity().getResources().getColor(R.color.accent),
      getActivity().getResources().getColor(R.color.accent_dark));

    this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
         onFeedRefresh();
      }
    });

    refresh();

	  return this.parentView;
	}

	@Override
	public void refresh() {
		if (this.isAttached) {
			final int start = this.recyclerView.getBottom();
			
			if ((this.listElements == null) || (this.listElements.size() == 1)) {
        this.swipeRefreshLayout.setTranslationY(start);
      }
			
			this.listElements = getListElements();
			if ((this.listElements != null) && (this.listElements.size() > 0) && (getActivity() != null) && (this.recyclerView != null)) {
				// specify an adapter (see also next example)
        this.adapter = new FeedAdapter(this.listElements);
        this.recyclerView.setAdapter(this.adapter);
			}
			
			if (this.adapter != null) {
				this.adapter.notifyDataSetChanged();
				animateEnterTransition();
			}
			
			// Stop the refreshing indicator
	    setRefreshing(false);
		}
	}

	public abstract void setRefreshing(boolean isRefreshing);

	private void animateEnterTransition() {

		final AnimatorListener listener = new AnimatorListener() {
			@Override
			public void onAnimationCancel(Animator animation) {	}

			@Override
			public void onAnimationEnd(Animator animation) {
				PageFragment.this.swipeRefreshLayout.setTranslationY(0);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {	}

			@Override
			public void onAnimationStart(Animator animation) { }

		};
		
		this.swipeRefreshLayout.animate().translationY(0)
		.setInterpolator(new DecelerateInterpolator())
		.setDuration(300)
		.setListener(listener)
		.start();
	}

	protected abstract List<FeedElement> getListElements();

	protected int getScrollY(RecyclerView recyclerView) {
	  
    final View firstChild = recyclerView.getChildAt(0);
    if (firstChild == null) {
        return 0;
    }
    
    final int firstVisiblePosition = recyclerView.getChildPosition(recyclerView.findChildViewUnder(0.0F, 0.0F));
    final int top = firstChild.getTop();

    int headerHeight = 0;
    if (firstVisiblePosition >= 1) {
        headerHeight = 154 * 3;
    }

    return top - (firstVisiblePosition * firstChild.getHeight()) - headerHeight;
	}

	protected abstract ViewHolder getViewHolder(ViewGroup parent);

	protected abstract void onFeedRefresh();

	protected abstract void onScrolled(RecyclerView recyclerView, int dy);

	protected abstract void onScrollStateChanged(int newState);
}
