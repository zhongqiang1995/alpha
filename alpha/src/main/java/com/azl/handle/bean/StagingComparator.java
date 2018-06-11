package com.azl.handle.bean;

import java.util.Comparator;

/**
 * Created by zhong on 2017/5/23.
 */

public class StagingComparator implements Comparator<Staging.StagingMethod> {

    @Override
    public int compare(Staging.StagingMethod o1, Staging.StagingMethod o2) {
        if (o1.getPriority() < o2.getPriority()) {
            return 1;
        } else if (o1.getPriority() == o2.getPriority()) {
            return 0;
        } else {
            return -1;
        }
    }
}
