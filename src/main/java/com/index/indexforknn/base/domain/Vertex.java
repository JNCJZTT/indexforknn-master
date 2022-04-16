package com.index.indexforknn.base.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * TODO
 * 2022/2/12 zhoutao
 */
@Getter
@Setter
public class Vertex {
    private int name;

    private String clusterName;

    private ArrayList<Node> origionEdges;

    private boolean active = false;

    public PriorityQueue<Car> cars;

    public Vertex() {
        origionEdges = new ArrayList<>();
    }

    /**
     * 添加原始道路结点
     */
    public void addOrigionEdge(Node node) {
        origionEdges.add(node);
    }

    public Node getRandomEdge() {
        return origionEdges.get(GlobalVariable.RANDOM.nextInt(origionEdges.size()));
    }

    public void addCar(Car car) {
        if (cars == null) {
            cars = new PriorityQueue<>(new Car.ActiveDisComprator());
        }
        cars.add(car);
    }

}
