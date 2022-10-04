package Baseline.base.domain.annotation;

import Baseline.base.domain.enumeration.UnitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
