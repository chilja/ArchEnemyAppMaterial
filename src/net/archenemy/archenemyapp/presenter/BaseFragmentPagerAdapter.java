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

package net.archenemy.archenemyapp.presenter;

/**
 * Custom implementation of a FragmentPagerAdapter suitable for BaseFragments
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

  private BaseFragment[] fragments;

  public BaseFragmentPagerAdapter(FragmentManager fm, BaseFragment[] fragments) {
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
