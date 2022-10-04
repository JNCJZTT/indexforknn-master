package Baseline.SGrid.service.graph;

import Baseline.SGrid.domain.SGridVariable;
import Baseline.SGrid.domain.SGridVertex;
import Baseline.base.domain.Car;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.dto.UpdateDTO;
import Baseline.base.service.graph.CarService;
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
        return IndexType.SGrid;
    }
}
