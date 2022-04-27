package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.service.knn.DijkstraService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * AhgKnn
 * 2022/4/15 zhoutao
 */
@Data
@Builder
@Slf4j
public class AhgKnn {
    // knn
    private PriorityQueue<Car> kCars;

    // queryName
    private Integer queryName;

    // current nearest name
    private Integer nearestName;

    // current search space
    private PriorityQueue<Integer> current;

    // Search threshold
    // (when the distance from the k-th moving object to the query point is less than searchLimit, the algorithm can be terminated in advance)
    private int searchLimit;

    // query dis
    public int[] queryArray;

    // temporary modfied clusters
    private AhgCluster tempInactiveCluster;

    // current nearest vertex
    private AhgVertex nearestVertex;

    private double queryTime;

    DijkstraService dijkstraService;

    /**
     * knn
     */
    public void knn() {

        dijkstraService = new DijkstraService();
        dijkstraService.Dijkstra(AhgVariable.vertices, queryName);

        long startKnn = System.nanoTime();
        while (true) {
            if (terminateEarly()) {
                break;
            }

            // expand search space
            if (nearestVertex.isVirtualMapBorderNode()) {
                for (Node node : nearestVertex.getVirtualLink()) {
                    setNode(node);
                }
            }

            // update current nearest node
            updateNearestName();
        }
        queryTime = (System.nanoTime() - startKnn) / 1000.0;

    }


    private void setNode(Node node) {
        int name = node.getName(), dis = node.getDis() + queryArray[nearestName];
        if (queryArray[name] == -1) {
            queryArray[name] = dis;
            current.add(name);
        } else if (queryArray[name] > dis) {
            current.remove(name);
            queryArray[name] = dis;
            current.add(name);
        }
    }


    private void updateNearestName() {
        nearestName = current.poll();
        nearestVertex = AhgVariable.vertices.get(nearestName);
    }

    private boolean terminateEarly() {
        if (queryArray[nearestName] >= searchLimit) {
            return true;
        }
        if (nearestVertex.isActive()) {
            int dis = queryArray[nearestName];
            for (Car car : nearestVertex.getCars()) {
                car.setQueryDis(dis);
                if (kCars.size() == GlobalVariable.K) {
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
            if (kCars.size() == GlobalVariable.K) {
                searchLimit = kCars.peek().getQueryDis();
            }
        }
        return false;
    }
}
