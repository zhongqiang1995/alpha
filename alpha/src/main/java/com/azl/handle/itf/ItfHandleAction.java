package com.azl.handle.itf;

import com.azl.handle.bean.Staging;
import com.azl.handle.bean.ThreadMode;

/**
 * Created by zhong on 2017/5/23.
 */

public interface ItfHandleAction {


    void register(Staging staging);

    void unregister(Object object);

    void notifyMsg(Object... object);

    void notifyMsgMark(String mark, Object... object);

}
