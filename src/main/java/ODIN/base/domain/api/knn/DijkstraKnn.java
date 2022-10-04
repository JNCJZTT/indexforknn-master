package ODIN.base.domain.api.knn;

import ODIN.base.domain.Car;
import ODIN.base.domain.GlobalVariable;
import ODIN.base.domain.Node;
import ODIN.base.domain.api.Vertex;
import lombok.Data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * DijkstraKnn
 * 2022/5/15 zhoutao
 */
@Data
public abstract class DijkstraKnn<T extends Vertex> extends Knn {

    // query dis
    public int[] queryArray;

    // current search space
    protected PriorityQueue<Integer> current;

    // current nearest name
    protected Integer nearestName;

    // Search threshold
    // (when the distance from the k-th moving object to the query point is less than searchLimit, the algorithm can be terminated in advance)
    protected int searchLimit;

    // current nearest vertex
    protected T nearestVertex;

    public abstract void knn();

    protected abstract void updateNearestName();

    public DijkstraKnn(int queryName,int vertexNum) {
        this.queryName = queryName;
        this.nearestName = queryName;
        this.kCars = new PriorityQueue<>(new Car.QueryDisComprator());
        this.queryArray = new int[vertexNum];
        this.current = new PriorityQueue<>(Comparator.comparingInt(o -> queryArray[o]));
        this.searchLimit = Integer.MAX_VALUE;
        Arrays.fill(queryArray, -1);
        queryArray[queryName] = 0;
    }

    protected void setNode(Node node) {
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

    protected boolean terminateEarly() {
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
