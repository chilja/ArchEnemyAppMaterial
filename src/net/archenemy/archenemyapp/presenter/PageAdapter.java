package net.archenemy.archenemyapp.presenter;

/**
 * Custom implementation of a FragmentPagerAdapter
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class PageAdapter extends FragmentPagerAdapter {

	private BaseFragment[] fragments;

  public PageAdapter(FragmentManager fm, BaseFragment[] fragments) {
		super(fm);
		this.fragments = fragments;
	}

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override
  public int getCount() {
    return fragments.length;
  }

  public int getIconResId(int position) {
  	return fragments[position].getIconResId();
  }

  @Override
  public Fragment getItem(int position) {
  	return fragments[position];
  }

}

