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

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Post;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.internal.ImageRequest;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Displays a Facebook post.
 * 
 * @author chiljagossow
 * 
 */
public class PostElement implements FeedElement, Serializable {

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PostViewHolder";

    private RelativeLayout postLayout;
    private FrameLayout placeholder;

    private TextView messageView;
    private TextView teaserView;
    private TextView nameView;
    private TextView dateView;
    private ImageView expandButton;
    private ImageView collapseButton;
    private ImageView avatarView;
    private View view;
    private PostElement postElement;

    public ViewHolder(View view) {
      super(view);
      this.view = view;

      placeholder = (FrameLayout) view.findViewById(R.id.placeholder);
      postLayout = (RelativeLayout) view.findViewById(R.id.post);

      messageView = (TextView) view.findViewById(R.id.messageView);
      teaserView = (TextView) view.findViewById(R.id.teaserView);
      nameView = (TextView) view.findViewById(R.id.nameView);
      dateView = (TextView) view.findViewById(R.id.dateView);
      avatarView = (ImageView) view.findViewById(R.id.avatarView);

      expandButton = (ImageView) view.findViewById(R.id.expandButton);
      expandButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          expand();
        }
      });

      collapseButton = (ImageView) view.findViewById(R.id.collapseButton);
      collapseButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          collapse();
        }
      });
    }

    private void clearLink() {
      view.setOnClickListener(null);
    }

    private void collapse() {
      messageView.setVisibility(View.GONE);
      collapseButton.setVisibility(View.GONE);
      expandButton.setVisibility(View.VISIBLE);
      postElement.isExpanded = false;
    }

    private void expand() {
      messageView.setVisibility(View.VISIBLE);
      collapseButton.setVisibility(View.VISIBLE);
      expandButton.setVisibility(View.INVISIBLE);
      postElement.isExpanded = true;
    }

    private void setDate(Date date) {
      dateView.setText(Utility.getDisplayDate(date));
    }

    private void setImageUrl(String imageUrl, float density, Activity activity) {
      ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
      // URL provided? -> load bitmap
      if (imageUrl != null) {
        imageView.setImageBitmap(null);
        BitmapUtility.loadBitmap(activity, imageUrl, imageView, (int) (72 * density),
            (int) (72 * density));
        imageView.setVisibility(View.VISIBLE);

      } else {
        // no picture -> hide image view
        imageView.setVisibility(View.GONE);
      }
    }

    private void setLink(final String link, final Activity activity) {
      view.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          Utility.startBrowserActivity(activity, link);
        }
      });
    }

    private void setMessage(String message) {
      int length = message.length();
      int maxTeaserLength = 16;
      if (length > maxTeaserLength) {
        teaserView.setText(message.substring(0, maxTeaserLength) + "...");
        messageView.setText(message);
        expandButton.setVisibility(View.VISIBLE);
      } else {
        teaserView.setText(message);
        messageView.setText(null);
        expandButton.setVisibility(View.INVISIBLE);
      }
    }

    private void setName(String name) {
      nameView.setText(name);
    }

    private void setProfileId(String profileId, float density, Activity activity) {
      String avatarUrl = null;
      avatarView.setImageBitmap(null);
      int diameter = (int) activity.getResources().getDimension(R.dimen.avatar_diameter);
      try {
        avatarUrl = ImageRequest.getProfilePictureUrl(profileId, diameter, diameter).toString();
      }
      catch (URISyntaxException e) {
        Log.e(TAG, "URL invalid");
      }

      // URL provided? -> load bitmap
      if (avatarUrl != null) {
        BitmapUtility.loadBitmap(activity, avatarUrl, avatarView, diameter);
      }
    }

    private void showPlaceholder() {
      placeholder.setVisibility(View.VISIBLE);
      postLayout.setVisibility(View.GONE);
    }

    private void showPost() {
      placeholder.setVisibility(View.GONE);
      postLayout.setVisibility(View.VISIBLE);
    }
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Post post;
  private boolean isPlaceholder = false;
  private boolean isExpanded = false;

  /**
   * Creates a new instance that is empty (placeholder)
   */
  public PostElement() {
    // placeholder
    isPlaceholder = true;
  }

  /**
   * Creates a new instance
   * 
   * @param post
   *          Post holding the data to be displayed
   */
  public PostElement(Post post) {
    this.post = post;
    isPlaceholder = false;
  }

  @Override
  public void bindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder,
      Activity activity) {
    if (holder instanceof ViewHolder) {
      ViewHolder myHolder = (ViewHolder) holder;
      if (isPlaceholder) {
        myHolder.showPlaceholder();
        myHolder.clearLink();
      }
      if (!isPlaceholder) {
        float density = activity.getResources().getDisplayMetrics().density;
        myHolder.showPost();
        myHolder.setMessage(post.getMessage());
        myHolder.setImageUrl(post.getImageUrl(), density, activity);
        myHolder.setDate(post.getDate());
        myHolder.setName(post.getUserName());
        myHolder.setProfileId(post.getUserId(), density, activity);
        myHolder.setLink(post.getLink(), activity);
        myHolder.postElement = this;
        if (isExpanded) {
          myHolder.expand();
        } else {
          myHolder.collapse();
        }
      }
    }
  }

  @Override
  public int compareTo(FeedElement element) {
    if ((element != null) && (element instanceof PostElement)) {
      if ((post == null) || (post.getDate() == null)) {
        return 1;
      }
      if ((((PostElement) element).post == null)
          || (((PostElement) element).post.getDate() == null)) {
        return -1;
      }
      if ((post.getDate().getTime() - ((PostElement) element).post.getDate().getTime()) < 0) {
        return 1;
      }
      if ((post.getDate().getTime() - ((PostElement) element).post.getDate().getTime()) > 0) {
        return -1;
      }
    }
    return 0;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof PostElement) {
      if ((post == null) && (((PostElement) object).post == null)) {
        return true;
      }
      if ((post != null) && (((PostElement) object).post != null)) {
        return post.equals(((PostElement) object).post);
      }
    }
    return false;
  }

  @Override
  public String getLink() {
    return post.getLink();
  }
}
