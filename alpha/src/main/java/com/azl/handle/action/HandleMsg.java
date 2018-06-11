package com.azl.handle.action;

import com.azl.handle.impl.HandleControlImpl;
import com.azl.handle.impl.HandleActionImpl;
import com.azl.handle.itf.ItfControl;

/**
 * Created by zhong on 2017/5/23.
 */

public class HandleMsg {
    private static ItfControl mItfControl;

    static {
        mItfControl = HandleControlImpl.build(HandleActionImpl.getInstance());
    }

    public static void bind(Object target) {
        mItfControl.bind(target);
    }

    public static void unbind(Object target) {
        mItfControl.unbind(target);
    }

    public static void handleMark(String mark, Object... obj) {
        mItfControl.handleMark(mark, obj);
    }

    public static void handle(Object... obj) {
        mItfControl.handle(obj);
    }
}
