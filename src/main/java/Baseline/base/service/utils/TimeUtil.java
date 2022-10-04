package Baseline.base.service.utils;

import Baseline.base.domain.enumeration.UnitType;

/**
 * TimeUtil
 * 2022/2/16 zhoutao
 */
public class TimeUtil {

    private final static String SUFFIX = "=";

    public static String formatTime(String msg, UnitType timeType, long startTime) {
        return msg + SUFFIX + String.format("%.2f",
                (((float) (System.nanoTime() - startTime)) / timeType.getQuantity())
        ) + timeType.getName();
    }
}
