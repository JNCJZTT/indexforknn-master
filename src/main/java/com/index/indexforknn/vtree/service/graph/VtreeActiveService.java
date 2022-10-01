package com.index.indexforknn.vtree.service.graph;

import com.index.indexforknn.ahg.domain.AhgActive;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.ahg.service.graph.AhgClusterService;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.vtree.domain.VtreeCluster;
import com.index.indexforknn.vtree.domain.VtreeVariable;
import com.index.indexforknn.vtree.domain.VtreeVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/15 zhoutao
 */
@Service
public class VtreeActiveService {
    @Autowired
    AhgClusterService clusterService;

    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            VtreeVertex activeVertex = VtreeVariable.INSTANCE.getVertex(active);

            if (!activeVertex.isActive()) {
                // build active info
                addActive(activeVertex);
                activeVertex.setActive(true);
            }
            activeVertex.addCar(car);
        }

    }

    private void addActive(VtreeVertex vertex) {
        VtreeCluster leafCluster = VtreeVariable.INSTANCE.getLeafCluster(vertex);
        leafCluster.addActive(vertex.getName());
    }

}
