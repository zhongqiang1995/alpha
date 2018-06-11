package com.azl.obs.ope.android.itf;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by zhong on 2017/5/17.
 */

public interface ItfBundleValue {

    void bind(Activity activity) ;

    void bind(Fragment fragment);

    void bind(android.app.Fragment fragment);
}
