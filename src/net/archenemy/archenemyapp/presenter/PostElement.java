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

import java.net.URISyntaxException;
import java.util.Date;

/**
 * Facebook Post
 * @author chiljagossow
 *
 */
public class PostElement implements
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

      placeholder= (FrameLayout) view.findViewById(R.id.placeholder);
      post = (RelativeLayout) view.findViewById(R.id.post);

      messageView = (TextView) view.findViewById(R.id.messageView);
      teaserView = (TextView) view.findViewById(R.id.teaserView);
      nameView = (TextView) view.findViewById(R.id.nameView);
      dateView = (TextView) view.findViewById(R.id.dateView);
    	imageView = (ImageView) view.findViewById(R.id.imageView);
    	avatarView = (ImageView) view.findViewById(R.id.avatarView);

    	collapseButton = (ImageView) view.findViewById(R.id.collapseButton);
    	expandButton = (ImageView) view.findViewById(R.id.expandButton);
    	expandButton.setOnClickListener( new View.OnClickListener(){
    		@Override
        public void onClick(View view) {
    			messageView.setVisibility(View.VISIBLE);
    			collapseButton.setVisibility(View.VISIBLE);
    			expandButton.setVisibility(View.INVISIBLE);
    		}
    	});

    	collapseButton.setOnClickListener( new View.OnClickListener(){
    		@Override
        public void onClick(View view) {
    			messageView.setVisibility(View.GONE);
    			collapseButton.setVisibility(View.GONE);
    			expandButton.setVisibility(View.VISIBLE);
    		}
    	});
    }

		public void setDate(Date date) {
			dateView.setText(Utility.getDisplayDate(date));
		}

		public void setImageUrl(String imageUrl, float density) {
			// URL provided? -> load bitmap
    	if (imageUrl != null){
    		imageView.setImageBitmap(null);
    		BitmapUtility.loadBitmap(
  				imageUrl, imageView,
  				(int) (72 * density),
  				(int) (72 * density));
    		imageView.setVisibility(View.VISIBLE);

    	} else {
    	  // no picture -> hide image view
    		imageView.setVisibility(View.GONE);
    	}
		}

		public void setLink(final String link, final Activity activity) {
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Utility.startBrowserActivity(activity, link);
				}
			});
		}

		public void setMessage(String message) {
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

		public void setName(String name) {
			nameView.setText(name);
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
    		BitmapUtility.loadBitmap(avatarUrl, avatarView, (int) (40 * density));
    	}
		}

		private void showPlaceholder(){
			placeholder.setVisibility(View.VISIBLE);
			post.setVisibility(View.GONE);
		}

		private void showPost(){
			placeholder.setVisibility(View.GONE);
			post.setVisibility(View.VISIBLE);
		}
  }

	private Post post;
	private boolean isPlaceholder = false;

	public PostElement(){
		//placeholder
		isPlaceholder = true;
	}

	public PostElement(Post post) {
		this.post = post;
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
				myHolder.showPost();
				myHolder.setMessage(post.getMessage());
				myHolder.setImageUrl(post.getImageUrl(), density);
				myHolder.setDate(post.getDate());
				myHolder.setName(post.getName());
				myHolder.setProfileId(post.getProfileId(), density);
				myHolder.setLink(post.getLink(), activity);
			}
		}
	}

	@Override
	public int compareTo(FeedElement element) {
		if (element instanceof PostElement) {
			if ((post.getDate().getTime() - ((PostElement)element).post.getDate().getTime())<0) {
        return 1;
      }
			if ((post.getDate().getTime() - ((PostElement)element).post.getDate().getTime())>0) {
        return -1;
      }
		}
		return 0;
	}

	@Override
	public String getLink() {
		return post.getLink();
	}
}
