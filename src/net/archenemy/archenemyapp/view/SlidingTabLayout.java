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

package net.archenemy.archenemyapp.view;

import net.archenemy.archenemyapp.presenter.BaseFragmentPagerAdapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

/**
 * Layout with tabs and a sliding indicator
 * 
 */
public class SlidingTabLayout extends HorizontalScrollView {

  private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
    private int scrollState;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      int tabStripChildCount = tabStrip.getChildCount();
      if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
        return;
      }

      tabStrip.onViewPagerPageChanged(position, positionOffset);

      View selectedTab = tabStrip.getChildAt(position);

      int extraOffset = (selectedTab != null) ? (int) (positionOffset * selectedTab.getWidth()) : 0;

      scrollToTab(position, extraOffset);

      if (viewPagerPageChangeListener != null) {
        viewPagerPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
      }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
      scrollState = state;

      if (viewPagerPageChangeListener != null) {
        viewPagerPageChangeListener.onPageScrollStateChanged(state);
      }
    }

    @Override
    public void onPageSelected(int position) {
      if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
        tabStrip.onViewPagerPageChanged(position, 0f);
        scrollToTab(position, 0);
        setTabSelection(position);
      }

      if (viewPagerPageChangeListener != null) {
        viewPagerPageChangeListener.onPageSelected(position);
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

  private int tabViewLayoutId;
  private int tabViewImageViewId;

  private ViewPager viewPager;

  private ViewPager.OnPageChangeListener viewPagerPageChangeListener;

  private final SlidingTabStrip tabStrip;

  public SlidingTabLayout(Context context) {
    this(context, null);
  }

  public SlidingTabLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    setHorizontalScrollBarEnabled(false);
    setFillViewport(true);

    tabStrip = new SlidingTabStrip(context);
    addView(tabStrip, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
  }

  public void setCustomTabView(int layoutResId, int imageViewId) {
    tabViewLayoutId = layoutResId;
    tabViewImageViewId = imageViewId;
  }

  public void setIndicatorColor(int color) {
    tabStrip.setIndicatorColor(color);
  }

  public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
    viewPagerPageChangeListener = listener;
  }

  public void setViewPager(ViewPager viewPager) {
    tabStrip.removeAllViews();

    this.viewPager = viewPager;
    if (viewPager != null) {
      viewPager.setOnPageChangeListener(new InternalViewPagerListener());
      populateTabStrip();
    }
  }

  private void populateTabStrip() {
    final PagerAdapter adapter = viewPager.getAdapter();
    final View.OnClickListener tabClickListener = new TabClickListener();
    if (adapter.getCount() > 0) {
      int width = getResources().getDisplayMetrics().widthPixels;
      int tabWidth = width / adapter.getCount();
      for (int i = 0; i < adapter.getCount(); i++) {
        ViewGroup tabView = null;
        ImageView tabIcon = null;

        if (tabViewLayoutId != 0) {
          // If there is a custom tab view layout id set, try and inflate it
          tabView = (ViewGroup) LayoutInflater.from(getContext()).inflate(tabViewLayoutId,
              tabStrip, false);
          tabIcon = (ImageView) tabView.findViewById(tabViewImageViewId);
        }

        tabIcon.setImageResource(((BaseFragmentPagerAdapter) adapter).getIconResId(i));
        tabView.setOnClickListener(tabClickListener);
        tabStrip.addView(tabView, tabWidth, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
      }
    }
  }

  private void scrollToTab(int tabIndex, int positionOffset) {
    final int tabStripChildCount = tabStrip.getChildCount();
    if ((tabStripChildCount == 0) || (tabIndex < 0) || (tabIndex >= tabStripChildCount)) {
      return;
    }

    View selectedTab = tabStrip.getChildAt(tabIndex);
    if (selectedTab != null) {
      int targetScrollX = selectedTab.getLeft() + positionOffset;
      scrollTo(targetScrollX, 0);
      if (positionOffset == 0) {
        setTabSelection(tabIndex);
      }
    }
  }

  private void setPageSelection(View selectedTabView) {
    for (int i = 0; i < tabStrip.getChildCount(); i++) {
      View tabView = tabStrip.getChildAt(i);
      if (selectedTabView == tabView) {
        viewPager.setCurrentItem(i);
        return;
      }
    }
  }

  private void setTabSelected(View tab) {
    ImageView tabIcon = (ImageView) tab.findViewById(tabViewImageViewId);
    tabIcon.setAlpha(1f);
  }

  private void setTabSelection(int position) {
    setTabSelection(tabStrip.getChildAt(position));
  }

  private void setTabSelection(View selectedTabView) {
    for (int i = 0; i < tabStrip.getChildCount(); i++) {
      View tabView = tabStrip.getChildAt(i);
      if (selectedTabView == tabView) {
        setTabSelected(tabView);
      } else {
        setTabUnselected(tabView);
      }
    }
  }

  private void setTabUnselected(View tab) {
    ImageView tabIcon = (ImageView) tab.findViewById(tabViewImageViewId);
    tabIcon.setAlpha(0.54f);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    if (viewPager != null) {
      scrollToTab(viewPager.getCurrentItem(), 0);
    }
  }
}
