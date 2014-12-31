package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Tweet;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.Date;

/**
 * Displays a Twitter Tweet.
 * 
 * @author chiljagossow
 * 
 */
public class TweetElement implements FeedElement {

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private TextView messageView;
    private TextView dateView;
    private ImageView imageView;
    private ImageView avatarView;

    private FrameLayout placeholder;
    private RelativeLayout tweet;
    private View view;

    private int containerWidth;
    private int imageHeight;
    private int avatarDiameter;

    private Context context;

    private float density;

    public ViewHolder(View view, int containerWidth, Context context) {
      super(view);
      this.view = view;
      this.context = context;

      messageView = (TextView) view.findViewById(R.id.messageView);
      dateView = (TextView) view.findViewById(R.id.dateView);
      imageView = (ImageView) view.findViewById(R.id.imageView);
      avatarView = (ImageView) view.findViewById(R.id.avatarView);
      placeholder = (FrameLayout) view.findViewById(R.id.placeholder);
      tweet = (RelativeLayout) view.findViewById(R.id.tweet);

      imageHeight = (containerWidth * 9) / 16; // image format 16:9
      avatarDiameter = (int) context.getResources().getDimension(R.dimen.avatar_diameter);
    }

    private void clearLink() {
      view.setOnClickListener(null);
    }

    private void setAvatarUrl(String avatarUrl) {
      // URL provided? -> load bitmap
      if (avatarUrl != null) {
        BitmapUtility.loadBitmap(context, avatarUrl, avatarView, avatarDiameter);
      }
    }

    private void setDate(Date date) {
      dateView.setText(Utility.getDisplayDate(date));
    }

    private void setImageUrl(String imageUrl) {

      if (imageUrl != null) {
        if (density == 0) {
          density = context.getResources().getDisplayMetrics().density;
        }
        // URL provided? -> load bitmap
        imageView.setImageBitmap(null);
        LayoutParams params = new LayoutParams(containerWidth, imageHeight);
        imageView.setLayoutParams(params);

        BitmapUtility.loadBitmap(context, imageUrl, imageView, containerWidth, imageHeight);
        imageView.setVisibility(View.VISIBLE);

      } else {
        // no picture -> hide image view
        imageView.setImageBitmap(null);
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
      messageView.setText(message);
    }

    private void showPlaceholder() {
      placeholder.setVisibility(View.VISIBLE);
      tweet.setVisibility(View.GONE);
    }

    private void showTweet() {
      placeholder.setVisibility(View.GONE);
      tweet.setVisibility(View.VISIBLE);
    }
  }

  private Tweet tweet;
  private boolean isPlaceholder = false;

  /**
   * Creates a new instance that is empty (placeholder)
   */
  public TweetElement() {
    // placeholder
    isPlaceholder = true;
  }

  /**
   * Creates a new instance
   * 
   * @param tweet
   *          Tweet holding the data to be displayed
   */
  public TweetElement(Tweet tweet) {
    this.tweet = tweet;
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
        myHolder.showTweet();
        myHolder.setMessage(tweet.getMessage());
        myHolder.setImageUrl(tweet.getImageUrl());
        myHolder.setDate(tweet.getDate());
        myHolder.setAvatarUrl(tweet.getAvatarUrl());
        myHolder.setLink(tweet.getLink(), activity);
      }
    }
  }

  @Override
  public int compareTo(FeedElement element) {
    if (element instanceof TweetElement) {
      if ((tweet == null) || (tweet.getDate() == null)) {
        return 1;
      }
      if ((((TweetElement) element).tweet == null)
          || (((TweetElement) element).tweet.getDate() == null)) {
        return -1;
      }
      if ((tweet.getDate().getTime() - ((TweetElement) element).tweet.getDate().getTime()) < 0) {
        return 1;
      }
      if ((tweet.getDate().getTime() - ((TweetElement) element).tweet.getDate().getTime()) > 0) {
        return -1;
      }
    }
    return 0;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof TweetElement) {
      if ((tweet == null) && (((TweetElement) object).tweet == null)) {
        return true;
      }
      if ((tweet != null) && (((TweetElement) object).tweet != null)) {
        return tweet.equals(((TweetElement) object).tweet);
      }
    }
    return false;
  }

  @Override
  public String getLink() {
    return tweet.getLink();
  }
}
