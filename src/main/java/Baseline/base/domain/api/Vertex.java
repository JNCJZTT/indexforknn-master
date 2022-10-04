package Baseline.base.domain.api;

import Baseline.base.domain.Car;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Vertex
 * 2022/2/12 zhoutao
 */
@Getter
@Setter
public abstract class Vertex {
    private int name;

    private String clusterName;

    private ArrayList<Node> origionEdges;

    private boolean active = false;

    public PriorityQueue<Car> cars;

    public Vertex() {
        origionEdges = new ArrayList<>();
    }

    /**
     * add Origional Edge
     */
    public void addOrigionEdge(Node node) {
        origionEdges.add(node);
    }

    public Node getRandomEdge() {
        return origionEdges.get(GlobalVariable.RANDOM.nextInt(origionEdges.size()));
    }

    public void addCar(Car car) {
        if (CollectionUtils.isEmpty(cars)) {
            cars = new PriorityQueue<Car>(new Car.ActiveDisComprator());
        }
        cars.add(car);
    }

    public void removeCar(Car car) {
        cars.remove(car);
        if (cars.isEmpty()) {
            active = false;
        }
    }

}
