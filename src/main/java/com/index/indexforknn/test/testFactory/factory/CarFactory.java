package com.index.indexforknn.test.testFactory.factory;

import com.index.indexforknn.test.testFactory.domain.Car;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 2022/3/20 zhoutao
 */
public class CarFactory {
    private static Map<String, Car> carFactory = new HashMap<>();

    public static void register(String carType, Car car) {
        if (!carFactory.containsKey(carType)) {
            carFactory.put(carType, car);
        }
    }

    public static Car getCar(String carType) {
        return carFactory.get(carType);
    }
}
