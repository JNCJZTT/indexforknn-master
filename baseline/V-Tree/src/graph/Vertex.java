package graph;

import common.Comprators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import static common.Constants.RANDOM;
import static common.GlobalVariable.CLUSTER_LEVEL;
import static common.GlobalVariable.allClusters;

public class Vertex {
    private int vertexName;

    private int[] clusterName;

    public List<Vnode> originalEdges;

    public boolean[] isBorder;

    public PriorityQueue<Car> vertexCars;

    public boolean isActive = false;


    public Vertex(int vertexName) {
        this.vertexName = vertexName;
        originalEdges = new ArrayList<>();
        clusterName = new int[CLUSTER_LEVEL];
        isBorder = new boolean[CLUSTER_LEVEL];
        vertexCars = new PriorityQueue<Car>(new Comprators.CurDisComprator());
    }

    public void setClusterName(int level, int clusterName) {
        this.clusterName[level] = clusterName;
    }

    public Integer getClusterName(int level) {
        return this.clusterName[level];
    }

    public boolean addCar(Car c) {
        vertexCars.add(c);
        if (!isActive) {
            isActive = true;
            return true;
        }
        return false;
    }


    public void addOriginalEdges(int name, int dis) {
        originalEdges.add(new Vnode(name, dis));
    }

    public Vnode getRandomEdge() {
        return originalEdges.get(RANDOM.nextInt(originalEdges.size()));
    }


    /*
     * 返回是否更新状态
     * */
    public boolean removeCar(Car c) {
        vertexCars.remove(c);
        if (vertexCars.isEmpty()) {
            isActive = false;
        }
        return !isActive;
    }
}
