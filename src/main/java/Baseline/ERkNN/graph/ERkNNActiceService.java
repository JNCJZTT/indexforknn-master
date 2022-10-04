package Baseline.ERkNN.graph;

import Baseline.ERkNN.domain.ERkNNVariable;
import Baseline.ERkNN.domain.ERkNNVertex;
import Baseline.base.domain.Car;
import Baseline.base.domain.GlobalVariable;
import org.springframework.stereotype.Service;

@Service
public class ERkNNActiceService {
    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            ERkNNVertex activeVertex = ERkNNVariable.INSTANCE.getVertex(active);

            if (!activeVertex.isActive()) {
                // build active info
                activeVertex.setActive(true);
            }
            activeVertex.addCar(car);
        }
    }
}
