package com.azl.activity.photo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.zhlib.R;

/**
 * Created by zhong on 2018/4/2.
 */

public class PhotoPreviewDialog extends Dialog implements View.OnClickListener {


    private View mContentView;
    private TextView mTvSave;
    private OnClickSaveListener mOnClickSaveListener;

    public PhotoPreviewDialog(@NonNull Context context) {
        this(context, R.style.AlphaDialogStyle);
    }

    public PhotoPreviewDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initContentView();
        setFullHorizontalWindows();
        setGravity();
        initView();
        initListener();
    }

    public void setOnClickSaveListener(OnClickSaveListener mOnClickSaveListener) {
        this.mOnClickSaveListener = mOnClickSaveListener;
    }

    public OnClickSaveListener getOnClickSaveListener() {
        return mOnClickSaveListener;
    }

    public void setGravity() {
        getWindow().setWindowAnimations(R.style.AlphaDialogAnimationBottomInTime200);
        WindowManager.LayoutParams arb = getWindow().getAttributes();
        arb.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(arb);
    }

    /**
     * 设置为横向全屏
     */
    public void setFullHorizontalWindows() {
        WindowManager.LayoutParams arb = getWindow().getAttributes();
        arb.width = ViewGroup.LayoutParams.MATCH_PARENT;
        arb.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(arb);

    }

    private void initContentView() {
        setContentView(R.layout.alpha_item_dialog_photo_preview);
    }

    private void initView() {
        mTvSave = findViewById(R.id.tvSave);
    }

    private void initListener() {
        mTvSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvSave) {
            if (mOnClickSaveListener != null) {
                mOnClickSaveListener.save();
            }
        }
    }

    public interface OnClickSaveListener {
        void save();
    }
}
