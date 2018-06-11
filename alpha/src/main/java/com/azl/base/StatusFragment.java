package com.azl.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azl.handle.action.HandleMsg;
import com.azl.obs.ope.android.action.FetchBind;
import com.azl.obs.ope.android.action.WinterBind;
import com.azl.view.CommonView;
import com.azl.view.helper.itf.ItfStatusActionSwitch;

/**
 * Created by zhong on 2017/7/19.
 */

public abstract class StatusFragment extends Fragment {

    protected View mContentView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mContentView == null) {
            mContentView = inflater.inflate(getContentLayoutId(), null);
            if (mContentView == null) {
                throw new RuntimeException(" layout not found:" + getContentLayoutId());
            }
            FetchBind.bind(this);
            WinterBind.bind(this, mContentView);
            HandleMsg.bind(this);
            init();
        }
        return mContentView;
    }


    public View getContentView() {
        return mContentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HandleMsg.unbind(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected abstract void init();

    protected abstract int getContentLayoutId();


    public final <T> T findViewById(int id) {
        return (T) this.mContentView.findViewById(id);
    }


}
