package com.github.openwebnet.view.device;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewFontAwesome extends TextView {

    private static final String FONT_PATH = "fonts/fontawesome-webfont.ttf";

    public TextViewFontAwesome(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_PATH));
    }
}
