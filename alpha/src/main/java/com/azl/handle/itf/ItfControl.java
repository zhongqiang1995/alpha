package com.azl.handle.itf;

/**
 * Created by zhong on 2017/5/23.
 */

public interface ItfControl {
    void bind(Object target);

    void unbind(Object object);

    void handleMark(String mark, Object... objects);

    void handle(Object... object);

}
