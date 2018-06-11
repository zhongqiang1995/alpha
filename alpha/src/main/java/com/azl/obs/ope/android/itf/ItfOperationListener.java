package com.azl.obs.ope.android.itf;

import android.view.View;

/**
 * Created by zhong on 2017/5/17.
 */

public interface ItfOperationListener {
    void bind(Object target);

    void bind(Object target,View contentView);
}
