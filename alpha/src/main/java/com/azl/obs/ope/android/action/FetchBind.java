package com.azl.obs.ope.android.action;

import android.app.Activity;
import android.app.Fragment;

import com.azl.obs.ope.android.impl.OperationBundleImpl;
import com.azl.obs.ope.android.itf.ItfBundleValue;

/**
 * Created by zhong on 2017/5/17.
 */

public class FetchBind {
    private static ItfBundleValue fetch;

    static {
        fetch = OperationBundleImpl.getInstance();
    }

    public static void bind(Activity activity) {
        fetch.bind(activity);
    }

    public static void bind(Fragment fragment) {
        fetch.bind(fragment);
    }

    public static void bind(android.support.v4.app.Fragment fragment) {
        fetch.bind(fragment);
    }
}
