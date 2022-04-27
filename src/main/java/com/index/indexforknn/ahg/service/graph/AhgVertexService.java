package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.ahg.service.utils.AhgVariableUtil;
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
public class AhgVertexService {

    /**
     * build borders
     */
    public void buildBorders() {
        AhgVariableUtil.getVertices().forEach(this::buildBorder);
    }

    /**
     * build border for vertex
     */
    public void buildBorder(AhgVertex vertex) {
        String[] clusterNames = vertex.getClusterNames();
        int minIndex = clusterNames.length;
        // traverse neighbor nodes
        for (Node node : vertex.getOrigionEdges()) {
            AhgVertex neighborVertex = AhgVariableUtil.getVertex(node.getName());
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
