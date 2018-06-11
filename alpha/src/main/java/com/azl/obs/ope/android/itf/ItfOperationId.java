package com.azl.obs.ope.android.itf;

import android.app.Activity;
import android.view.View;

/**
 * Created by zhong on 2017/5/17.
 */

public interface ItfOperationId {
    void bind(Object object, View tartView);

    void bind(Activity activity);

}
