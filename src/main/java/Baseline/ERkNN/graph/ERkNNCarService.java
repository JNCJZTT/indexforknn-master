package Baseline.ERkNN.graph;


import Baseline.ERkNN.domain.ERkNNVariable;
import Baseline.ERkNN.domain.ERkNNVertex;
import Baseline.ERkNN.dto.ERkNNUpdateProcessDTO;
import Baseline.base.domain.Car;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.dto.UpdateDTO;
import Baseline.base.service.graph.CarService;
import org.springframework.stereotype.Service;

@Service
public class ERkNNCarService extends CarService {
    public ERkNNCarService() {
        register();
    }

    private ERkNNUpdateProcessDTO updateProcessDTO;

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        // don't need do anything
        this.updateProcessDTO = new ERkNNUpdateProcessDTO();
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        ERkNNVertex originalVertex = ERkNNVariable.INSTANCE.getVertex(originalActiveName),
                vertex = ERkNNVariable.INSTANCE.getVertex(activeName);

        originalVertex.removeCar(car);
        vertex.addCar(car);
        if (!vertex.isActive()) {
            vertex.setActive(true);
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.ERkNN;
    }
}
