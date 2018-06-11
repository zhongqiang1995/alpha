package com.azl.obs.retrofit.helper;

import com.azl.obs.retrofit.itf.Construction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhong on 2017/5/16.
 */

public class ConstructionImpl implements Construction{

    private Build mBuild;

    private ConstructionImpl(Build build) {
        this.mBuild = build;
    }

    public <T> T create(Class<T> t) {
        return ProxyHelper.getProxy(t, mBuild);
    }


    public static class Build {
        private Map<String, String> mParams;
        private Map<String, String> mDefaultParams;


        public Map<String, String> getDefaultParams() {
            return mDefaultParams;
        }

        public Map<String, String> getParams() {
            return mParams;
        }

        public Build addGlobalParams(String key, String value) {
            if (mParams == null) {
                mParams = new HashMap<>();
            }
            mParams.put(key, value);
            return this;
        }

        public Build addDefaultParams(String key, String value) {
            if (mDefaultParams == null) {
                mDefaultParams = new HashMap<>();
            }
            mDefaultParams.put(key, value);
            return this;
        }

        public Construction build() {
            return new ConstructionImpl(this);
        }
    }

}
