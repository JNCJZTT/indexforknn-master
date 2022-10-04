package Baseline.SIMkNN.graph;

import Baseline.SIMkNN.domain.SIMkNNVariable;
import Baseline.SIMkNN.domain.SIMkNNVertex;
import Baseline.base.domain.Car;
import Baseline.base.domain.GlobalVariable;
import org.springframework.stereotype.Service;

@Service
public class SIMkNNActiceService {
    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            SIMkNNVertex activeVertex = SIMkNNVariable.INSTANCE.getVertex(active);

            if (!activeVertex.isActive()) {
                // build active info
                activeVertex.setActive(true);
            }
            activeVertex.addCar(car);
        }
    }
}
