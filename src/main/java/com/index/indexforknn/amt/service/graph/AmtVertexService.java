package com.index.indexforknn.amt.service.graph;

import com.index.indexforknn.amt.domain.AmtVertex;
import com.index.indexforknn.amt.domain.AmtVariable;
import com.index.indexforknn.base.domain.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * AhgVertexService
 * 2022/3/12 zhoutao
 */
@Slf4j
@Service
public class AmtVertexService {

    /**
     * build borders
     */
    public void buildBorders() {
        AmtVariable.INSTANCE.getVertices().forEach(this::buildBorder);
    }

    /**
     * build border for vertex
     */
    public void buildBorder(AmtVertex vertex) {
        String[] clusterNames = vertex.getClusterNames();
        int minIndex = clusterNames.length;
        // traverse neighbor nodes
        for (Node node : vertex.getOrigionEdges()) {
            AmtVertex neighborVertex = AmtVariable.INSTANCE.getVertex(node.getName());
            String[] neighborClusterNames = neighborVertex.getClusterNames();

            // Determine whether the i-th layer is a boundary point
            for (int i = 0; i < Math.min(clusterNames.length, neighborClusterNames.length); i++) {
                if (!clusterNames[i].equals(neighborClusterNames[i])) {
                    minIndex = Math.min(i, minIndex);
                    break;
                }
            }
        }
        Arrays.fill(vertex.getBorder(), minIndex, clusterNames.length, true);
    }

}
