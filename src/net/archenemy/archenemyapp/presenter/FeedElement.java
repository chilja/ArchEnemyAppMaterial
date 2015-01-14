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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Interface for feed elements with common methods
 * 
 * @author chiljagossow
 * 
 */
public interface FeedElement extends Comparable<FeedElement> {
  /**
   * Binds {@link RecyclerView.ViewHolder RecyclerView.ViewHolder} to data
   * 
   * @param holder
   * @param activity
   */
  public void bindViewHolder(ViewHolder holder, Activity activity);

  /**
   * 
   * @return Link that should be opened on click event
   */
  public String getLink();
}
