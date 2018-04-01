package com.github.openwebnet.view.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.openwebnet.R;

public class TextViewCustom extends AppCompatTextView {

    public TextViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontViewHelper.initCustomFont(this, attrs,
            R.styleable.TextViewCustom, R.styleable.TextViewCustom_fontCustom);
    }
}
