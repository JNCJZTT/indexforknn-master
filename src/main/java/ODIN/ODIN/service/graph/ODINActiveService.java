package ODIN.ODIN.service.graph;

import ODIN.ODIN.domain.ODINActive;
import ODIN.ODIN.domain.ODINCluster;
import ODIN.ODIN.domain.ODINVertex;
import ODIN.ODIN.domain.ODINVariable;
import ODIN.base.domain.Car;
import ODIN.base.domain.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * AhgActiveService
 * 2022/3/26 zhoutao
 */
@Service
public class ODINActiveService {

    @Autowired
    ODINClusterService clusterService;

    /**
     * build active node
     */
    public void buildActive() {
//        long buildStartTime = System.nanoTime();

        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            ODINVertex activeVertex = ODINVariable.INSTANCE.getVertex(active);

            if (!activeVertex.isActive()) {
                // build active info
                addActive(activeVertex);
                activeVertex.setActive(true);
            }
            activeVertex.addCar(car);
        }
//        long buildActiveTime = System.nanoTime() - buildStartTime;
//        System.out.println("build active time:" + (float) buildActiveTime / 1000_000 + "ms");

    }

    public void updateActive(Set<Integer> actives) {
        for (Integer name : actives) {
            ODINVertex vertex = ODINVariable.INSTANCE.getVertex(name);
            if (vertex.isActive()) {
                addActive(vertex);
            } else {
                removeActive(vertex);
            }
        }
    }

    private void addActive(ODINVertex vertex) {
        ODINActive activeInfo = vertex.getActiveInfo() == null ?
                new ODINActive(vertex) : vertex.getActiveInfo();

        ODINCluster leafCluster = ODINVariable.INSTANCE.getLeafCluster(vertex);
        clusterService.updateHighestBorderInfo(activeInfo, vertex, leafCluster);
        leafCluster.addActiveName(vertex.getName());
        vertex.setActiveInfo(activeInfo);
    }

    /**
     * Shallow delete
     * Only active nodes are deleted in the virtual road network,
     * and the distance maintenance matrix between the active nodes and the boundary nodes remains unchanged
     *
     * @param vertex active node
     */
    private void removeActive(ODINVertex vertex) {
        clusterService.removeActive(vertex);
    }
}
