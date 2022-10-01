package com.index.indexforknn.sim.graph;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.sim.domain.SimVariable;
import com.index.indexforknn.sim.domain.SimVertex;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimVertexService {

    public SimVertex generateBorderVertex(List<String> clusterNames) {
        int borderName = GlobalVariable.VERTEX_NUM + SimVariable.INSTANCE.getBorderSize();

        SimVertex vertex = new SimVertex();
        vertex.setName(borderName);
        vertex.setClusterNames(clusterNames);
        vertex.setBorder(true);

        SimVariable.INSTANCE.autoIncrementBorderSize();
        SimVariable.INSTANCE.addVertex(vertex);

        return vertex;
    }
}
