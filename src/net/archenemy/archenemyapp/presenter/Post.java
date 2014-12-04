package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.BitmapUtility;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Utility;

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

import java.net.URISyntaxException;
import java.util.Date;

public class Post implements
	FeedElement{

	public static class ViewHolder extends RecyclerView.ViewHolder {
	  private static final String TAG = "PostViewHolder";
    private RelativeLayout post;
    private FrameLayout placeholder;
	  
	  private TextView messageView;
		private TextView teaserView;
		private TextView nameView;
		private TextView dateView;
		private ImageView imageView;
		private ImageView expandButton;
		private ImageView collapseButton;
		private ImageView avatarView;
		
    public ViewHolder(View view) {
      super(view);
      
      this.placeholder= (FrameLayout) view.findViewById(R.id.placeholder);
      this.post = (RelativeLayout) view.findViewById(R.id.post);

      this.messageView = (TextView) view.findViewById(R.id.messageView);
      this.teaserView = (TextView) view.findViewById(R.id.teaserView);
      this.nameView = (TextView) view.findViewById(R.id.nameView);
      this.dateView = (TextView) view.findViewById(R.id.dateView);
    	this.imageView = (ImageView) view.findViewById(R.id.imageView);
    	this.avatarView = (ImageView) view.findViewById(R.id.avatarView);

    	this.collapseButton = (ImageView) view.findViewById(R.id.collapseButton);
    	this.expandButton = (ImageView) view.findViewById(R.id.expandButton);
    	this.expandButton.setOnClickListener( new View.OnClickListener(){
    		@Override
        public void onClick(View view) {
    			ViewHolder.this.messageView.setVisibility(View.VISIBLE);
    			ViewHolder.this.collapseButton.setVisibility(View.VISIBLE);
    			ViewHolder.this.expandButton.setVisibility(View.INVISIBLE);
    		}
    	});
    	
    	this.collapseButton.setOnClickListener( new View.OnClickListener(){
    		@Override
        public void onClick(View view) {
    			ViewHolder.this.messageView.setVisibility(View.GONE);
    			ViewHolder.this.collapseButton.setVisibility(View.GONE);
    			ViewHolder.this.expandButton.setVisibility(View.VISIBLE);
    		}
    	});
    }

		public void setDate(Date date) {
			this.dateView.setText(Utility.getDisplayDate(date));
		}

		public void setImageUrl(String imageUrl, float density) {
			// URL provided? -> load bitmap
    	if (imageUrl != null){
    		this.imageView.setImageBitmap(null);
    		BitmapUtility.loadBitmap(
  				imageUrl, this.imageView,
  				(int) (72 * density),
  				(int) (72 * density));
    		this.imageView.setVisibility(View.VISIBLE);
    	
    	} else {
    	  // no picture -> hide image view
    		this.imageView.setVisibility(View.GONE);
    	}
		}

		public void setLink(final String link, final Activity activity) {
			this.imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Utility.startBrowserActivity(activity, link);
				}
			});
		}

		public void setMessage(String message) {
			int length = message.length();
			int maxTeaserLength = 20;
			if (length > maxTeaserLength) {
				this.teaserView.setText(message.substring(0, maxTeaserLength) + "...");
				this.messageView.setText(message);
				this.expandButton.setVisibility(View.VISIBLE);
			} else {
				this.teaserView.setText(message);
				this.messageView.setText(null);
				this.expandButton.setVisibility(View.INVISIBLE);
			}

		}

		public void setName(String name) {
			this.nameView.setText(name);
		}

		public void setProfileId(String profileId, float density) {
			String avatarUrl = null;
			try {
				avatarUrl = ImageRequest.getProfilePictureUrl(profileId, 100,  100).toString();
			} catch (URISyntaxException e) {
			  Log.e(TAG, "URL invalid");
			}

			// URL provided? -> load bitmap
    	if (avatarUrl != null){
    		BitmapUtility.loadBitmap(avatarUrl, this.avatarView, (int) (40 * density));
    	}
		}

		private void showPlaceholder(){
			this.placeholder.setVisibility(View.VISIBLE);
			this.post.setVisibility(View.GONE);
		}

		private void showPost(){
			this.placeholder.setVisibility(View.GONE);
			this.post.setVisibility(View.VISIBLE);
		}
  }
	
	private String imageUrl ;
	private String message;
	private Date date;
	private String link;
	private boolean isPlaceholder = false;
	private String name;
	protected String profileId;
	protected String avatarUrl;

	public Post(){
		//placeholder
		this.isPlaceholder = true;
	}

	public Post(Activity activity, String name, String id, String message, String createdAt, String imageUrl, String link, String avatarUrl) {
		this.imageUrl = imageUrl;
		this.date = FacebookAdapter.getDate(createdAt);
		this.link = link;
		this.message = message;
		this.name = name;
		this.profileId = id;
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
				myHolder.showPost();
				myHolder.setMessage(this.message);
				myHolder.setImageUrl(this.imageUrl, density);
				myHolder.setDate(this.date);
				myHolder.setName(this.name);
				myHolder.setProfileId(this.profileId, density);
				myHolder.setLink(this.link, activity);
			}
		}
	}

	@Override
	public int compareTo(FeedElement element) {
		if (element instanceof Post) {
			if ((this.date.getTime() - ((Post)element).date.getTime())<0) {
        return 1;
      }
			if ((this.date.getTime() - ((Post)element).date.getTime())>0) {
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
