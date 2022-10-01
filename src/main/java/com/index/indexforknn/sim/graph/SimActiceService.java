package com.index.indexforknn.sim.graph;

import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.sim.domain.SimVariable;
import com.index.indexforknn.sim.domain.SimVertex;
import org.springframework.stereotype.Service;

@Service
public class SimActiceService {
    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            SimVertex activeVertex = SimVariable.INSTANCE.getVertex(active);

            if (!activeVertex.isActive()) {
                // build active info
                activeVertex.setActive(true);
            }
            activeVertex.addCar(car);
        }
    }
}
