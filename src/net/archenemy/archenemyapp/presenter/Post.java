package net.archenemy.archenemyapp.presenter;

import java.net.URISyntaxException;
import java.util.Date;

import com.facebook.internal.ImageRequest;
import com.facebook.widget.ProfilePictureView;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.BitmapUtility;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Utility;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Post implements 
	FeedElement{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mImageUrl ;
	private String mMessage;
	private Date mDate;
	private String mLink;
	private boolean mIsPlaceholder = false;
	private String mName;
	protected String mProfileId;
	protected String mAvatarUrl;

	
	public Post(){
		//placeholder
		mIsPlaceholder = true;
	}

	public Post(Activity activity, String name, String id, String message, String createdAt, String imageUrl, String link, String avatarUrl) {
		mImageUrl = imageUrl;
		mDate = FacebookAdapter.getDate(createdAt);
		mLink = link;
		mMessage = message;
		mName = name;
		mProfileId = id;
		
	}
	
	@Override
	public int compareTo(FeedElement element) {
		if (element instanceof Post) {
			if ((mDate.getTime() - ((Post)element).mDate.getTime())<0) return 1;
			if ((mDate.getTime() - ((Post)element).mDate.getTime())>0) return -1;
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
				myHolder.showPost();
				myHolder.setMessage(mMessage);
				myHolder.setImageUrl(mImageUrl, density);
				myHolder.setDate(mDate);
				myHolder.setName(mName);
				myHolder.setProfileId(mProfileId, density);
				myHolder.setLink(mLink, activity);
			}
		}		
	}
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
		private TextView mMessageView;
		private TextView mTeaserView;
		private TextView mNameView;
		private TextView mDateView;
		private transient ImageView mImageView;
		private RelativeLayout mPost;
		private FrameLayout mPlaceholder;
		private ImageView mExpandButton;
		private ImageView mCollapseButton;
		protected transient ImageView mAvatarView;
		
        public View mView;
        public ViewHolder(View view) {
            super(view);
            mMessageView = (TextView) view.findViewById(R.id.messageView);
            mTeaserView = (TextView) view.findViewById(R.id.teaserView);
            mNameView = (TextView) view.findViewById(R.id.nameView);
            mDateView = (TextView) view.findViewById(R.id.dateView);
        	mImageView = (ImageView) view.findViewById(R.id.imageView);
        	mAvatarView = (ImageView) view.findViewById(R.id.avatarView);
        	
        	mPlaceholder= (FrameLayout) view.findViewById(R.id.placeholder);
        	mPost = (RelativeLayout) view.findViewById(R.id.post);
        	
        	mCollapseButton = (ImageView) view.findViewById(R.id.collapseButton);
        	mExpandButton = (ImageView) view.findViewById(R.id.expandButton);
        	mExpandButton.setOnClickListener( new View.OnClickListener(){
        		public void onClick(View view) {
        			mMessageView.setVisibility(View.VISIBLE);
        			mCollapseButton.setVisibility(View.VISIBLE);
        			mExpandButton.setVisibility(View.INVISIBLE);
        		}
        	});
        	mCollapseButton.setOnClickListener( new View.OnClickListener(){
        		public void onClick(View view) {
        			mMessageView.setVisibility(View.GONE);
        			mCollapseButton.setVisibility(View.GONE);
        			mExpandButton.setVisibility(View.VISIBLE);
        		}
        	});
        }

		public void setMessage(String message) {
			int length = message.length();
			int maxTeaserLength = 20;
			if (length > maxTeaserLength) {
				mTeaserView.setText(message.substring(0, maxTeaserLength) + "...");
				mMessageView.setText(message);
				mExpandButton.setVisibility(View.VISIBLE);
			} else {
				mTeaserView.setText(message);
				mMessageView.setText(null);
				mExpandButton.setVisibility(View.INVISIBLE);
			}

		}
		
		public void setName(String name) {			
			mNameView.setText(name);
		}
		
		public void setProfileId(String profileId, float density) {
			String avatarUrl = null;
			try {
				avatarUrl = ImageRequest.getProfilePictureUrl(profileId, 100,  100).toString();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// URL provided? -> load bitmap
	    	if (avatarUrl != null){
	    		BitmapUtility.loadBitmap(avatarUrl, mAvatarView, (int) (40 * density));
	    	}
		}
		
		public void setImageUrl(String imageUrl, float density) {
			// URL provided? -> load bitmap
	    	if (imageUrl != null){
	    		mImageView.setImageBitmap(null);
	    		BitmapUtility.loadBitmap(
	    				imageUrl, mImageView, 
	    				(int) (72 * density), 
	    				(int) (72 * density));
	    		mImageView.setVisibility(View.VISIBLE);
	    	// no picture -> hide image view	
	    	} else {
	    		mImageView.setVisibility(View.GONE);
	    	}
		}
		
		public void setDate(Date date) {
			mDateView.setText(Utility.getDisplayDate(date));
		}
		
		private void showPlaceholder(){
			mPlaceholder.setVisibility(View.VISIBLE);
			mPost.setVisibility(View.GONE);
		}
		
		private void showPost(){
			mPlaceholder.setVisibility(View.GONE);
			mPost.setVisibility(View.VISIBLE);
		}
		
		public void setLink(final String link, final Activity activity) {
			mImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Utility.startBrowserActivity(activity, link);					
				}				
			});
		}
    }
		
}
