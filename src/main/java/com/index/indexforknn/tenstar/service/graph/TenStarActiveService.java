package com.index.indexforknn.tenstar.service.graph;

import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.sgrid.domain.SGridVariable;
import com.index.indexforknn.sgrid.domain.SGridVertex;
import com.index.indexforknn.tenstar.domain.TenStarVariable;
import com.index.indexforknn.tenstar.domain.TenStarVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/10/1 zhoutao
 */
@Service
public class TenStarActiveService {
    @Autowired
    TenStarVertexService vertexService;

    public void buildActive() {
        for (Car car : GlobalVariable.CARS) {
            int active = car.getActive();
            TenStarVertex vertex = TenStarVariable.INSTANCE.getVertex(active);

            if (!vertex.isActive()) {
                // build active info
                vertex.setActive(true);
                vertexService.buildAncestor(vertex);
            }
            vertex.addCar(car);
        }
    }
}
