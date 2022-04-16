package com.index.indexforknn.base.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Comparator;

/**
 * TODO
 * 2022/2/16 zhoutao
 */
@Getter
@Setter
@Accessors(chain = true)
public class Car {
    // 移动对象名称
    private int name;

    // 到活跃点的距离
    private int activeDis;

    // 到查询点距离
    private int queryDis;

    // 活跃点
    private int active;

    /**
     * 对移动对象到兴趣点之间到距离进行排序
     */
    public static class ActiveDisComprator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            return Integer.compare(((Car) object1).activeDis, ((Car) object2).activeDis);
        }
    }

    /**
     * 对移动对象到查询点之间到距离进行排序
     */
    public static class QueryDisComprator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            return Integer.compare(((Car) object1).queryDis, ((Car) object2).queryDis);
        }
    }

}
