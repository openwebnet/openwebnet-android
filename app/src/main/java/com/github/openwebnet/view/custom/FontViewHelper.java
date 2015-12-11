package com.github.openwebnet.view.custom;

import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.TextView;

public class FontViewHelper {

    private FontViewHelper() {}

    private static final int DEFAULT_FONT = 1;

    private static final SparseArray<String> FONTS;
    static {
        FONTS = new SparseArray<>();
        FONTS.put(0, "fonts/fontawesome-webfont.ttf");
        FONTS.put(1, "fonts/DAGGERSQUARE-OBLIQUE.otf");
    }

    public static void initCustomFont(TextView view, AttributeSet attributeSet,
        @StyleableRes int[] attrs, int attrIndex) {

        TypedArray typedArray = view.getContext().getTheme()
            .obtainStyledAttributes(attributeSet, attrs, 0, 0);

        try {
            int fontIndex = typedArray.getInt(attrIndex, DEFAULT_FONT);
            String fontPath = FONTS.get(fontIndex);
            if (fontPath != null) {
                view.setTypeface(Typeface.createFromAsset(view.getContext().getAssets(), fontPath));
            } else {
                throw new IllegalArgumentException("invalid font path");
            }
        } finally {
            typedArray.recycle();
        }
    }
}
