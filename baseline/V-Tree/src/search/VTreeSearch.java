package search;

import common.Comprators;
import common.GlobalVariable;
import graph.Car;
import graph.Vertex;
import graph.Vnode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static common.GlobalVariable.*;

/**
 * @author: zhoutao
 * @since: 2021/11/20 10:52 下午
 * @description: TODO
 */
public class VTreeSearch {
    private List<Car> kCars = new ArrayList<>();
    private int queryName;
    private Vertex queryVertex;
    private int searchLimit;

    public VTreeSearch(int queryName) {
        kCars = new ArrayList<>(GlobalVariable.K);
        searchLimit = Integer.MAX_VALUE;
        queryVertex = allVertices[queryName];
        this.queryName = queryName;

        long startTime = System.nanoTime();
        knn();
        queryTime = (float) (System.nanoTime() - startTime) / 1000;
        System.out.println("查询时间=" + String.format("%.2f", queryTime / 1000) + "微秒");
        display();

    }

    private void knn() {
        Vnode gnav = allClusters.get(queryVertex.getClusterName(0)).getGNAV(queryName);
        Vertex u = allVertices[gnav.name];
        for (Car c : u.vertexCars) {
            c.queryDis = c.curDis + gnav.dis;
        }
        kCars.addAll(u.vertexCars);
        updateSearchLimit();

        while (gnav.dis < searchLimit) {
            gnav = nnav(gnav, u);
            u = allVertices[gnav.name];
            for (Car c : u.vertexCars) {
                c.queryDis = c.curDis + gnav.dis;
            }
            kCars.addAll(u.vertexCars);
            updateSearchLimit();
        }

    }

    private Vnode nnav(Vnode gnav, Vertex gnavVertex) {
        allClusters.get(gnavVertex.getClusterName(0)).deleteActive(gnav.name);
        return allClusters.get(queryVertex.getClusterName(0)).getGNAV(queryName);
    }

    private void updateSearchLimit() {
        if (kCars.size() >= K) {
            Collections.sort(kCars, new Comprators.QueryDisComprator());
            kCars.subList(0, K);
            searchLimit = kCars.get(K - 1).queryDis;
        }
    }


    private void display() {
        for (Car c : kCars) {
            System.out.println("CarName=" + c.carName + " dis=" + c.queryDis);
        }
    }
}
