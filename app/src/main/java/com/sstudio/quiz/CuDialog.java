package com.sstudio.quiz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Alan on 12/20/2017.
 */

public class CuDialog extends AlertDialog {
    protected CuDialog(@NonNull Context context) {
        super(context);
    }

    protected CuDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CuDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
    }

    @Override
    public void setMessage(CharSequence message) {
        super.setMessage(message);
    }

    @Override
    public void setView(View view) {
        super.setView(view);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

    @Override
    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        super.setButton(whichButton, text, listener);
    }
}
