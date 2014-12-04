package net.archenemy.archenemyapp.presenter;

import android.app.Activity;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

	protected boolean isResumed = false;
	protected boolean isAttached = false;

	public int getIconResId() {
		return 0;
	}

	public abstract String getTAG();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.isAttached = true;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.isAttached = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		this.isResumed = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.isResumed = true;
	}

	protected void refresh(){}
}
