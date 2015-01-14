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
