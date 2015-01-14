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

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Basic class for fragments.
 * 
 * @author chiljagossow
 * 
 */
public abstract class BaseFragment extends Fragment {

  protected boolean isResumed = false;
  protected boolean isAttached = false;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    isAttached = true;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    isAttached = false;
  }

  @Override
  public void onPause() {
    super.onPause();
    isResumed = false;
  }

  @Override
  public void onResume() {
    super.onResume();
    isResumed = true;
  }

  protected void refresh() {}

  /**
   * Return resource Id for fragment icon if present
   * 
   * @return
   */
  int getIconResId() {
    return 0;
  }

  abstract String getTAG();
}
