package com.index.indexforknn.tenstar.service.graph;

import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.tenstar.domain.TenStarVariable;
import com.index.indexforknn.tenstar.domain.TenStarVertex;
import com.index.indexforknn.tenstar.service.TenStarIndexService;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/30 zhoutao
 */
@Service
public class TenStarVariableService implements IVariableService {

    public TenStarVariableService() {
        register();
    }

    @Override
    public void buildVertex(int vertexName, String clusterName) {
        TenStarVertex vertex = new TenStarVertex();
        vertex.setName(vertexName);
        TenStarVariable.INSTANCE.addVertex(vertex);
    }

    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {
        TenStarVertex vertex = TenStarVariable.INSTANCE.getVertex(vertexName);
        for (int i = 0; i < edgeInfo.length; i += 2) {
            int neighbor = Integer.parseInt(edgeInfo[i]), dis = Integer.parseInt(edgeInfo[i + 1]);
            if (neighbor < vertexName) continue;

            TenStarVertex neighborVertex = TenStarVariable.INSTANCE.getVertex(neighbor);
            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            neighborVertex.addOrigionEdge(new Node(vertexName, dis));
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.TENSTAR;
    }
}
