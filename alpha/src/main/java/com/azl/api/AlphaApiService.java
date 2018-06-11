package com.azl.api;

import com.azl.obs.retrofit.helper.ConstructionImpl;
import com.azl.obs.retrofit.itf.Construction;

/**
 * Created by zhong on 2017/7/10.
 */

public class AlphaApiService {

    private static AlphaApi mApi;

    public static AlphaApi getInstance() {
        if (mApi == null) {
            synchronized (AlphaApiService.class) {
                if (mApi == null) {
                    Construction construction = new ConstructionImpl
                            .Build()
                            .build();
                    mApi = construction.create(AlphaApi.class);
                }
            }
        }
        return mApi;
    }

}
