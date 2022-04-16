package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.base.domain.Car;
import lombok.Builder;
import lombok.Data;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * TODO
 * 2022/4/15 zhoutao
 */
@Data
@Builder
public class AhgKnn {
    // knn结果列表
    private PriorityQueue<Car> kCars;

    // 当前最近结点
    private Integer nearestName;

    // 当前到搜索空间
    private PriorityQueue<Integer> current;

    // 搜索阈值（当第k辆移动对象到查询点到距离小于searchLimit时，即可提前结束算法）
    private int searchLimit;

    // 所有结点到查询点到距离
    public static int[] queryArray;

    /**
     * 对结点到查询点之间到距离进行排序
     */
    public static class QueryArrayDisComprator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            return Integer.compare(queryArray[(int) o1], queryArray[(int) o2]);
        }
    }
}
