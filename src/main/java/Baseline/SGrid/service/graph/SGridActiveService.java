package Baseline.SGrid.service.graph;

import Baseline.SGrid.domain.SGridVariable;
import Baseline.SGrid.domain.SGridVertex;
import Baseline.base.domain.Car;
import Baseline.base.domain.GlobalVariable;
import org.springframework.stereotype.Service;

/**
 * SGridActiveService
 * 2022/5/22 zhoutao
 */
@Service
public class SGridActiveService {
    /**
     * build active node
     */
    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            SGridVertex activeVertex = SGridVariable.INSTANCE.getVertex(active);

            if (!activeVertex.isActive()) {
                // build active info
                activeVertex.setActive(true);
            }
            activeVertex.addCar(car);
        }
    }
}
