package com.index.indexforknn.base.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Comparator;

/**
 * Moving objects
 * 2022/2/16 zhoutao
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Car {
    // Car name
    private int name;

    /**
     * the dis from car to active node
     */
    private int activeDis;

    /**
     * the dis from car to query node
     */
    private int queryDis;

    /**
     * active name
     */
    private int active;

    public void setQueryDis(int dis) {
        queryDis = dis + activeDis;
    }

    public void setActiveInfo(Node node) {
        active = node.getName();
        activeDis = node.getDis();
    }

    /**
     * Comprator based on activeDis
     */
    public static class ActiveDisComprator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            return Integer.compare(((Car) object1).activeDis, ((Car) object2).activeDis);
        }
    }

    /**
     * Comprator based on queryDis
     */
    public static class QueryDisComprator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            return Integer.compare(((Car) object2).queryDis, ((Car) object1).queryDis);
        }
    }

}
