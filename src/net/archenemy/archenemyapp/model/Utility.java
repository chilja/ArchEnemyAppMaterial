package net.archenemy.archenemyapp.model;

import java.io.FileOutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

public class Utility {
	
	private Utility () {
		//prevent instantiation
	}
	
	public static boolean isConnectedToNetwork(Activity activity, boolean makeToast){
		//check internet connection
    	ConnectivityManager connMgr = (ConnectivityManager) 
    	        activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        	return true;
        } else {
        	if (makeToast) 
        		Toast.makeText(activity,"No network connection available", Toast.LENGTH_SHORT).show();
        	return false;
        }
	}
	
	public static void startBrowserActivity(Activity activity, String uri) {
		if (uri != null && uri != "") {
			uri = uri.trim();
			Intent intent = new Intent(Intent.ACTION_VIEW);
	    	intent.setData(Uri.parse(uri));
	    	activity.startActivity(intent);
		}
	}
		
	public static void makeTextShareIntent(Activity activity, String message, String subject){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, message);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.setType("text/plain");
		activity.startActivity(Intent.createChooser(intent, "Share text to.."));;
	}
	
	public static boolean saveImageToInternalStorage(Activity activity, Bitmap image) {
		try {
		    // Use the compress method on the Bitmap object to write image to
		    // the OutputStream
		    FileOutputStream fos = activity.openFileOutput("desiredFilename.png", Context.MODE_PRIVATE);
	
		    // Writing the bitmap to the output stream
		    image.compress(Bitmap.CompressFormat.PNG, 100, fos);
		    fos.close();
	
		    return true;
	    } catch (Exception e) {
		    Log.e("saveToInternalStorage()", e.getMessage());
		    return false;
	    }
    }
	 
	public static String getDisplayDate(Date date) {
		return (String) DateUtils.getRelativeTimeSpanString(
				date.getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS);
	}
}
