package net.archenemy.archenemyapp.presenter;

import java.util.Date;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.BitmapUtility;
import net.archenemy.archenemyapp.model.Utility;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Tweet 
	implements 
		FeedElement {

	private String mMessage;
	private Date mDate;
	private String mLink;
	private String mImageUrl ;
	private String mAvatarUrl;
	private boolean mIsPlaceholder = false;
	private float mDensity;
	
	public Tweet(){
		//placeholder
		mIsPlaceholder = true;
	}

	public Tweet(String name, String message, Date createdAt, String link, String avatarUrl) {
		mDate = createdAt;
		mLink = link;
		mMessage = message;
		mAvatarUrl = avatarUrl;
	}
	
	public Tweet(String name, String message, Date createdAt, String link, String imageUrl, String avatarUrl) {
		mDate = createdAt;
		mLink = link;
		mMessage = message;
		mImageUrl = imageUrl;
		mAvatarUrl = avatarUrl;
	}
	
	@Override
	public int compareTo(FeedElement element) {
		if (element instanceof Tweet) {
			if ((mDate.getTime() - ((Tweet)element).mDate.getTime())<0) return 1;
			if ((mDate.getTime() - ((Tweet)element).mDate.getTime())>0) return -1;
		}
		return 0;
	}
	

	@Override
	public String getLink() {
		return mLink;
	}

	@Override
	public void bindViewHolder(
			android.support.v7.widget.RecyclerView.ViewHolder holder, Activity activity) {
		if (holder instanceof ViewHolder) {
			ViewHolder myHolder = (ViewHolder) holder;
			
			if (mIsPlaceholder){
				myHolder.showPlaceholder();
			}
			if (!mIsPlaceholder){
				float density = activity.getResources().getDisplayMetrics().density;
				int width = activity.getResources().getDisplayMetrics().widthPixels;
				myHolder.showTweet();
				myHolder.setMessage(mMessage);
				myHolder.setImageUrl(mImageUrl, width);
				myHolder.setDate(mDate);
				myHolder.setAvatarUrl(mAvatarUrl, density);
			}			
		}
		
	}
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
		private TextView mMessageView;
		private TextView mDateView;
		private transient ImageView mImageView;
		private transient ImageView mAvatarView;
		private FrameLayout mPlaceholder;
		private RelativeLayout mTweet;
        private View mView;
        
        public ViewHolder(View view) {
            super(view);
            mMessageView = (TextView) view.findViewById(R.id.messageView);
            mDateView = (TextView) view.findViewById(R.id.dateView);
        	mImageView = (ImageView) view.findViewById(R.id.imageView);
        	mAvatarView= (ImageView) view.findViewById(R.id.avatarView);
        	mPlaceholder= (FrameLayout) view.findViewById(R.id.placeholder);
        	mTweet = (RelativeLayout) view.findViewById(R.id.tweet);
        }

        private void setMessage(String message) {
			mMessageView.setText(message);
		}
		
		private void setImageUrl(String imageUrl, int width) {
			// URL provided? -> load bitmap
	    	if (imageUrl != null){
	    		mImageView.setImageBitmap(null);
	    		BitmapUtility.loadBitmap(imageUrl, mImageView, width, width);
	    		mImageView.setVisibility(View.VISIBLE);
	    	// no picture -> hide image view	
	    	} else {
	    		mImageView.setImageBitmap(null);
	    		mImageView.setVisibility(View.GONE);
	    	}
		}
		
		private void setAvatarUrl(String avatarUrl, float density) {
		// URL provided? -> load bitmap
	    	if (avatarUrl != null){
	    		BitmapUtility.loadBitmap(avatarUrl, mAvatarView,(int) (40 * density));
	    	}
		}
		
		private void setDate(Date date) {
			mDateView.setText(Utility.getDisplayDate(date));
		}
		
		private void showPlaceholder(){
			mPlaceholder.setVisibility(View.VISIBLE);
			mTweet.setVisibility(View.GONE);
		}
		private void showTweet(){
			mPlaceholder.setVisibility(View.GONE);
			mTweet.setVisibility(View.VISIBLE);
		}
		
		private void setLink(final String link, final Activity activity) {
			mImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Utility.startBrowserActivity(activity, link);					
				}				
			});
		}
    }
}
