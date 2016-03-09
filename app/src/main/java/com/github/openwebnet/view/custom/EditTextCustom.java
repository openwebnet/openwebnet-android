package com.github.openwebnet.view.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.EditText;

import com.github.openwebnet.R;

public class EditTextCustom extends AppCompatEditText {

    public EditTextCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontViewHelper.initCustomFont(this, attrs,
            R.styleable.EditTextCustom, R.styleable.EditTextCustom_font);
    }
}
