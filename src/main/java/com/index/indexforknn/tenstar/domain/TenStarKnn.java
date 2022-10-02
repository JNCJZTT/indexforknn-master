package com.index.indexforknn.tenstar.domain;

import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.knn.Knn;

import java.util.*;

/**
 * TODO
 * 2022/10/2 zhoutao
 */
public class TenStarKnn extends Knn {
    private TenStarVertex queryVertex;
    private List<Node> ancestorInfo;
    private Set<Integer> names = new HashSet<>();

    // query dis
    public int[] queryArray;

    public TenStarKnn(int queryName) {
        setQueryName(queryName);
        queryVertex = TenStarVariable.INSTANCE.getVertex(queryName);
        ancestorInfo = queryVertex.getAncestorInfo();
        queryArray = new int[GlobalVariable.VERTEX_NUM];
        Arrays.fill(queryArray, -1);
        this.kCars = new PriorityQueue<>(new Car.QueryDisComprator());
        queryArray[queryName] = 0;
    }


    public void knn() {
        long startKnn = System.nanoTime();

        for (Node vn : ancestorInfo) {
            TenStarVertex parent = TenStarVariable.INSTANCE.getVertex(vn.getName());

            for (Node active : parent.getKtnn()) {
                int dis = vn.getDis() + active.getDis();
                if (vn.getDis() != -1 && active.getDis() != -1 && (queryArray[active.getName()] == -1 || queryArray[active.getName()] > dis)) {
                    queryArray[active.getName()] = dis;
                    names.add(active.getName());
                }
            }
        }

        //System.out.println("添加KNN-------------------");
        for (Integer name : names) {
            TenStarVertex vertex = TenStarVariable.INSTANCE.getVertex(name);
            for (Car car : vertex.getCars()) {
                car.setQueryDis(queryArray[name]);
                if (kCars.size() == GlobalVariable.K) {
                    assert kCars.peek() != null;
                    if (kCars.peek().getQueryDis() > car.getQueryDis()) {
                        kCars.poll();
                        kCars.add(car);
                    } else {
                        break;
                    }
                } else {
                    kCars.add(car);
                }
            }
        }
        queryTime = (System.nanoTime() - startKnn) / 1000.0;
    }
}
