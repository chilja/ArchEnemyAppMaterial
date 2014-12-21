package net.archenemy.archenemyapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Tab indicator that slides
 * 
 */

class SlidingTabStrip extends LinearLayout {

  private static final int INDICATOR_HEIGHT_DIPS = 2;

  private final int indicatorHeight;
  private final Paint indicatorPaint;

  private int selectedPosition;
  private float selectionOffset;

  private int indicatorColor;

  public SlidingTabStrip(Context context) {
    this(context, null);
  }

  public SlidingTabStrip(Context context, AttributeSet attrs) {
    super(context, attrs);
    setWillNotDraw(false);

    final float density = getResources().getDisplayMetrics().density;

    indicatorHeight = (int) (INDICATOR_HEIGHT_DIPS * density);
    indicatorPaint = new Paint();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    final int height = getHeight();
    final int childCount = getChildCount();

    if (childCount > 0) {
      View selectedTab = getChildAt(selectedPosition);
      int left = selectedTab.getLeft();
      int right = selectedTab.getRight();

      if ((selectionOffset > 0f) && (selectedPosition < (getChildCount() - 1))) {

        // Draw the indicator
        View nextTab = getChildAt(selectedPosition + 1);
        left = (int) ((selectionOffset * nextTab.getLeft()) + ((1.0f - selectionOffset) * left));
        right = (int) ((selectionOffset * nextTab.getRight()) + ((1.0f - selectionOffset) * right));
      }

      canvas.drawRect(left, height - indicatorHeight, right, height, indicatorPaint);
    }

  }

  void onViewPagerPageChanged(int position, float positionOffset) {
    selectedPosition = position;
    selectionOffset = positionOffset;
    invalidate();
  }

  void setIndicatorColor(int color) {
    indicatorColor = color;
    indicatorPaint.setColor(indicatorColor);
    invalidate();
  }
}
