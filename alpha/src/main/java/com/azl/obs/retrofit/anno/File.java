package com.azl.obs.retrofit.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhong on 2017/6/15.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface File {
    String value() default "";

    ActionType type();

    String targetUrl() default "";

    enum ActionType {
        DOWNLOAD, UPLOAD
    }
}
