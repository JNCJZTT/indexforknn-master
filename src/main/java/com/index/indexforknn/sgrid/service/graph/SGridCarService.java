package com.index.indexforknn.sgrid.service.graph;

import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.UpdateDTO;
import com.index.indexforknn.base.service.graph.CarService;
import com.index.indexforknn.sgrid.domain.SGridVariable;
import com.index.indexforknn.sgrid.domain.SGridVertex;
import org.springframework.stereotype.Service;

/**
 * SGridCarService
 * 2022/5/22 zhoutao
 */
@Service
public class SGridCarService extends CarService {
    public SGridCarService() {
        register();
    }

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        // don't need do anything
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        SGridVertex originalVertex = SGridVariable.INSTANCE.getVertex(originalActiveName),
                vertex = SGridVariable.INSTANCE.getVertex(activeName);

        originalVertex.removeCar(car);
        vertex.addCar(car);
        if (!vertex.isActive()) {
            vertex.setActive(true);
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.SGRID;
    }
}
