package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.ahg.service.utils.AhgUtil;
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
 * TODO
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


    public void initVariable(IndexDTO ahgIndex) {
        AhgVariable.LEAST_ACTIVE_NUM = ahgIndex.getLeastActiveNum();
        AhgVariable.MOST_ACTIVE_NUM = AhgConstants.MULTIPLE_LEAST_ACTIVE_NUM * AhgVariable.LEAST_ACTIVE_NUM;

        AhgVariable.vertices = new ArrayList<>(GlobalVariable.MAP_INFO.getSize());
        AhgVariable.clusters = new HashMap<>(GlobalVariable.MAP_INFO.getSize() / ahgIndex.getSubGraphSize());
    }


    /**
     * 初始化AhgIndex变量
     */
//    public void initVariable(AhgIndexDTO ahgIndex) {
//        // 初始化全局变量
//        GlobalVariable.initGlobalVariable(ahgIndex);
//
//        AhgVariable.LEAST_ACTIVE_NUM = ahgIndex.getLeastActiveNum();
//        AhgVariable.MOST_ACTIVE_NUM = AhgConstants.MULTIPLE_LEAST_ACTIVE_NUM * AhgVariable.LEAST_ACTIVE_NUM;
//
//        vertices = new ArrayList<>(GlobalVariable.MAP_INFO.getSize());
//        clusters = new HashMap<>(GlobalVariable.MAP_INFO.getSize() / ahgIndex.getSubGraphSize());
//    }


    /**
     * 创建结点，并将结点添加到叶子簇中
     */
    @Override
    public void buildVertex(int vertexName, String clusterName) {
        // 如果该簇还没创建，则创建该叶子簇
        if (!AhgVariable.INSTANCE.containsClusterValue(clusterName)) {
            AhgVariable.INSTANCE.addCluster(clusterName, new AhgCluster(clusterName, true));
        }

        // 创建结点
        AhgVertex vertex = new AhgVertex();
        vertex.setName(vertexName);
        vertex.setClusterName(clusterName);

        // 添加结点，并将结点添加到叶子簇中去
        AhgVariable.vertices.add(vertex);
        AhgVariable.clusters.get(clusterName).addVertex(vertexName);
    }

    /**
     * 构建边，添加原始道路与簇内和簇外的的边
     */
    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {
        AhgVertex vertex = AhgVariable.vertices.get(vertexName);
        String clusterName = vertex.getClusterName();
        AhgCluster cluster = AhgVariable.clusters.get(clusterName);

        for (int j = 0; j < edgeInfo.length; j += 2) {
            int neighbor = Integer.parseInt(edgeInfo[j]), dis = Integer.parseInt(edgeInfo[j + 1]);
            // 双向边，剪枝
            if (neighbor < vertexName) continue;

            AhgVertex neighborVertex = AhgVariable.vertices.get(neighbor);

            // 添加原始道路结点
            vertex.addOrigionEdge(new Node(neighbor, dis));
            neighborVertex.addOrigionEdge(new Node(vertexName, dis));

            // 和邻居结点在同一个簇，则添加并相连，如果不同簇，则添加边界
            if (clusterName.equals(neighborVertex.getClusterName())) {
                cluster.addClusterLink(vertexName, neighbor, dis);
            } else {
                cluster.addBorderLink(vertexName, neighbor, dis);
                AhgVariable.clusters.get(neighborVertex.getClusterName())
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


}
