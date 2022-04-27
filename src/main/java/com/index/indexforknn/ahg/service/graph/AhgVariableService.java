package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.ahg.service.build.AhgClusterBuilder;
import com.index.indexforknn.ahg.service.utils.AhgVariableUtil;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.base.service.dto.IndexDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AhgVertexService vertexService;

    public AhgVariableService() {
        register();
    }

    @Override
    public void initVariable(IndexDTO ahgIndex) {
        AhgVariable.LEAST_ACTIVE_NUM = ahgIndex.getLeastActiveNum();
        AhgVariable.MOST_ACTIVE_NUM = AhgConstants.MULTIPLE_LEAST_ACTIVE_NUM * AhgVariable.LEAST_ACTIVE_NUM;

        AhgVariable.vertices = new ArrayList<>(GlobalVariable.MAP_INFO.getSize());
        AhgVariable.clusters = new HashMap<>(GlobalVariable.MAP_INFO.getSize() / ahgIndex.getSubGraphSize());

        AhgVariable.DIGIT = String.valueOf(ahgIndex.getBranch()).length();
    }

    @Override
    public void buildVertex(int vertexName, String clusterName) {
        // build leaf cluster
        if (!AhgVariableUtil.containsClusterKey(clusterName)) {
            AhgVariableUtil.addCluster(clusterName, AhgClusterBuilder.build(clusterName, true));
        }

        // build vertex
        AhgVertex vertex = new AhgVertex();
        vertex.setName(vertexName);
        vertex.setClusterName(clusterName);
        AhgVariableUtil.addVertex(vertex);

        // add vertex into cluster
        AhgVariableUtil.getCluster(clusterName).addVertex(vertexName);
    }

    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {
        AhgVertex vertex = AhgVariableUtil.getVertex(vertexName);
        String clusterName = vertex.getClusterName();
        AhgCluster cluster = AhgVariableUtil.getCluster(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);

            // Only save one - way edge.
            // Need to be modified if it is a directed graph
            if (neighbor < vertexName) continue;

            AhgVertex neighborVertex = AhgVariableUtil.getVertex(neighbor);

            // add Origion Edge
            vertex.addOrigionEdge(new Node(neighbor, dis));
            neighborVertex.addOrigionEdge(new Node(vertexName, dis));

            // connect neighbor node
            if (clusterName.equals(neighborVertex.getClusterName())) {
                cluster.addClusterLink(vertexName, neighbor, dis);
            } else {
                cluster.addBorderLink(vertexName, neighbor, dis);
                AhgVariableUtil.getCluster(neighborVertex.getClusterName())
                        .addBorderLink(neighbor, vertexName, dis);
            }
        }
    }

    @Override
    public List getVertices() {
        return AhgVariable.vertices;
    }

    @Override
    public IndexType supportType() {
        return IndexType.AHG;
    }


    @Override
    public int getVertexSize() {
        return AhgVariable.vertices.size();
    }

    @Override
    public int getClusterSize() {
        return AhgVariable.clusters.size();
    }

    /**
     * build full true
     */
    public void buildFullTreeKey() {
        Queue<String> queue = new LinkedList<>(AhgVariableUtil.getClusterKeySet());
        while (!queue.isEmpty()) {
            String clusterName = queue.poll();
            String parentName;
            // 当parentName存在，且clusters不存在parentName时，向上递归
            while (StringUtils.hasLength(parentName = AhgVariableUtil.getParentClusterName(clusterName))
                    && !AhgVariableUtil.containsClusterKey(parentName)) {
                AhgVariableUtil.addCluster(parentName, null);
                clusterName = parentName;
            }
        }
    }
}
