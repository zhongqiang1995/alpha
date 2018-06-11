package com.azl.view.helper.itf;

import android.view.View;

/**
 * Created by zhong on 2017/5/19.
 */

public interface ItfStatusActionSwitch extends ItfStatusAction {
    void replaceErrorView(View view);

    void replaceLoadingView(View view);

    void replaceNoDataView(View view);

    void replaceNoNetView(View view);
}
