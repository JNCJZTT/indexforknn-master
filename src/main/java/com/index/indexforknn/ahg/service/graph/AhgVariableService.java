package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.ahg.service.build.AhgClusterBuilder;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.base.service.dto.IndexDTO;
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
public class AhgVariableService implements IVariableService {

    public AhgVariableService() {
        register();
    }

//    @Override
//    public void initVariable(IndexDTO ahgIndex) {
//        AhgVariable.INSTANCE.initVariables(ahgIndex);
//    }

    @Override
    public void buildVertex(int vertexName, String clusterName) {
        // build leaf cluster
        if (!AhgVariable.INSTANCE.containsClusterKey(clusterName)) {
            AhgVariable.INSTANCE.addCluster(clusterName, AhgClusterBuilder.build(clusterName, true));
        }

        // build vertex
        AhgVertex vertex = new AhgVertex();
        vertex.setName(vertexName);
        vertex.setClusterName(clusterName);
        AhgVariable.INSTANCE.addVertex(vertex);

        // add vertex into cluster
        AhgVariable.INSTANCE.getCluster(clusterName).addVertex(vertexName);
    }

    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {
        AhgVertex vertex = AhgVariable.INSTANCE.getVertex(vertexName);
        String clusterName = vertex.getClusterName();
        AhgCluster cluster = AhgVariable.INSTANCE.getCluster(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);

            // Only save one - way edge.
            // Need to be modified if it is a directed graph
            if (neighbor < vertexName) continue;

            AhgVertex neighborVertex = AhgVariable.INSTANCE.getVertex(neighbor);

            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            neighborVertex.addOrigionEdge(new Node(vertexName, dis));

            // connect neighbor node
            if (clusterName.equals(neighborVertex.getClusterName())) {
                cluster.addClusterLink(vertexName, neighbor, dis);
            } else {
                cluster.addBorderLink(vertexName, neighbor, dis);
                AhgVariable.INSTANCE.getCluster(neighborVertex.getClusterName())
                        .addBorderLink(neighbor, vertexName, dis);
            }
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.AHG;
    }

    /**
     * build full true
     */
    public void buildFullTreeKey() {
        Queue<String> queue = new LinkedList<>(AhgVariable.INSTANCE.getClusterKeySet());
        while (!queue.isEmpty()) {
            String clusterName = queue.poll();
            String parentName;
            while (StringUtils.hasLength(parentName = AhgVariable.INSTANCE.getParentClusterName(clusterName))
                    && !AhgVariable.INSTANCE.containsClusterKey(parentName)) {
                AhgVariable.INSTANCE.addCluster(parentName, null);
                clusterName = parentName;
            }
        }
    }
}
