package Baseline.VTree.domain;

import Baseline.base.domain.Car;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import Baseline.base.domain.api.knn.Knn;

import java.util.PriorityQueue;

/**
 * TODO
 * 2022/9/16 zhoutao
 */
public class VTreekNN extends Knn {
    VTreeVertex queryVertex;
    private int searchLimit;

    public VTreekNN(int queryName) {
        this.queryName = queryName;
        this.kCars = new PriorityQueue<>(new Car.QueryDisComprator());
        this.queryVertex = VtreeVariable.INSTANCE.getVertex(queryName);
        searchLimit = Integer.MAX_VALUE;


    }

    public void knn() {
        long startKnn = System.nanoTime();
        Node gnav = VtreeVariable.INSTANCE.getCluster(queryVertex.getClusterName()).getGNAV(queryName);
        VTreeVertex u = VtreeVariable.INSTANCE.getVertex(gnav.getName());
        for (Car c : u.cars) {
            c.setQueryDis(gnav.getDis());
        }
        kCars.addAll(u.cars);
        updateSearchLimit();

        while (gnav.getDis() < searchLimit) {
           gnav = nnav(gnav, u);
            u = VtreeVariable.INSTANCE.getVertex(gnav.getName());
            for (Car c : u.cars) {
                c.setQueryDis(gnav.getDis());
            }
            kCars.addAll(u.cars);
            updateSearchLimit();
        }
        queryTime = (System.nanoTime() - startKnn) / 1000.0;
    }

    private Node nnav(Node gnav, VTreeVertex gnavVertex) {
        VtreeVariable.INSTANCE.getCluster(gnavVertex.getClusterName()).deleteActive(gnav.getName());
        return VtreeVariable.INSTANCE.getCluster(queryVertex.getClusterName()).getGNAV(queryName);
    }

    private void updateSearchLimit() {
        while (kCars.size() > GlobalVariable.K) {
            kCars.poll();
        }
        if (kCars.size() == GlobalVariable.K) {
            searchLimit = kCars.peek().getQueryDis();
        }
    }
}
