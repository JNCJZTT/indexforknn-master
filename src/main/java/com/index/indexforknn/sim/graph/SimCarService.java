package com.index.indexforknn.sim.graph;

import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.UpdateDTO;
import com.index.indexforknn.base.service.graph.CarService;
import com.index.indexforknn.sim.domain.SimVariable;
import com.index.indexforknn.sim.domain.SimVertex;
import org.springframework.stereotype.Service;

@Service
public class SimCarService extends CarService {
    public SimCarService() {
        register();
    }

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        // don't need do anything
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        SimVertex originalVertex = SimVariable.INSTANCE.getVertex(originalActiveName),
                vertex = SimVariable.INSTANCE.getVertex(activeName);

        originalVertex.removeCar(car);
        vertex.addCar(car);
        if (!vertex.isActive()) {
            vertex.setActive(true);
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.SIM;
    }
}
