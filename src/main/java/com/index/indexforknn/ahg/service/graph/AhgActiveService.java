package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.domain.AhgActive;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.ahg.service.utils.AhgVariableUtil;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * AhgActiveService
 * 2022/3/26 zhoutao
 */
@Service
public class AhgActiveService {

    @Autowired
    AhgClusterService clusterService;

    /**
     * build active node
     */
    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            AhgVertex activeVertex = AhgVariableUtil.getVertex(active);

            if (!activeVertex.isActive()) {
                // build active info
                addActive(activeVertex);
                activeVertex.setActive(true);
            }
            activeVertex.addCar(car);
        }
    }

    public void updateActive(Set<Integer> actives) {
        for (Integer name : actives) {
            AhgVertex vertex = AhgVariable.vertices.get(name);
            if (vertex.isActive()) {
                addActive(vertex);
            } else {
                removeActive(vertex);
            }
        }
    }

    private void addActive(AhgVertex vertex) {
        AhgActive activeInfo = vertex.getActiveInfo() == null ?
                new AhgActive(vertex) : vertex.getActiveInfo();

        AhgCluster leafCluster = AhgVariableUtil.getLeafCluster(vertex);
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
    private void removeActive(AhgVertex vertex) {
        clusterService.removeActive(vertex);
    }
}
