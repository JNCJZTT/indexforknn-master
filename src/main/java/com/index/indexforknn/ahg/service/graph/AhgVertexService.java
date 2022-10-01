package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

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
//        long buildStartTime = System.nanoTime();
        AhgVariable.INSTANCE.getVertices().parallelStream().forEach(this::buildBorder);
//        long buildBorderTime = System.nanoTime() - buildStartTime;
//        System.out.println("the time of building borders : " + (float) (buildBorderTime) / 1000_000 + "ms");
    }

    /**
     * build border for vertex
     */
    private void buildBorder(AhgVertex vertex) {
        String[] clusterNames = vertex.getClusterNames();
        int minIndex = clusterNames.length;
        // traverse neighbor nodes

        for (Node node : vertex.getOrigionEdges()) {
            AhgVertex neighborVertex = AhgVariable.INSTANCE.getVertex(node.getName());
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
