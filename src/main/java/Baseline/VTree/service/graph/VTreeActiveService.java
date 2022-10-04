package Baseline.VTree.service.graph;

import Baseline.VTree.domain.VTreeCluster;
import Baseline.VTree.domain.VtreeVariable;
import Baseline.VTree.domain.VTreeVertex;
import Baseline.base.domain.Car;
import Baseline.base.domain.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/15 zhoutao
 */
@Service
public class VTreeActiveService {
    @Autowired
    VTreeClusterService clusterService;

    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            VTreeVertex activeVertex = VtreeVariable.INSTANCE.getVertex(active);

            if (!activeVertex.isActive()) {
                // build active info
                addActive(activeVertex);
                activeVertex.setActive(true);
            }
            activeVertex.addCar(car);
        }

    }

    private void addActive(VTreeVertex vertex) {
        VTreeCluster leafCluster = VtreeVariable.INSTANCE.getLeafCluster(vertex);
        leafCluster.addActive(vertex.getName());
    }

}
