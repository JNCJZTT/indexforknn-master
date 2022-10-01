package com.index.indexforknn.er.graph;

import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.er.domain.ErVariable;
import com.index.indexforknn.er.domain.ErVertex;
import org.springframework.stereotype.Service;

@Service
public class ErActiceService {
    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            ErVertex activeVertex = ErVariable.INSTANCE.getVertex(active);

            if (!activeVertex.isActive()) {
                // build active info
                activeVertex.setActive(true);
            }
            activeVertex.addCar(car);
        }
    }
}
