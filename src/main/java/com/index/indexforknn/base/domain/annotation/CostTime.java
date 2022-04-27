package com.index.indexforknn.base.domain.annotation;

import com.index.indexforknn.base.domain.enumeration.UnitType;

import java.lang.annotation.*;

/**
 * CostTime
 * 2022/2/16 zhoutao
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CostTime {
    String msg();

    UnitType timeType();
}
