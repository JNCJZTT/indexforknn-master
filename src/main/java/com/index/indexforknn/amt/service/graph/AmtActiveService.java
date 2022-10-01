package com.index.indexforknn.amt.service.graph;

import com.index.indexforknn.amt.domain.AmtActive;
import com.index.indexforknn.amt.domain.AmtCluster;
import com.index.indexforknn.amt.domain.AmtVertex;
import com.index.indexforknn.amt.domain.AmtVariable;
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
public class AmtActiveService {

    @Autowired
    AmtClusterService clusterService;

    /**
     * build active node
     */
    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            AmtVertex activeVertex = AmtVariable.INSTANCE.getVertex(active);

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
            AmtVertex vertex = AmtVariable.INSTANCE.getVertex(name);
            if (vertex.isActive()) {
                addActive(vertex);
            } else {
                removeActive(vertex);
            }
        }
    }

    private void addActive(AmtVertex vertex) {
        AmtActive activeInfo = vertex.getActiveInfo() == null ?
                new AmtActive(vertex) : vertex.getActiveInfo();

        AmtCluster leafCluster = AmtVariable.INSTANCE.getLeafCluster(vertex);
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
    private void removeActive(AmtVertex vertex) {
        clusterService.removeActive(vertex);
    }
}
