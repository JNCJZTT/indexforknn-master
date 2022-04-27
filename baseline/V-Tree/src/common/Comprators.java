package common;

import graph.Car;
import graph.Vnode;

import java.util.Comparator;

import static common.GlobalVariable.*;

public class Comprators {

    /*
     * 兴趣点对移动对象进行排序
     * */
    public static class CurDisComprator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            Car o1 = (Car) object1;
            Car o2 = (Car) object2;
            return Integer.compare(o1.curDis, o2.curDis);
        }
    }

    public static class VnodeComprator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            Vnode o1 = (Vnode) object1;
            Vnode o2 = (Vnode) object2;
            return Integer.compare(o1.dis, o2.dis);
        }
    }

    public static class QueryDisComprator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            Car p1 = (Car) object1;
            Car p2 = (Car) object2;
            return Integer.compare(p2.queryDis, p1.queryDis);
        }
    }

    public static class ValueDisComprator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            return Integer.compare(Value[(int) o1], Value[(int) o2]);
        }
    }
}
