package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;

import android.app.Activity;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

	protected boolean mIsResumed = false;
	protected boolean mIsAttached = false;
	protected float mDensity; 
	
	public abstract String getTAG();
	
	protected void refresh(){	
	}
	
	public int getIconResId() {
		return 0;
	}

	@Override
	public void onPause() {
		super.onPause();
		mIsResumed = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		mIsResumed = true;
	}	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mIsAttached = true;
        mDensity = getResources().getDisplayMetrics().density;
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		mIsAttached = false;
	}
}
