package com.index.indexforknn.ahg.service.dto.result;

import com.index.indexforknn.ahg.domain.AhgKnn;
import com.index.indexforknn.ahg.service.graph.AhgVariableService;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.service.dto.KnnDTO;
import com.index.indexforknn.base.service.dto.ResultDTO;
import com.index.indexforknn.base.service.factory.ServiceFactory;
import com.index.indexforknn.base.service.knn.DijkstraService;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * AhgKnnResultDTO
 * 2022/4/16 zhoutao
 */
@Getter
@Service
@ToString
public class AhgKnnResultDTO extends ResultDTO {
    private static final String QUERY_NAME = "QueryName";

    private static final String QUERY_TIME = "QueryTime";

    private static final String KNN = "KNN";

    public AhgKnnResultDTO() {
        super();
    }

    public void buildResult(AhgKnn ahgKnn, KnnDTO knnDTO) {
        if (knnDTO.isPrintQueryName()) {
            result.put(QUERY_NAME, ahgKnn.getQueryName());
        }
        result.put(QUERY_TIME, ahgKnn.getQueryTime() + "us");
        if (knnDTO.isPrintKnn()) {
            result.put(KNN, compareLastCar(ahgKnn.getKCars()));
        }
        if (knnDTO.isDijkstra()) {
            AhgVariableService variableService = ServiceFactory.getVariableService();
            DijkstraService dijkstraService = new DijkstraService();
            dijkstraService.Dijkstra(variableService.getVertices(), ahgKnn.getQueryName());
            result.put("Dijkstra", compareLastCar(dijkstraService.kCars));
        }
    }

    private List buildKnn(PriorityQueue<Car> kCars) {
        List<Node> carData = new ArrayList<>();
        for (int i = 0; i < GlobalVariable.K; i++) {
            carData.add(new Node(-1, -1));
        }

        int index = GlobalVariable.K - 1;
        while (!kCars.isEmpty()) {
            Car car = kCars.poll();
            carData.get(index).setName(car.getName());
            carData.get(index).setDis(car.getQueryDis());
            index--;
        }
        return carData;
    }

    private Node compareLastCar(PriorityQueue<Car> kCars) {
        Car car = kCars.poll();
        return new Node(car.getName(), car.getQueryDis());
    }
}
