package Baseline.TenIndex.service.graph;

import Baseline.TenIndex.domain.TenIndexVariable;
import Baseline.TenIndex.domain.TenIndexVertex;
import Baseline.base.domain.Car;
import Baseline.base.domain.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/10/1 zhoutao
 */
@Service
public class TenIndexActiveService {
    @Autowired
    TenIndexVertexService vertexService;

    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            TenIndexVertex vertex = TenIndexVariable.INSTANCE.getVertex(active);

            if (!vertex.isActive()) {
                // build active info
                vertex.setActive(true);
                vertexService.buildAncestor(vertex);
            }
            vertex.addCar(car);
        }
    }
}
