package com.azl.api;

import com.azl.obs.data.DataGet;
import com.azl.obs.data.HttpDataGet;
import com.azl.obs.retrofit.anno.Download;
import com.azl.obs.retrofit.anno.HandleMark;
import com.azl.obs.retrofit.anno.LocalPath;
import com.azl.obs.retrofit.anno.Tag;
import com.azl.obs.retrofit.anno.Upload;
import com.azl.obs.retrofit.anno.Url;

/**
 * Created by zhong on 2017/7/10.
 */

public interface AlphaApi {
    @Download
    DataGet download(@HandleMark String mark, @Url String url, @LocalPath String localPath, @Tag Object tag);

    @Upload
    DataGet upload(@HandleMark String mark, @Url String url, @LocalPath String localPath, @Tag Object tab);
}
