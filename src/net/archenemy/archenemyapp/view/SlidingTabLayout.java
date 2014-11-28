package net.archenemy.archenemyapp.view;

import net.archenemy.archenemyapp.presenter.PageAdapter;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class SlidingTabLayout extends HorizontalScrollView {

    public interface TabColorizer {
        int getIndicatorColor(int position);
    }
 
//    private static final int TITLE_OFFSET_DIPS = 24;
//    private static final int TAB_VIEW_PADDING_DIPS = 16;
//    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;
 
//    private int mTitleOffset;
 
    private int mTabViewLayoutId;
    private int mTabViewImageViewId;
 
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;
 
    private final SlidingTabStrip mTabStrip;
 
    public SlidingTabLayout(Context context) {
        this(context, null);
    }
 
    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
 
        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);
 
//        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);
 
        mTabStrip = new SlidingTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }
 
    public void setIndicatorColor(int color) {
        mTabStrip.setIndicatorColor(color);
    }
 
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }
    
    public void setCustomTabView(int layoutResId, int imageViewId) {
        mTabViewLayoutId = layoutResId;
        mTabViewImageViewId = imageViewId;
    }
 
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();
 
        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }
 
    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final View.OnClickListener tabClickListener = new TabClickListener();
 
        for (int i = 0; i < adapter.getCount(); i++) {
            ViewGroup tabView = null;
            ImageView tabIcon = null;
 
            if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabView =  (ViewGroup) LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip, false);
                tabIcon  = (ImageView) tabView.findViewById(mTabViewImageViewId);
            }
            
           
          tabIcon.setImageResource(((PageAdapter)adapter).getIconResId(i));
          tabView.setOnClickListener(tabClickListener);
          mTabStrip.addView(tabView);
        }
    }
 
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
 
        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }
 
    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }
 
        View selectedTab = mTabStrip.getChildAt(tabIndex);
        if (selectedTab != null) {
            int targetScrollX = selectedTab.getLeft() + positionOffset;
            scrollTo(targetScrollX, 0);
            if (positionOffset == 0) {
            	setTabSelection(tabIndex);
            }
        }
    }
    
    private void setPageSelection(View selectedTabView) {
	    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
	    	View tabView = mTabStrip.getChildAt(i);	    	           	
	        if (selectedTabView == tabView) {
	            mViewPager.setCurrentItem(i);
	            return;
	        }
	    }
	}
    
    private void setTabSelection(View selectedTabView) {
	    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
	    	View tabView = mTabStrip.getChildAt(i);	    	           	
	        if (selectedTabView == tabView) {
	            setTabSelected(tabView);
	        } else {
	        	setTabUnselected(tabView);
	        }
	    }
	}
    
    private void setTabSelection(int position) {
    	setTabSelection(mTabStrip.getChildAt(position));
    }
    
    private void setTabSelected(View tab) {	
    	ImageView tabIcon  = (ImageView) tab.findViewById(mTabViewImageViewId);
    	tabIcon.setAlpha(1f);
	}
	
	private void setTabUnselected(View tab) {	
		ImageView tabIcon  = (ImageView) tab.findViewById(mTabViewImageViewId);
		tabIcon.setAlpha(0.54f);
	}
 
    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;
 
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }
 
            mTabStrip.onViewPagerPageChanged(position, positionOffset);
 
            View selectedTab = mTabStrip.getChildAt(position);

            int extraOffset = (selectedTab != null)
                    ? (int) (positionOffset * selectedTab.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);
 
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }
 
        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
 
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }
 
        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
                setTabSelection(position);
            }
 
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }
 
    }
 
    private class TabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {       	
        	setTabSelection(view);
        	setPageSelection(view);
        }        
    }
}
