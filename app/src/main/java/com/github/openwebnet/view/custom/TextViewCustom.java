package com.github.openwebnet.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.openwebnet.R;

public class TextViewCustom extends TextView {

    public TextViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontViewHelper.initCustomFont(this, attrs,
            R.styleable.TextViewCustom, R.styleable.TextViewCustom_font);
    }
}
