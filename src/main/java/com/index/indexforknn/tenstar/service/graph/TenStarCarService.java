package com.index.indexforknn.tenstar.service.graph;

import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.UpdateDTO;
import com.index.indexforknn.base.service.graph.CarService;
import com.index.indexforknn.sgrid.domain.SGridVariable;
import com.index.indexforknn.sgrid.domain.SGridVertex;
import com.index.indexforknn.tenstar.domain.TenStarVariable;
import com.index.indexforknn.tenstar.domain.TenStarVertex;
import com.index.indexforknn.tenstar.service.dto.TenStarUpdateProcessDTO;
import lombok.Getter;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/10/2 zhoutao
 */
@Service
@Getter
public class TenStarCarService extends CarService {
    private TenStarUpdateProcessDTO updateProcessDTO;

    public TenStarCarService() {
        register();
    }

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        this.updateProcessDTO = new TenStarUpdateProcessDTO();
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        TenStarVertex originalVertex = TenStarVariable.INSTANCE.getVertex(originalActiveName),
                vertex = TenStarVariable.INSTANCE.getVertex(activeName);

        originalVertex.removeCar(car);
        vertex.addCar(car);
        if (!vertex.isActive()) {
            updateProcessDTO.addToChange2InActiveSet(activeName);
            vertex.setActive(true);
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.TENSTAR;
    }
}
