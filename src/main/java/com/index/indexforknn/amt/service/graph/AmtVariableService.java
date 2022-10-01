package com.index.indexforknn.amt.service.graph;

import com.index.indexforknn.amt.domain.AmtCluster;
import com.index.indexforknn.amt.domain.AmtVertex;
import com.index.indexforknn.amt.service.build.AmtClusterBuilder;
import com.index.indexforknn.amt.domain.AmtVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IVariableService;
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
public class AmtVariableService implements IVariableService {

    public AmtVariableService() {
        register();
    }

//    @Override
//    public void initVariable(IndexDTO ahgIndex) {
//        AhgVariable.INSTANCE.initVariables(ahgIndex);
//    }

    @Override
    public void buildVertex(int vertexName, String clusterName) {
        // build leaf cluster
        if (!AmtVariable.INSTANCE.containsClusterKey(clusterName)) {
            AmtVariable.INSTANCE.addCluster(clusterName, AmtClusterBuilder.build(clusterName, true));
        }

        // build vertex
        AmtVertex vertex = new AmtVertex();
        vertex.setName(vertexName);
        vertex.setClusterName(clusterName);
        AmtVariable.INSTANCE.addVertex(vertex);

        // add vertex into cluster
        AmtVariable.INSTANCE.getCluster(clusterName).addVertex(vertexName);
    }

    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {
        AmtVertex vertex = AmtVariable.INSTANCE.getVertex(vertexName);
        String clusterName = vertex.getClusterName();
        AmtCluster cluster = AmtVariable.INSTANCE.getCluster(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);

            // Only save one - way edge.
            // Need to be modified if it is a directed graph
            if (neighbor < vertexName) continue;

            AmtVertex neighborVertex = AmtVariable.INSTANCE.getVertex(neighbor);

            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            neighborVertex.addOrigionEdge(new Node(vertexName, dis));

            // connect neighbor node
            if (clusterName.equals(neighborVertex.getClusterName())) {
                cluster.addClusterLink(vertexName, neighbor, dis);
            } else {
                cluster.addBorderLink(vertexName, neighbor, dis);
                AmtVariable.INSTANCE.getCluster(neighborVertex.getClusterName())
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
        Queue<String> queue = new LinkedList<>(AmtVariable.INSTANCE.getClusterKeySet());
        while (!queue.isEmpty()) {
            String clusterName = queue.poll();
            String parentName;
            while (StringUtils.hasLength(parentName = AmtVariable.INSTANCE.getParentClusterName(clusterName))
                    && !AmtVariable.INSTANCE.containsClusterKey(parentName)) {
                AmtVariable.INSTANCE.addCluster(parentName, null);
                clusterName = parentName;
            }
        }
    }
}
