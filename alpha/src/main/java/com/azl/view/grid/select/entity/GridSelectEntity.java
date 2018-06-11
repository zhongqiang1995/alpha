package com.azl.view.grid.select.entity;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by zhong on 2018/3/7.
 */

public class GridSelectEntity {

    public GridSelectEntity(String path) {

        try {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            this.contentType = fileNameMap.getContentTypeFor(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.path = path;
    }

    private String path;
    private String contentType;


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public boolean isImage() {
        if (contentType == null || !contentType.startsWith("image")) {
            return false;
        }
        return true;
    }
}
