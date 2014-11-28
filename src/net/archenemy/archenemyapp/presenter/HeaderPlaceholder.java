package net.archenemy.archenemyapp.presenter;

import java.util.Date;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.BitmapUtility;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HeaderPlaceholder 
implements 
FeedElement {

	@Override
	public int compareTo(FeedElement another) {
		//sort as fist Element
		return 1;
	}

	@Override
	public String getLink() {
		return null;
	}

	@Override
	public void bindViewHolder(ViewHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	public static class MyViewHolder extends RecyclerView.ViewHolder {

		private transient ImageView mImageView;
		
        public View mView;
        public MyViewHolder(View view) {
            super(view);
        	mImageView = (ImageView) view.findViewById(R.id.imageView);
        }
        
        public void setImageRes(Activity activity, int resId) {
			// URL provided? -> load bitmap
	    	if (resId != 0){
	    		BitmapUtility.loadBitmap(activity, resId, mImageView, mImageView.getWidth(), mImageView.getHeight());	
	    	} 
		}
    }
}
