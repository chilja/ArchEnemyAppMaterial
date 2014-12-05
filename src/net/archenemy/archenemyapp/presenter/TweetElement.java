package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Tweet;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

/**
 * Twitter Tweed
 * @author chiljagossow
 *
 */
public class TweetElement
	implements
		FeedElement {

	public static class ViewHolder extends RecyclerView.ViewHolder {

	  private TextView messageView;
		private TextView dateView;
		private ImageView imageView;
		private ImageView avatarView;

		private FrameLayout placeholder;
		private RelativeLayout tweet;
		private View view;

    public ViewHolder(View view) {
      super(view);
      this.view = view;
      messageView = (TextView) view.findViewById(R.id.messageView);
      dateView = (TextView) view.findViewById(R.id.dateView);
    	imageView = (ImageView) view.findViewById(R.id.imageView);
    	avatarView= (ImageView) view.findViewById(R.id.avatarView);
    	placeholder= (FrameLayout) view.findViewById(R.id.placeholder);
    	tweet = (RelativeLayout) view.findViewById(R.id.tweet);
    }

    private void setAvatarUrl(String avatarUrl, float density) {
    // URL provided? -> load bitmap
    	if (avatarUrl != null){
    		BitmapUtility.loadBitmap(avatarUrl, avatarView,(int) (40 * density));
    	}
    }

		private void setDate(Date date) {
			dateView.setText(Utility.getDisplayDate(date));
		}

		private void setImageUrl(String imageUrl, int width) {
    	if (imageUrl != null){
    	  // URL provided? -> load bitmap
    		imageView.setImageBitmap(null);
    		BitmapUtility.loadBitmap(imageUrl, imageView, width, width);
    		imageView.setVisibility(View.VISIBLE);

    	} else {
    	  // no picture -> hide image view
    		imageView.setImageBitmap(null);
    		imageView.setVisibility(View.GONE);
    	}
		}

		private void setMessage(String message) {
		  messageView.setText(message);
		}
		
		private void setLink(final String link, final Activity activity) {
      view.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          Utility.startBrowserActivity(activity, link);
        }
      });
    }

		private void showPlaceholder(){
			placeholder.setVisibility(View.VISIBLE);
			tweet.setVisibility(View.GONE);
		}

		private void showTweet(){
			placeholder.setVisibility(View.GONE);
			tweet.setVisibility(View.VISIBLE);
		}
  }

	private Tweet tweet;
	private boolean isPlaceholder = false;

	public TweetElement(){
		//placeholder
		isPlaceholder = true;
	}

	public TweetElement(Tweet tweet) {
		this.tweet = tweet;
	}

	@Override
	public void bindViewHolder(
			android.support.v7.widget.RecyclerView.ViewHolder holder, Activity activity) {

		if (holder instanceof ViewHolder) {
			ViewHolder myHolder = (ViewHolder) holder;

			if (isPlaceholder){
				myHolder.showPlaceholder();
			}

			if (!isPlaceholder){
				float density = activity.getResources().getDisplayMetrics().density;
				int width = activity.getResources().getDisplayMetrics().widthPixels;
				myHolder.showTweet();
				myHolder.setMessage(tweet.getMessage());
				myHolder.setImageUrl(tweet.getImageUrl(), width);
				myHolder.setDate(tweet.getDate());
				myHolder.setAvatarUrl(tweet.getAvatarUrl(), density);
				myHolder.setLink(tweet.getLink(), activity);
			}
		}
	}

	@Override
	public int compareTo(FeedElement element) {
		if (element instanceof TweetElement) {
			if ((tweet.getDate().getTime() - ((TweetElement)element).tweet.getDate().getTime())<0) {
        return 1;
      }
			if ((tweet.getDate().getTime() - ((TweetElement)element).tweet.getDate().getTime())>0) {
        return -1;
      }
		}
		return 0;
	}

	@Override
	public String getLink() {
		return tweet.getLink();
	}
}
