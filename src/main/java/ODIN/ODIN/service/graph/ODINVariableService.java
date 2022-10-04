package ODIN.ODIN.service.graph;

import ODIN.ODIN.domain.ODINCluster;
import ODIN.ODIN.domain.ODINVertex;
import ODIN.ODIN.service.build.ODINClusterBuilder;
import ODIN.ODIN.domain.ODINVariable;
import ODIN.base.domain.Node;
import ODIN.base.domain.enumeration.IndexType;
import ODIN.base.service.api.IVariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.*;

/**
 * AhgVariableService
 * 2022/2/11 zhoutao
 */
@Slf4j
@Service
public class ODINVariableService implements IVariableService {

    public ODINVariableService() {
        register();
    }

//    @Override
//    public void initVariable(IndexDTO ahgIndex) {
//        ODINVariable.INSTANCE.initVariables(ahgIndex);
//    }

    @Override
    public void buildVertex(int vertexName, String clusterName) {
        // build leaf cluster
        if (!ODINVariable.INSTANCE.containsClusterKey(clusterName)) {
            ODINVariable.INSTANCE.addCluster(clusterName, ODINClusterBuilder.build(clusterName, true));
        }

        // build vertex
        ODINVertex vertex = new ODINVertex();
        vertex.setName(vertexName);
        vertex.setClusterName(clusterName);
        ODINVariable.INSTANCE.addVertex(vertex);

        // add vertex into cluster
        ODINVariable.INSTANCE.getCluster(clusterName).addVertex(vertexName);
    }

    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {
        ODINVertex vertex = ODINVariable.INSTANCE.getVertex(vertexName);
        String clusterName = vertex.getClusterName();
        ODINCluster cluster = ODINVariable.INSTANCE.getCluster(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);

            // Only save one - way edge.
            // Need to be modified if it is a directed graph
            if (neighbor < vertexName) continue;

            ODINVertex neighborVertex = ODINVariable.INSTANCE.getVertex(neighbor);

            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            neighborVertex.addOrigionEdge(new Node(vertexName, dis));

            // connect neighbor node
            if (clusterName.equals(neighborVertex.getClusterName())) {
                cluster.addClusterLink(vertexName, neighbor, dis);
            } else {
                cluster.addBorderLink(vertexName, neighbor, dis);
                ODINVariable.INSTANCE.getCluster(neighborVertex.getClusterName())
                        .addBorderLink(neighbor, vertexName, dis);
            }
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.ODIN;
    }

    /**
     * build full true
     */
    public void buildFullTreeKey() {
        Queue<String> queue = new LinkedList<>(ODINVariable.INSTANCE.getClusterKeySet());
        while (!queue.isEmpty()) {
            String clusterName = queue.poll();
            String parentName;
            while (StringUtils.hasLength(parentName = ODINVariable.INSTANCE.getParentClusterName(clusterName))
                    && !ODINVariable.INSTANCE.containsClusterKey(parentName)) {
                ODINVariable.INSTANCE.addCluster(parentName, null);
                clusterName = parentName;
            }
        }
    }
}
