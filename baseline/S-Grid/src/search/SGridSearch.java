package search;

import common.Comprators;
import common.Utils;
import graph.Car;
import graph.Vertex;
import graph.Vnode;

import java.util.PriorityQueue;

import static common.GlobalVariable.*;

/**
 * @author: zhoutao
 * @since: 2021/11/14 3:07 下午
 * @description: TODO
 */
public class SGridSearch {
    public PriorityQueue<Car> kCars;

    private PriorityQueue<Integer> current;

    private int nearestName;

    private int searchLimit;

    public SGridSearch(int queryName) {

        kCars = new PriorityQueue<>(new Comprators.QueryDisComprator());
        current = new PriorityQueue<>(new Comprators.ValueDisComprator());
        searchLimit = Integer.MAX_VALUE;

        nearestName = queryName;
        Value[queryName] = 0;
        queryTime = System.nanoTime();
        sgridSearch();
        queryTime = (float) (System.nanoTime() - queryTime) / 1000;

        Utils.resetValue();

//        display();
    }

    private void sgridSearch() {

        while (true) {

            Vertex v = allVertices.get(nearestName);
            if (isBreak(v)) {
                break;
            }

            for (Vnode vn : v.Link) {
                int name = vn.name, dis = vn.dis + Value[nearestName];
                if (Value[name] == -1) {
                    Value[name] = dis;
                    current.add(name);
                } else if (Value[name] > dis) {
                    //修改了距离
                    Value[name] = dis;
                    current.remove(name);
                    current.add(name);
                }
            }

            nearestName = current.poll();

        }
    }

    private boolean isBreak(Vertex v) {
        if (Value[nearestName] > searchLimit) {
            //如果最大搜索区域小于Limit,
            return true;
        }
        if (v.isActive) {
            int dis = Value[nearestName];
            for (Car c : v.vertexCars) {
                c.queryDis = c.curDis + dis;
            }
            kCars.addAll(v.vertexCars);
            while (kCars.size() > K) {
                kCars.poll();
            }
            if (kCars.size() == K) {
                assert kCars.peek() != null;
                searchLimit = kCars.peek().queryDis;
            }
        }
        return false;
    }


    private void display() {
        while (!kCars.isEmpty()) {
            Car c = kCars.poll();
            System.out.println("CarName=" + c.carName + " dis=" + c.queryDis);
        }
    }

}

