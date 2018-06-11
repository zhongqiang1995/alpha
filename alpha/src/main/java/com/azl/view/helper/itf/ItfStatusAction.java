package com.azl.view.helper.itf;

/**
 * Created by zhong on 2017/5/22.
 */

public interface ItfStatusAction {
    /**
     * 显示异常
     */
    void showError();

    /**
     * 显示没有网络
     */
    void showNoNet();

    /**
     * 显示没有数据
     */
    void showNoData();

    /**
     * 显示loading
     */
    void showLoading();
}
