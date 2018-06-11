package com.azl.bean;

/**
 * Created by zhong on 2017/9/21.
 */

public class PermissionPackageBean {
    public PermissionPackageBean(String[] arr) {
        setPermissions(arr);
    }

    public String[] permissions;

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public String[] getPermissions() {
        return permissions;
    }
}
