package com.index.indexforknn.vtree.domain;

import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.knn.Knn;

import java.util.Collections;
import java.util.PriorityQueue;

/**
 * TODO
 * 2022/9/16 zhoutao
 */
public class VtreeKnn extends Knn {
    VtreeVertex queryVertex;
    private int searchLimit;

    public VtreeKnn(int queryName) {
        this.queryName = queryName;
        this.kCars = new PriorityQueue<>(new Car.QueryDisComprator());
        this.queryVertex = VtreeVariable.INSTANCE.getVertex(queryName);
        searchLimit = Integer.MAX_VALUE;


    }

    public void knn() {
        long startKnn = System.nanoTime();
        Node gnav = VtreeVariable.INSTANCE.getCluster(queryVertex.getClusterName()).getGNAV(queryName);
        VtreeVertex u = VtreeVariable.INSTANCE.getVertex(gnav.getName());
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

    private Node nnav(Node gnav, VtreeVertex gnavVertex) {
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
