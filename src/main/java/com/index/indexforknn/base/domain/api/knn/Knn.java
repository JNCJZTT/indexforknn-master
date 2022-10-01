package com.index.indexforknn.base.domain.api.knn;

import com.index.indexforknn.base.domain.Car;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
