package Baseline.base.service.dto.result;

import Baseline.base.domain.Car;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import Baseline.base.domain.api.knn.Knn;
import Baseline.base.service.dto.KnnDTO;
import Baseline.base.service.knn.DijkstraService;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


/**
 * AhgKnnResultDTO
 * 2022/4/16 zhoutao
 */
@Getter
@Service
@ToString
public class KnnResultDTO extends ResultDTO {
    private static final String QUERY_NAME = "QueryName";

    private static final String QUERY_TIME = "QueryTime";

    private static final String KNN = "KNN";

    public KnnResultDTO() {
        super();
    }

    public void buildResult(Knn knn, KnnDTO knnDTO) {
        if (knnDTO.isPrintQueryName()) {
            result.put(QUERY_NAME, knn.getQueryName());
        }
        result.put(QUERY_TIME, knn.getQueryTime() + "us");
        if (knnDTO.isPrintKnn()) {
            result.put(KNN, compareLastCar(knn.getKCars()));
        }
        if (knnDTO.isDijkstra()) {
            DijkstraService dijkstraService = new DijkstraService();
            dijkstraService.Dijkstra(GlobalVariable.variable.getVertices(), knn.getQueryName());
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
