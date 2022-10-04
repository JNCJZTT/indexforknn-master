package Baseline.base.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Comparator;
import java.util.Objects;

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

    public void addOffSetToQueryDis(int dis) {
        queryDis += dis;
    }


    public void setActiveInfo(Node node) {
        active = node.getName();
        activeDis = node.getDis();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Car)) {
            return false;
        }
        Car node = (Car) obj;
        return name == node.name;
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
