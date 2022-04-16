package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.annotation.CostTime;
import com.index.indexforknn.base.domain.enumeration.TimeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * TODO
 * 2022/3/12 zhoutao
 */
@Slf4j
@Service
public class AhgVertexService {

    /**
     * 构建边界点
     */
    @CostTime(msg = "构建边界点", timeType = TimeType.MilliSecond)
    public void buildBorders() {
        AhgVariable.vertices.forEach(this::buildBorder);
    }

    /**
     * 单个结点构建边界点
     */
    public void buildBorder(AhgVertex vertex) {
        String[] clusterNames = vertex.getClusterNames();

        int minIndex = clusterNames.length;
        // 遍历邻居节点
        for (Node node : vertex.getOrigionEdges()) {
            AhgVertex neighborVertex = AhgVariable.vertices.get(node.getName());
            String[] neighborClusterNames = neighborVertex.getClusterNames();

            int minLen = Math.min(clusterNames.length, neighborClusterNames.length);
            // 判断第 i 层是否是边界点
            for (int i = 0; i < minLen; i++) {
                if (!clusterNames[i].equals(neighborClusterNames[i])) {
                    minIndex = i;
                    break;
                }
            }
        }
        Arrays.fill(vertex.getBorder(), minIndex, clusterNames.length, true);
    }

}
