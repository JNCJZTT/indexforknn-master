package com.index.indexforknn.vtree.service.graph;

import com.index.indexforknn.ahg.service.dto.AhgUpdateProcessDTO;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.dto.UpdateDTO;
import com.index.indexforknn.base.service.graph.CarService;
import com.index.indexforknn.sgrid.domain.SGridVariable;
import com.index.indexforknn.sgrid.domain.SGridVertex;
import com.index.indexforknn.vtree.domain.VtreeVariable;
import com.index.indexforknn.vtree.domain.VtreeVertex;
import com.index.indexforknn.vtree.service.dto.VtreeUpdateProcessDTO;
import lombok.Getter;
import org.jboss.jandex.Index;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/18 zhoutao
 */
@Service
@Getter
public class VtreeCarService extends CarService {
    private VtreeUpdateProcessDTO updateProcessDTO;

    public VtreeCarService() {
        register();
    }

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        this.updateProcessDTO = new VtreeUpdateProcessDTO();
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        VtreeVertex originalVertex = VtreeVariable.INSTANCE.getVertex(originalActiveName),
                vertex = VtreeVariable.INSTANCE.getVertex(activeName);

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
        return IndexType.VTREE;
    }
}
