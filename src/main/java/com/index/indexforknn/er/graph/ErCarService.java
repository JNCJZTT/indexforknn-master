package com.index.indexforknn.er.graph;


import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.UpdateDTO;
import com.index.indexforknn.base.service.graph.CarService;
import com.index.indexforknn.er.domain.ErVariable;
import com.index.indexforknn.er.domain.ErVertex;
import com.index.indexforknn.er.dto.ErUpdateProcessDTO;
import org.springframework.stereotype.Service;

@Service
public class ErCarService extends CarService {
    public ErCarService() {
        register();
    }

    private ErUpdateProcessDTO updateProcessDTO;

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        // don't need do anything
        this.updateProcessDTO = new ErUpdateProcessDTO();
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        ErVertex originalVertex = ErVariable.INSTANCE.getVertex(originalActiveName),
                vertex = ErVariable.INSTANCE.getVertex(activeName);

        originalVertex.removeCar(car);
        vertex.addCar(car);
        if (!vertex.isActive()) {
            vertex.setActive(true);
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.ER;
    }
}
