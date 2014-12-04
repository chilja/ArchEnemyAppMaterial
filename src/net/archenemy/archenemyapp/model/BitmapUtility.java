package net.archenemy.archenemyapp.model;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * <p>General helper class for loading, caching and formatting bitmaps</p>
 *
 * @author chiljagossow
 */

public class BitmapUtility {

  /**
   * AsyncTask to retrieve images from resources
   */
  private static class BitmapFromResourcesTask extends AsyncTask<Integer, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private int resId = 0;
    private final int reqWidth;
    private final int reqHeight;
    private final Activity activity;

		private BitmapFromResourcesTask(
		    Activity activity, ImageView imageView, int reqWidth, int reqHeight) {
      this.imageViewReference = new WeakReference<ImageView>(imageView);
      this.reqWidth = reqWidth;
			this.reqHeight = reqHeight;
			this.activity = activity;
			tasks.add(this);
		}

    @Override
    protected Bitmap doInBackground(Integer... params) {
      this.resId = params[0];
      return decodeSampledBitmapFromResource(
          this.activity.getResources(), this.resId, this.reqWidth, this.reqHeight);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      if ((this.imageViewReference != null) && (bitmap != null)) {
        final ImageView imageView = this.imageViewReference.get();
        if (imageView != null) {
          imageView.setImageBitmap(bitmap);
        }
      }
      tasks.remove(this);
    }
	}

	/**
	 * AsyncTask to retrieve images from URL
	 */
	private static class BitmapFromUrlTask extends AsyncTask<String, Void, Bitmap> {

		private final WeakReference<ImageView> imageViewReference;
		private final int reqWidth;
		private final int reqHeight;
		private URL url;
		private final boolean round;

		private BitmapFromUrlTask(ImageView imageView, int radius) {
			this.imageViewReference = new WeakReference<ImageView>(imageView);
			this.reqWidth = radius;
			this.reqHeight = radius;
			this.round = true;
			tasks.add(this);
		}

		private BitmapFromUrlTask(ImageView imageView, int reqWidth, int reqHeight) {
			this.imageViewReference = new WeakReference<ImageView>(imageView);
			this.reqWidth = reqWidth;
			this.reqHeight = reqHeight;
			this.round = false;
			tasks.add(this);
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			if ((urls != null) && (urls.length > 0)) {
				try {
					this.url = new URL(urls[0].trim());
					return decodeSampledBitmapFromUrl(this.url, this.reqWidth, this.reqHeight);
				} catch(final Exception ex) {
				  Log.e(TAG, "Could not load bitmap from" + this.url );
					return null;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if ((this.imageViewReference != null) && (bitmap != null)) {
        final ImageView imageView = this.imageViewReference.get();
        if (imageView != null) {
        	if (this.round) {
        		imageView.setImageBitmap(getCircleBitmap(bitmap, this.reqWidth));
        	} else {
            imageView.setImageBitmap(bitmap);
        	}
        }
      }
			addBitmapToMemoryCache(this.url.toString(), bitmap);
			tasks.remove(this);
		}
	}

	private static final String TAG = "BitmapUtility";

	private static ArrayList<AsyncTask> tasks = new ArrayList<AsyncTask>();

	private static LruCache<String, Bitmap> memoryCache ;

	static {
	  
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 8;

    memoryCache = new LruCache<String, Bitmap>(cacheSize) {
      @Override
      protected int sizeOf(String key, Bitmap bitmap) {
          return bitmap.getByteCount() / 1024;
      }
    };
	}

	/**
  * Transforms a bitmap into a circle shape.
  * @param bitmap
  * @param pixels
  * @return bitmap as circle shape
  */
	public static Bitmap getCircleBitmap(Bitmap bitmap, int diameterPixels) {
    final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
        bitmap.getHeight(), Config.ARGB_8888);
    final Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    final RectF rectF = new RectF(rect);
    final float roundPx = diameterPixels;

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    return output;
	}
	
	/**
	 * Loads bitmap from resources.
	 * @param activity activity to access the recourses
	 * @param resId resouce id of the bitmap to beloaded
	 * @param imageView imageView the bitmap will be attached to
	 * @param reqWidth width in pixels
	 * @param reqHeight height in pixels
	 */
	public static void loadBitmap(
		Activity activity, int resId, ImageView imageView, int reqWidth, int reqHeight) {

    final BitmapFromResourcesTask task =
    	new BitmapFromResourcesTask(activity, imageView, reqWidth, reqHeight);

    task.execute(resId);
	}

	/**
	 * Loads bitmap from URL and transforms the shape into a circle.
	 * @param bitmapUrl URL of the bitmap
	 * @param imageView imageView the bitmap will be attached to
	 * @param diameter diameter in pixels of the resulting circle
	 */
	public static void loadBitmap(String bitmapUrl, ImageView imageView, int diameter) {
		if ((imageView != null) && (bitmapUrl != null)) {
			//check cache
			final Bitmap bitmap = getBitmapFromMemCache(bitmapUrl.toString());
			if (bitmap != null) {
					imageView.setImageBitmap(getCircleBitmap(bitmap, diameter));
				return;
			}
			final BitmapFromUrlTask task = new BitmapFromUrlTask(imageView, diameter);
			task.execute(bitmapUrl);
		}
	}

	/**
	 * Loads bitmap from URL.
	 * @param bitmapUrl URL of the bitmap
	 * @param imageView imageView the bitmap will be attached to
	 * @param reqWidth width in pixels
	 * @param reqHeight height in pixels
	 */
	public static void loadBitmap(
			String bitmapUrl, ImageView imageView, int reqWidth, int reqHeight) {
		if ((imageView != null) && (bitmapUrl != null)) {
			//check cache first
			final Bitmap bitmap = getBitmapFromMemCache(bitmapUrl.toString());
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
				return;
			}
			final BitmapFromUrlTask task = new BitmapFromUrlTask(imageView, reqWidth, reqHeight);
			task.execute(bitmapUrl);
		}
	}
	
	/**
	 * Cancels all background tasks that haven't completed. Should be called from activity's onDestroy() method.
	 */
	public static void onDestroy() {
		if (!tasks.isEmpty()) {
			for (final AsyncTask task:tasks) {
				task.cancel(true);
			}
			tasks.clear();
		}
		Log.i(TAG, "all async tasks cancelled");
	}

	private static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
    if (getBitmapFromMemCache(key) == null) {
        memoryCache.put(key, bitmap);
    }
	}

	private static int calculateInSampleSize(
      BitmapFactory.Options options, int reqWidth, int reqHeight) {

    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if ((height > reqHeight) || (width > reqWidth)) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while (((halfHeight / inSampleSize) > reqHeight)
          && ((halfWidth / inSampleSize) > reqWidth)) {
        inSampleSize *= 2;
      }
    }
    return inSampleSize;
	}

	private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	    int reqWidth, int reqHeight) {

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res, resId, options);

    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeResource(res, resId, options);
	}

	private static Bitmap decodeSampledBitmapFromUrl(URL url, int reqWidth, int reqHeight)
			throws IOException {
		URLConnection connection = url.openConnection();
		InputStream inputStream = new BufferedInputStream(connection.getInputStream());

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(inputStream, null, options);
    inputStream.close();

    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    options.inJustDecodeBounds = false;
    connection = url.openConnection();
    inputStream = new BufferedInputStream(connection.getInputStream());
    return BitmapFactory.decodeStream(connection.getInputStream(), null, options);
	}

	private static Bitmap getBitmapFromMemCache(String key) {
	    return memoryCache.get(key);
	}
}
