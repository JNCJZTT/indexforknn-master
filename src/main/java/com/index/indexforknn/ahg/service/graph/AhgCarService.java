package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.ahg.service.dto.AhgUpdateProcessDTO;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.UpdateDTO;
import com.index.indexforknn.base.service.graph.CarService;
import lombok.Getter;
import org.springframework.stereotype.Service;

/**
 * AhgCarService
 * 2022/4/19 zhoutao
 */
@Service
@Getter
public class AhgCarService extends CarService {
    private AhgUpdateProcessDTO updateProcessDTO;

    public AhgCarService() {
        variableService = new AhgVariableService();
        register();
    }

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        this.updateProcessDTO = new AhgUpdateProcessDTO();
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        AhgVertex originalVertex = AhgVariable.vertices.get(originalActiveName),
                vertex = AhgVariable.vertices.get(activeName);

        originalVertex.removeCar(car);
        vertex.addCar(car);
        if (!originalVertex.isActive()) {
            updateProcessDTO.change2InActive(originalActiveName);
        }
        if (!vertex.isActive()) {
            vertex.setActive(true);
            updateProcessDTO.change2Active(activeName);
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.AHG;
    }
}
