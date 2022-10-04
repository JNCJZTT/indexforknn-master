package Baseline.SIMkNN.graph;

import Baseline.SIMkNN.domain.SIMkNNVariable;
import Baseline.SIMkNN.domain.SIMkNNVertex;
import Baseline.base.domain.Car;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.dto.UpdateDTO;
import Baseline.base.service.graph.CarService;
import org.springframework.stereotype.Service;

@Service
public class SIMkNNCarService extends CarService {
    public SIMkNNCarService() {
        register();
    }

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        // don't need do anything
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        SIMkNNVertex originalVertex = SIMkNNVariable.INSTANCE.getVertex(originalActiveName),
                vertex = SIMkNNVariable.INSTANCE.getVertex(activeName);

        originalVertex.removeCar(car);
        vertex.addCar(car);
        if (!vertex.isActive()) {
            vertex.setActive(true);
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.SIMkNN;
    }
}
