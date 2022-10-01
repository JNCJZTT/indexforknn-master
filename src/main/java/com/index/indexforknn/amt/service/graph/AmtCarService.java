package com.index.indexforknn.amt.service.graph;

import com.index.indexforknn.amt.domain.AmtVertex;
import com.index.indexforknn.amt.service.dto.AmtUpdateProcessDTO;
import com.index.indexforknn.amt.domain.AmtVariable;
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
public class AmtCarService extends CarService {
    private AmtUpdateProcessDTO updateProcessDTO;

    public AmtCarService() {
        variableService = new AmtVariableService();
        register();
    }

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        this.updateProcessDTO = new AmtUpdateProcessDTO();
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        AmtVertex originalVertex = AmtVariable.INSTANCE.getVertex(originalActiveName),
                vertex = AmtVariable.INSTANCE.getVertex(activeName);

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
