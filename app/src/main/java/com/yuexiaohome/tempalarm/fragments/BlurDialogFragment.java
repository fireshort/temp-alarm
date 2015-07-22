package com.yuexiaohome.tempalarm.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.*;
import com.yuexiaohome.tempalarm.R;
import com.yuexiaohome.tempalarm.utils.ImageUtil;

public class BlurDialogFragment extends DialogFragment implements View.OnTouchListener,
        DialogInterface.OnKeyListener {

    private static final float OPT_SCALE = .3f;
    private static final float BLUR_RADIUS = 3.f;
    private static final int MASK_COLOR = 0x88000000;


    protected View getContainerForDismiss() {
        return null;
    }

    protected boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(this);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BitmapDrawable blurBg = buildBlurBackground();
        if (blurBg == null) {
            //L.e("Can't build blur background !");
        } else {
            getDialog().getWindow().setBackgroundDrawable(blurBg);
        }
        view.setOnTouchListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Rect rect = new Rect();
            ViewGroup root = (ViewGroup) v;
            View container = getContainerForDismiss();
            if (container != null) {
                container.getDrawingRect(rect);
                root.offsetDescendantRectToMyCoords(container, rect);
                try {
                    float x = event.getX();
                    float y = event.getY();
                    boolean hitOutside = !rect.contains((int) x, (int) y);
                    if (hitOutside) {
                        dismiss();
                    }
                } catch (Exception e) {
                    //L.e("motion event invalid", e);
                }
            }
        }
        return true;
    }


    private BitmapDrawable buildBlurBackground() {

        View decorView = getActivity().getWindow().getDecorView().getRootView();

        if (decorView == null || decorView.getWidth() == 0 || decorView.getHeight() == 0)
            return null;

        int viewW = decorView.getWidth();
        int viewH = decorView.getHeight();

        int outputW = (int) (viewW * OPT_SCALE);
        int outputH = (int) (viewH * OPT_SCALE);

        Bitmap raw = Bitmap.createBitmap(outputW, outputH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(raw);
        canvas.scale(OPT_SCALE, OPT_SCALE);
        decorView.draw(canvas);
        canvas.drawColor(MASK_COLOR);

        Bitmap blur = ImageUtil.stackBlur(raw,(int)BLUR_RADIUS);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), blur);
        raw.recycle();
        return drawable;
    }

    private int getWindowContentTop(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams param = getActivity().getWindow().getAttributes();
            if ((param.flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0) {
                return 0;
            }
        }

        Rect contentRect = new Rect();
        v.getWindowVisibleDisplayFrame(contentRect);
        return contentRect.top;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return event.getAction() == KeyEvent.ACTION_UP
                && keyCode == KeyEvent.KEYCODE_BACK
                && onBackPressed();
    }
}

