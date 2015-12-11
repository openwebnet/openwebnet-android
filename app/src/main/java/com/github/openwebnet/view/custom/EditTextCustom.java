package com.github.openwebnet.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.github.openwebnet.R;

public class EditTextCustom extends EditText {

    public EditTextCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontViewHelper.initCustomFont(this, attrs,
            R.styleable.EditTextCustom, R.styleable.EditTextCustom_font);
    }
}
