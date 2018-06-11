package com.azl.view.helper.itf;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by zhong on 2017/5/18.
 */

public abstract class ItfStatusView extends FrameLayout implements ItfStatusActionSwitch {

    public ItfStatusView(@NonNull Context context) {
        super(context);
    }

    public ItfStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

}
