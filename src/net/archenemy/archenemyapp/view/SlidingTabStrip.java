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

package net.archenemy.archenemyapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Sliding tab indicator.
 */

class SlidingTabStrip extends LinearLayout {

  private static final int INDICATOR_HEIGHT_DIPS = 2;

  private final int indicatorHeight;
  private final Paint indicatorPaint;
  private int indicatorColor;

  private int selectedPosition;
  private float selectionOffset;

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

      if ((selectionOffset > 0F) && (selectedPosition < (getChildCount() - 1))) {
        View nextTab = getChildAt(selectedPosition + 1);
        left = (int) ((selectionOffset * nextTab.getLeft()) + ((1.0F - selectionOffset) * left));
        right = (int) ((selectionOffset * nextTab.getRight()) + ((1.0F - selectionOffset) * right));
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
