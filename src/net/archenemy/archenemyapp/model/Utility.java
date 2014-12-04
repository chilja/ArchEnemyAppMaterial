package net.archenemy.archenemyapp.model;

/**
 * <p>Collection of system functions</p>
 *
 * @author chiljagossow
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.format.DateUtils;
import android.widget.Toast;

import java.util.Date;

public class Utility {
  
  private Utility () {
    //prevent instantiation
  }
  
  /**
   * Formats date
   * @param date
   * @return formatted date string
   */
	public static String getDisplayDate(Date date) {
		return (String) DateUtils.getRelativeTimeSpanString(
				date.getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS);
	}

	/**
	 * Checks network connection
	 * @param activity Context for system service
	 * @param makeToast notify user of unavailability
	 * @return
	 */
	public static boolean isConnectedToNetwork(Activity activity, boolean makeToast){
		
  	final ConnectivityManager connMgr = (ConnectivityManager)
  	        activity.getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    if ((networkInfo != null) && networkInfo.isConnected()) {
    	return true;
    } else {
    	if (makeToast) {
    	  Toast.makeText(activity,"No network connection available", Toast.LENGTH_SHORT).show();
    	}
    	return false;
    }
	}

	/**
	 * Makes text share intent
	 * @param activity Calling activity
	 * @param message Message to be shared
	 * @param subject Subject of message
	 */
	public static void makeTextShareIntent(Activity activity, String message, String subject){
		final Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, message);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.setType("text/plain");
		activity.startActivity(Intent.createChooser(intent, "Share text to.."));;
	}
	
	/**
	 * Starts browser activity
	 * @param activity Calling activity
	 * @param uri uri to be opened in browser
	 */
	public static void startBrowserActivity(Activity activity, String uri) {
		if ((uri != null) && (uri != "")) {
			uri = uri.trim();
			final Intent intent = new Intent(Intent.ACTION_VIEW);
	    	intent.setData(Uri.parse(uri));
	    	activity.startActivity(intent);
		}
	}
}
