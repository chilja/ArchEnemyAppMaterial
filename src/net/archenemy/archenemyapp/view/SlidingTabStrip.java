package net.archenemy.archenemyapp.view;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
 
class SlidingTabStrip extends LinearLayout {

    private static final int SELECTED_INDICATOR_THICKNESS_DIPS = 2;

    private final int mSelectedIndicatorThickness;
    private final Paint mSelectedIndicatorPaint;
 
    private int mSelectedPosition;
    private float mSelectionOffset;

    private int mIndicatorColor;
 
    SlidingTabStrip(Context context) {
        this(context, null);
    }
 
    SlidingTabStrip(Context context,AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
 
        final float density = getResources().getDisplayMetrics().density;
 
        mSelectedIndicatorThickness = (int) (SELECTED_INDICATOR_THICKNESS_DIPS * density);
        mSelectedIndicatorPaint = new Paint();
    }

    void setIndicatorColor(int color) {
        mIndicatorColor = color;
        mSelectedIndicatorPaint.setColor(mIndicatorColor);
        invalidate();
    }
 
    void onViewPagerPageChanged(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        invalidate();
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight();
        final int childCount = getChildCount();

        if (childCount > 0) {
            View selectedTitle = getChildAt(mSelectedPosition);
            int left = selectedTitle.getLeft();
            int right = selectedTitle.getRight();
 
            if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {
 
                // Draw the selection partway between the tabs
                View nextTab = getChildAt(mSelectedPosition + 1);
                left = (int) (mSelectionOffset * nextTab.getLeft() +
                        (1.0f - mSelectionOffset) * left);
                right = (int) (mSelectionOffset * nextTab.getRight() +
                        (1.0f - mSelectionOffset) * right);
            }
 
            canvas.drawRect(left, height - mSelectedIndicatorThickness, right,
                    height, mSelectedIndicatorPaint);
        }
 
    }
}
