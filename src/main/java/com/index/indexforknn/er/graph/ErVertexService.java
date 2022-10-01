package com.index.indexforknn.er.graph;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.er.domain.ErVariable;
import com.index.indexforknn.er.domain.ErVertex;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErVertexService {

    public ErVertex generateBorderVertex(List<String> clusterNames) {
        int borderName = GlobalVariable.VERTEX_NUM + ErVariable.INSTANCE.getBorderSize();

        ErVertex vertex = new ErVertex();
        vertex.setName(borderName);
        vertex.setClusterNames(clusterNames);
        vertex.setBorder(true);

        ErVariable.INSTANCE.autoIncrementBorderSize();
        ErVariable.INSTANCE.addVertex(vertex);

        return vertex;
    }
}
