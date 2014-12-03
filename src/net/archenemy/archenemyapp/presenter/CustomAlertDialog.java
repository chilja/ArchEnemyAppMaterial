package net.archenemy.archenemyapp.presenter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

public class CustomAlertDialog extends AlertDialog {

	public CustomAlertDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomAlertDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public CustomAlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	

}
