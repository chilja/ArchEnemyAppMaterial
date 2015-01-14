/**
 * Copyright 2014-present Chilja Gossow.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.archenemy.archenemyapp.presenter;/**

 * Collection of common functions
 * </p>
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

  /**
   * Formats date
   * 
   * @param date
   * @return formatted date string
   */
  public static String getDisplayDate(Date date) {
    return (String) DateUtils.getRelativeTimeSpanString(date.getTime(), new Date().getTime(),
        DateUtils.MINUTE_IN_MILLIS);
  }

  /**
   * Checks network connection
   * 
   * @param context
   *          Context for system service
   * @param makeToast
   *          notify user of unavailability
   * @return
   */
  public static boolean isConnectedToNetwork(Context context, boolean makeToast) {

    final ConnectivityManager connMgr = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    if ((networkInfo != null) && networkInfo.isConnected()) {
      return true;
    } else {
      if (makeToast) {
        Toast.makeText(context, "No network connection available", Toast.LENGTH_SHORT).show();
      }
      return false;
    }
  }

  /**
   * Makes text share intent
   * 
   * @param activity
   *          Calling activity
   * @param message
   *          Message to be shared
   * @param subject
   *          Subject of message
   */
  public static void makeTextShareIntent(Activity activity, String message, String subject) {
    final Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    intent.putExtra(Intent.EXTRA_TEXT, message);
    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    intent.setType("text/plain");
    activity.startActivity(Intent.createChooser(intent, "Share text to.."));
    ;
  }

  /**
   * Starts browser activity
   * 
   * @param activity
   *          Calling activity
   * @param uri
   *          uri to be opened in browser
   */
  public static void startBrowserActivity(Activity activity, String uri) {
    if ((uri != null) && (uri != "")) {
      uri = uri.trim();
      final Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(uri));
      activity.startActivity(intent);
    }
  }

  private Utility() {
    // prevent instantiation
  }
}
