package net.archenemy.archenemyapp.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class PageAdapter extends FragmentPagerAdapter {
	
	private BaseFragment[] mFragments;
		 
        public PageAdapter(FragmentManager fm, BaseFragment[] fragments) {
			super(fm);
			mFragments = fragments;
		}

		/**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return mFragments.length;
        }
 
        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
//        @Override
//        public boolean isViewFromObject(View view, Object o) {
//            return o == view;
//        }


        @Override
        public Fragment getItem(int position) {
        	return mFragments[position];
        }
        
        public int getIconResId(int position) {
        	return mFragments[position].getIconResId();
        }
 
        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

        }
 
    }

