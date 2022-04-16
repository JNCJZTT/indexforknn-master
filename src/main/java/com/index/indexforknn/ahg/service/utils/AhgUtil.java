package com.index.indexforknn.ahg.service.utils;

import com.index.indexforknn.ahg.common.AhgConstants;

/**
 * 一些通用的工具类
 * 2022/2/22 zhoutao
 */
public class AhgUtil {

    // 获得簇的层数
    public static int getClusterLevel(String clusterName) {
        return clusterName.split(AhgConstants.CLUSTER_NAME_SUFFIX).length - 1;
    }

    /**
     * 获得簇名父亲到簇名
     */
    public static String getParentClusterName(String name) {
        int lastIndex = name.lastIndexOf(AhgConstants.CLUSTER_NAME_SUFFIX);
        if (lastIndex == -1) {
            return null;
        }
        return name.substring(0, lastIndex);
    }


}
