package com.azl.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by zhong on 2017/7/7.
 */

public class StreamHelper {
    public static void close(Closeable... cls) {
        if (cls != null) {
            for (Closeable c : cls) {
                if (c == null) {
                    continue;
                }
                try {
                    c.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
