package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.DataAdapter;
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
 * Base fragment for feed pages. Handles common page behavior, eg. swipe
 * refresh, enter animation, scrolling.
 * 
 * @author chiljagossow
 * 
 */
public abstract class PageFragment extends BaseFragment implements Serializable {

  class FeedAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<FeedElement> feedElements;

    public FeedAdapter(List<FeedElement> feedElements) {
      super();
      this.feedElements = feedElements;
    }

    @Override
    public int getItemCount() {
      return feedElements.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      feedElements.get(position).bindViewHolder(holder, getActivity());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return getViewHolder(parent);
    }

    private List<FeedElement> getFeedElements() {
      return feedElements;
    }
  }

  class ScrollListener extends RecyclerView.OnScrollListener {

    private int recentDy;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
      recentDy = dy;
      PageFragment.this.onScrolled(recyclerView, dy);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
      PageFragment.this.onScrollStateChanged(recyclerView, recentDy);
    }
  }

  private static final long serialVersionUID = 1L;
  public static final String TAG = "FacebookPageFragment";

  protected static ArrayList<SocialMediaUser> socialMediaUsers;

  protected transient RecyclerView recyclerView;
  protected transient RecyclerView.LayoutManager layoutManager;
  protected transient FeedAdapter adapter;

  protected transient SwipeRefreshLayout swipeRefreshLayout;
  protected transient View parentView;

  protected List<FeedElement> feedElements;

  private int headerHeight;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    headerHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    super.onCreateView(inflater, container, savedInstanceState);
    parentView = inflater.inflate(R.layout.page_fragment, container, false);

    socialMediaUsers = DataAdapter.getInstance().getEnabledSocialMediaUsers(getActivity());

    layoutManager = new LinearLayoutManager(getActivity());

    recyclerView = (RecyclerView) parentView.findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setOnScrollListener(new ScrollListener());

    swipeRefreshLayout = (SwipeRefreshLayout) parentView.findViewById(R.id.swipeRefresh);
    swipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.accent),
        getActivity().getResources().getColor(R.color.accent_dark));

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        onFeedRefresh();
      }
    });

    if (savedInstanceState == null) {
      refresh();
    }

    return parentView;
  }

  @Override
  public void refresh() {
    if (isAttached && (recyclerView != null)) {
      boolean enterAnimation = false;
      if ((adapter == null) || (adapter.getFeedElements() == null)) {
        enterAnimation = true;
      }

      feedElements = getFeedElements();
      if ((feedElements != null) && (feedElements.size() > 0)) {
        if (adapter == null) {
          adapter = new FeedAdapter(feedElements);
          recyclerView.setAdapter(adapter);
        } else {
          adapter = new FeedAdapter(feedElements);
          recyclerView.swapAdapter(adapter, false);
        }

        if (enterAnimation) {
          animateEnterTransition();
        }
      }
      // Stop the refreshing indicator
      setRefreshing(false);
    }
  }

  public abstract void setRefreshing(boolean isRefreshing);

  private void animateEnterTransition() {
    swipeRefreshLayout.setTranslationY(recyclerView.getBottom());

    final AnimatorListener listener = new AnimatorListener() {
      @Override
      public void onAnimationCancel(Animator animation) {}

      @Override
      public void onAnimationEnd(Animator animation) {
        swipeRefreshLayout.setTranslationY(0);
      }

      @Override
      public void onAnimationRepeat(Animator animation) {}

      @Override
      public void onAnimationStart(Animator animation) {}
    };

    swipeRefreshLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator())
        .setDuration(300).setListener(listener).start();
  }

  protected abstract List<FeedElement> getFeedElements();

  protected int getScrollY() {
    if (recyclerView != null) {
      final View firstChild = recyclerView.getChildAt(0);
      if (firstChild == null) {
        return 0;
      }

      final int firstVisiblePosition = recyclerView.getChildPosition(recyclerView
          .findChildViewUnder(0.0F, 0.0F));
      final int top = firstChild.getTop();

      int headerHeight = 0;
      if (firstVisiblePosition >= 1) {
        headerHeight = this.headerHeight;
      }

      return top - (firstVisiblePosition * firstChild.getHeight()) - headerHeight;
    }
    return 0;
  }

  protected abstract ViewHolder getViewHolder(ViewGroup parent);

  protected abstract void onFeedRefresh();

  protected abstract void onScrolled(RecyclerView recyclerView, int dy);

  protected abstract void onScrollStateChanged(RecyclerView recyclerView, int dy);
}
