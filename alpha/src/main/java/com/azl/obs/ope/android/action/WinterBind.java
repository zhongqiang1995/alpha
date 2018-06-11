package com.azl.obs.ope.android.action;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;

import com.azl.obs.ope.android.impl.OperationIdImpl;
import com.azl.obs.ope.android.impl.OperationListenerImpl;
import com.azl.obs.ope.android.itf.ItfOperationListener;
import com.azl.obs.ope.android.itf.ItfOperationId;

/**
 * Created by zhong on 2017/5/17.
 */

public class WinterBind {
    private static ItfOperationId itfOperation;
    private static ItfOperationListener itfListener;

    static {
        itfOperation = OperationIdImpl.getInstance();
        itfListener = OperationListenerImpl.getInstance();
    }


    public static void bind(Activity activity) {
        itfOperation.bind(activity);
        itfListener.bind(activity);
    }

    public static void bind(Fragment fragment, View contentView) {
        itfOperation.bind(fragment, contentView);
        itfListener.bind(fragment, contentView);
    }

    public static void bind(android.support.v4.app.Fragment fragment, View contentView) {
        itfOperation.bind(fragment, contentView);
        itfListener.bind(fragment, contentView);
    }
}
