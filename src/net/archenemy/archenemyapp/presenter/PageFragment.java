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

/**
 * Base fragment for feed pages
 * @author chiljagossow
 *
 */
public abstract class PageFragment extends BaseFragment
	implements Serializable {

	class FeedAdapter extends RecyclerView.Adapter<ViewHolder> {

		private final List<FeedElement> listElements;

    public FeedAdapter(List<FeedElement> listElements) {
    	this.listElements = listElements;
    }

    @Override
    public int getItemCount() {
      return listElements.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    	listElements.get(position).bindViewHolder(holder, getActivity());
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
		parentView = inflater.inflate(R.layout.page_fragment, container, false);

		socialMediaUsers = ArchEnemyDataAdapter.getInstance().getEnabledSocialMediaUsers(getActivity());

    layoutManager = new LinearLayoutManager(getActivity());
	 	recyclerView = (RecyclerView) parentView.findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setOnScrollListener(new ScrollListener());

    swipeRefreshLayout = (SwipeRefreshLayout) parentView.findViewById(R.id.swipeRefresh);
    swipeRefreshLayout.setColorSchemeColors(
      getActivity().getResources().getColor(R.color.accent),
      getActivity().getResources().getColor(R.color.accent_dark));

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
         onFeedRefresh();
      }
    });

    refresh();

	  return parentView;
	}

	@Override
	public void refresh() {
		if (isAttached) {
			final int start = recyclerView.getBottom();

			if ((listElements == null) || (listElements.size() == 1)) {
        swipeRefreshLayout.setTranslationY(start);
      }

			listElements = getListElements();
			if ((listElements != null) && (listElements.size() > 0) && (getActivity() != null) && (recyclerView != null)) {
				// specify an adapter (see also next example)
        adapter = new FeedAdapter(listElements);
        recyclerView.setAdapter(adapter);
			}

			if (adapter != null) {
				adapter.notifyDataSetChanged();
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
				swipeRefreshLayout.setTranslationY(0);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {	}

			@Override
			public void onAnimationStart(Animator animation) { }

		};

		swipeRefreshLayout.animate().translationY(0)
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
}
