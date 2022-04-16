package com.index.indexforknn.base.service.utils;

import com.index.indexforknn.base.domain.enumeration.TimeType;

/**
 * TODO
 * 2022/2/16 zhoutao
 */
public class TimeUtil {

    private final static String SUFFIX = "=";

    public static String formatTime(String msg, TimeType timeType, long startTime) {
        return msg + SUFFIX + String.format("%.2f",
                (((float) (System.nanoTime() - startTime)) / timeType.getQuantity())
        ) + timeType.getName();
    }
}
