package ODIN.ODIN.service.graph;

import ODIN.ODIN.domain.ODINVertex;
import ODIN.ODIN.domain.ODINVariable;
import ODIN.ODIN.service.dto.ODINUpdateProcessDTO;
import ODIN.base.domain.Car;
import ODIN.base.domain.enumeration.IndexType;
import ODIN.base.service.dto.UpdateDTO;
import ODIN.base.service.graph.CarService;
import lombok.Getter;
import org.springframework.stereotype.Service;

/**
 * AhgCarService
 * 2022/4/19 zhoutao
 */
@Service
@Getter
public class ODINCarService extends CarService {
    private ODINUpdateProcessDTO updateProcessDTO;

    public ODINCarService() {
        variableService = new ODINVariableService();
        register();
    }

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        this.updateProcessDTO = new ODINUpdateProcessDTO();
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        ODINVertex originalVertex = ODINVariable.INSTANCE.getVertex(originalActiveName),
                vertex = ODINVariable.INSTANCE.getVertex(activeName);

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
        return IndexType.ODIN;
    }
}
