package com.index.indexforknn.vtree.service.graph;


import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.vtree.domain.VtreeVariable;
import com.index.indexforknn.vtree.domain.VtreeVertex;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * TODO
 * 2022/9/15 zhoutao
 */
@Service
public class VtreeVertexService {

    /**
     * build borders
     */
    public void buildBorders() {
        VtreeVariable.INSTANCE.getVertices().parallelStream().forEach(this::buildBorder);
    }

    /**
     * build border for vertex
     */
    private void buildBorder(VtreeVertex vertex) {
        String[] clusterNames = vertex.getClusterNames();
        int minIndex = clusterNames.length;
        // traverse neighbor nodes
        for (Node node : vertex.getOrigionEdges()) {
            VtreeVertex neighborVertex = VtreeVariable.INSTANCE.getVertex(node.getName());
            String[] neighborClusterNames = neighborVertex.getClusterNames();

//          Determine whether the i -th layer is a boundary point
            for (int i = 0; i < minIndex; i++) {
                if (!clusterNames[i].equals(neighborClusterNames[i])) {
                    minIndex = i;
                    break;
                }
            }
        }
        Arrays.fill(vertex.getBorder(), minIndex, clusterNames.length, true);
    }
}
