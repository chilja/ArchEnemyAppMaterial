package net.archenemy.archenemyapp.presenter;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Basic class for fragments
 * @author chiljagossow
 *
 */
public abstract class BaseFragment extends Fragment {

	protected boolean isResumed = false;
	protected boolean isAttached = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		isAttached = true;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		isAttached = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		isResumed = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		isResumed = true;
	}

	protected void refresh() {}

	/**
	 * Return resource Id for icon fragment if present
	 * @return
	 */
	int getIconResId() {
		return 0;
	}

	abstract String getTAG();
}
