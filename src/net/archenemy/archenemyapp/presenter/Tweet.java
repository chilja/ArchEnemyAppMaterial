package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.BitmapUtility;
import net.archenemy.archenemyapp.model.Utility;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

public class Tweet
	implements
		FeedElement {

	public static class ViewHolder extends RecyclerView.ViewHolder {
		
	  private TextView messageView;
		private TextView dateView;
		private ImageView imageView;
		private ImageView avatarView;
		
		private FrameLayout placeholder;
		private RelativeLayout tweet;
		
    public ViewHolder(View view) {
      super(view);
      this.messageView = (TextView) view.findViewById(R.id.messageView);
      this.dateView = (TextView) view.findViewById(R.id.dateView);
    	this.imageView = (ImageView) view.findViewById(R.id.imageView);
    	this.avatarView= (ImageView) view.findViewById(R.id.avatarView);
    	this.placeholder= (FrameLayout) view.findViewById(R.id.placeholder);
    	this.tweet = (RelativeLayout) view.findViewById(R.id.tweet);
    }

    private void setAvatarUrl(String avatarUrl, float density) {
    // URL provided? -> load bitmap
    	if (avatarUrl != null){
    		BitmapUtility.loadBitmap(avatarUrl, this.avatarView,(int) (40 * density));
    	}
    }

		private void setDate(Date date) {
			this.dateView.setText(Utility.getDisplayDate(date));
		}

		private void setImageUrl(String imageUrl, int width) {
    	if (imageUrl != null){
    	  // URL provided? -> load bitmap
    		this.imageView.setImageBitmap(null);
    		BitmapUtility.loadBitmap(imageUrl, this.imageView, width, width);
    		this.imageView.setVisibility(View.VISIBLE);
    	
    	} else {
    	  // no picture -> hide image view
    		this.imageView.setImageBitmap(null);
    		this.imageView.setVisibility(View.GONE);
    	}
		}

		private void setMessage(String message) {
		  this.messageView.setText(message);
		}

		private void showPlaceholder(){
			this.placeholder.setVisibility(View.VISIBLE);
			this.tweet.setVisibility(View.GONE);
		}
		
		private void showTweet(){
			this.placeholder.setVisibility(View.GONE);
			this.tweet.setVisibility(View.VISIBLE);
		}
  }
	
	private String message;
	private Date date;
	private String link;
	private String imageUrl ;
	private String avatarUrl;
	private boolean isPlaceholder = false;

	public Tweet(){
		//placeholder
		this.isPlaceholder = true;
	}

	public Tweet(String name, String message, Date createdAt, String link, String avatarUrl) {
		this.date = createdAt;
		this.link = link;
		this.message = message;
		this.avatarUrl = avatarUrl;
	}

	public Tweet(String name, String message, Date createdAt, String link, String imageUrl, String avatarUrl) {
		this.date = createdAt;
		this.link = link;
		this.message = message;
		this.imageUrl = imageUrl;
		this.avatarUrl = avatarUrl;
	}

	@Override
	public void bindViewHolder(
			android.support.v7.widget.RecyclerView.ViewHolder holder, Activity activity) {
	  
		if (holder instanceof ViewHolder) {
			ViewHolder myHolder = (ViewHolder) holder;

			if (this.isPlaceholder){
				myHolder.showPlaceholder();
			}
			
			if (!this.isPlaceholder){
				float density = activity.getResources().getDisplayMetrics().density;
				int width = activity.getResources().getDisplayMetrics().widthPixels;
				myHolder.showTweet();
				myHolder.setMessage(this.message);
				myHolder.setImageUrl(this.imageUrl, width);
				myHolder.setDate(this.date);
				myHolder.setAvatarUrl(this.avatarUrl, density);
			}
		}
	}

	@Override
	public int compareTo(FeedElement element) {
		if (element instanceof Tweet) {
			if ((this.date.getTime() - ((Tweet)element).date.getTime())<0) {
        return 1;
      }
			if ((this.date.getTime() - ((Tweet)element).date.getTime())>0) {
        return -1;
      }
		}
		return 0;
	}

	@Override
	public String getLink() {
		return this.link;
	}
}
