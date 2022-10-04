package Baseline.base.domain.api.knn;

import Baseline.base.domain.Car;
import lombok.Data;

import java.util.PriorityQueue;

/**
 * TODO
 * 2022/5/12 zhoutao
 */
@Data
public abstract class Knn {
    // knn
    protected PriorityQueue<Car> kCars;

    // queryName
    protected Integer queryName;

    // queryTime
    protected double queryTime;


}
