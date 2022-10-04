package Baseline.TenIndex.service.graph;

import Baseline.TenIndex.domain.TenIndexVariable;
import Baseline.TenIndex.domain.TenIndexVertex;
import Baseline.TenIndex.service.dto.TenIndexUpdateProcessDTO;
import Baseline.base.domain.Car;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.dto.UpdateDTO;
import Baseline.base.service.graph.CarService;
import lombok.Getter;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/10/2 zhoutao
 */
@Service
@Getter
public class TenIndexCarService extends CarService {
    private TenIndexUpdateProcessDTO updateProcessDTO;

    public TenIndexCarService() {
        register();
    }

    @Override
    protected void initUpdateDTO(UpdateDTO updateDTO) {
        this.updateProcessDTO = new TenIndexUpdateProcessDTO();
    }

    @Override
    protected void updateActive(int originalActiveName, int activeName, Car car) {
        TenIndexVertex originalVertex = TenIndexVariable.INSTANCE.getVertex(originalActiveName),
                vertex = TenIndexVariable.INSTANCE.getVertex(activeName);

        originalVertex.removeCar(car);
        vertex.addCar(car);
        if (!vertex.isActive()) {
            updateProcessDTO.addToChange2InActiveSet(activeName);
            vertex.setActive(true);
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.TenIndex;
    }
}
