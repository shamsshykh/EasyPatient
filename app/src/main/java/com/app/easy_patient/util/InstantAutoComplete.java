package com.app.easy_patient.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

public class InstantAutoComplete extends AppCompatAutoCompleteTextView {

    public InstantAutoComplete(Context context) {
        super(context);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    this.getWindowToken(), 0);
        }
        return false;
    }

    @Override
    public boolean performClick() {
        if (!getText().toString().isEmpty()) {
            getFilter().filter(null);
        }
        showDropDown();
        return super.performClick();
    }
}