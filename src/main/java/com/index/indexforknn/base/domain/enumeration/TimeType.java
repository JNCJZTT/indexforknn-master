package com.index.indexforknn.base.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 * 2022/2/16 zhoutao
 */
@AllArgsConstructor
@Getter
public enum TimeType {
    Second(1000_000_000, "秒"),
    MilliSecond(1000_000, "毫秒"),
    microseconds(1000, "微秒");

    private int quantity;

    private String name;
}
