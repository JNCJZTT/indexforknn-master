package Baseline.VTree.service.graph;

import Baseline.VTree.domain.VtreeVariable;
import Baseline.VTree.domain.VTreeVertex;
import Baseline.VTree.service.dto.VTreeUpdateProcessDTO;
import Baseline.base.domain.Car;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.dto.UpdateDTO;
import Baseline.base.service.graph.CarService;
import lombok.Getter;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/18 zhoutao
 */
@Service
@Getter
public class VTreeCarService extends CarService {
    private VTreeUpdateProcessDTO updateProcessDTO;

    public VTreeCarService() {
        register();
    }

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        this.updateProcessDTO = new VTreeUpdateProcessDTO();
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        VTreeVertex originalVertex = VtreeVariable.INSTANCE.getVertex(originalActiveName),
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
        return IndexType.VTree;
    }
}
