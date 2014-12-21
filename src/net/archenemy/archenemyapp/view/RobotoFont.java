package net.archenemy.archenemyapp.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom TextView using non-standard types of Roboto
 * 
 * @author chiljagossow
 * 
 */
public class RobotoFont extends TextView {

  public RobotoFont(Context context) {
    super(context);
  }

  public RobotoFont(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RobotoFont(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void setTypeface(Typeface tf, int style) {
    switch (style) {
      case Typeface.BOLD:
        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
            "fonts/Roboto-Black.ttf"));
        break;
      case Typeface.ITALIC:
        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
            "fonts/Roboto-LightItalic.ttf"));
        break;
      default:
        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
            "fonts/Roboto-Medium.ttf"));
    }
  }
}
