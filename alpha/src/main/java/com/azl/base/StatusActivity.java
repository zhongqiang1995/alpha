package com.azl.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.azl.bean.PermissionPackageBean;
import com.azl.handle.action.HandleMsg;
import com.azl.handle.anno.Intercept;
import com.azl.handle.anno.Mark;
import com.azl.handle.anno.SelectThread;
import com.azl.handle.bean.ThreadMode;
import com.azl.helper.AKXMarkList;
import com.azl.obs.ope.android.action.FetchBind;
import com.azl.obs.ope.android.action.WinterBind;
import com.azl.view.CommonView;
import com.azl.view.helper.itf.ItfStatusActionSwitch;

/**
 * Created by zhong on 2017/5/22.
 */

public class StatusActivity extends PermissionsActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FetchBind.bind(this);
        HandleMsg.bind(this);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        WinterBind.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HandleMsg.unbind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }


    @Mark(AKXMarkList.MARK_PERMISSIONS)
    @Intercept(true)
    @SelectThread(ThreadMode.MAIN_THREAD)
    public void $permissions$(PermissionPackageBean arr) {
        requestPermission(arr.getPermissions(), 1);
    }

    @Override
    protected void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        if (requestCode == 1) {
            //获取读写权限成功
            HandleMsg.handleMark(AKXMarkList.MARK_PERMISSIONS_SD);
        }
    }
}
