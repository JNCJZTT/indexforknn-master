package ODIN.ODIN.service.graph;

import ODIN.ODIN.domain.ODINVertex;
import ODIN.ODIN.domain.ODINVariable;
import ODIN.base.domain.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * AhgVertexService
 * 2022/3/12 zhoutao
 */
@Slf4j
@Service
public class ODINVertexService {

    /**
     * build borders
     */
    public void buildBorders() {
//        long buildStartTime = System.nanoTime();
        ODINVariable.INSTANCE.getVertices().parallelStream().forEach(this::buildBorder);
//        long buildBorderTime = System.nanoTime() - buildStartTime;
//        System.out.println("the time of building borders : " + (float) (buildBorderTime) / 1000_000 + "ms");
    }

    /**
     * build border for vertex
     */
    private void buildBorder(ODINVertex vertex) {
        String[] clusterNames = vertex.getClusterNames();
        int minIndex = clusterNames.length;
        // traverse neighbor nodes

        for (Node node : vertex.getOrigionEdges()) {
            ODINVertex neighborVertex = ODINVariable.INSTANCE.getVertex(node.getName());
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
