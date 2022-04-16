package com.index.indexforknn.base.domain.annotation;

import com.index.indexforknn.base.domain.enumeration.TimeType;

import java.lang.annotation.*;

/**
 * TODO
 * 2022/2/16 zhoutao
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CostTime {
    String msg();

    TimeType timeType();
}
