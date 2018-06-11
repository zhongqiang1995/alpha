package com.dy.alp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhong on 2018/2/27.
 */

public class TestBean implements Parcelable {
    String id;

    protected TestBean(Parcel in) {
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TestBean> CREATOR = new Creator<TestBean>() {
        @Override
        public TestBean createFromParcel(Parcel in) {
            return new TestBean(in);
        }

        @Override
        public TestBean[] newArray(int size) {
            return new TestBean[size];
        }
    };
}
