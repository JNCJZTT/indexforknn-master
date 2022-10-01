package com.index.indexforknn.vtree.service.graph;

import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.vtree.domain.VtreeCluster;
import com.index.indexforknn.vtree.domain.VtreeVariable;
import com.index.indexforknn.vtree.domain.VtreeVertex;
import com.index.indexforknn.vtree.service.build.VtreeClusterBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
@Slf4j
@Service
public class VtreeVariableService implements IVariableService {

    public VtreeVariableService() {
        register();
    }

    @Override
    public void buildVertex(int vertexName, String clusterName) {
        if (!VtreeVariable.INSTANCE.containsClusterKey(clusterName)) {
            VtreeVariable.INSTANCE.addCluster(clusterName, VtreeClusterBuilder.build(clusterName, true));
        }

        VtreeVertex vertex = new VtreeVertex();
        vertex.setName(vertexName);
        vertex.setClusterName(clusterName);
        VtreeVariable.INSTANCE.addVertex(vertex);
        VtreeVariable.INSTANCE.getCluster(clusterName).addVertex(vertexName);
    }

    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {
        VtreeVertex vertex = VtreeVariable.INSTANCE.getVertex(vertexName);
        String clusterName = vertex.getClusterName();
        VtreeCluster cluster = VtreeVariable.INSTANCE.getCluster(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);

            if (neighbor < vertexName) continue;

            VtreeVertex neighborVertex = VtreeVariable.INSTANCE.getVertex(neighbor);

            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            neighborVertex.addOrigionEdge(new Node(vertexName, dis));

            if (clusterName.equals(neighborVertex.getClusterName())) {
                cluster.addClusterLink(vertexName, neighbor, dis);
            } else {
                cluster.addBorderLink(vertexName, neighbor, dis);
                VtreeVariable.INSTANCE.getCluster(neighborVertex.getClusterName())
                        .addBorderLink(neighbor, vertexName, dis);
            }

        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.VTREE;
    }
}
