package com.index.indexforknn.sgrid.service.graph;

import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.sgrid.domain.SGridVariable;
import com.index.indexforknn.sgrid.domain.SGridVertex;
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
