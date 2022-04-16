package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.ahg.service.graph.AhgVariableService;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.Vertex;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 * 2022/2/10 zhoutao
 */
@Getter
@Setter
public class AhgVertex extends Vertex {

    private boolean[] border;

    private String[] clusterNames;

    private AhgActive activeInfo;

    // 虚拟路网
    private Set<Node> virtualLink = new HashSet<>();

    @Override
    public void setClusterName(String clusterName) {
        super.setClusterName(clusterName);
        clusterNames = clusterName.split(AhgConstants.CLUSTER_NAME_SUFFIX);
        border = new boolean[clusterNames.length];
        Arrays.fill(border, false);
    }

    public void buildBorder() {
        // 遍历邻居节点
        for (Node node : getOrigionEdges()) {
            AhgVertex neighborVertex = AhgVariable.INSTANCE.getVertex(node.getName());
            String[] neighborClusterNames = neighborVertex.getClusterNames();
            // 判断第 i 层是否是边界点
            for (int i = 0; i < clusterNames.length && i < neighborClusterNames.length; i++) {
                if (!clusterNames[i].equals(neighborClusterNames[i])) {
                    setBorder(i);
                    neighborVertex.setBorder(i);
                }
            }
        }
    }

    public void setBorder(int level) {
        border[level] = true;
    }

    public boolean isBorder(int level) {
        return border[level];
    }

    public void buildVirtualMap(Set<Node> virtualLink) {
        this.virtualLink = virtualLink;
    }

    public String getLayerClusterName(int layer) {
        return clusterNames[layer];
    }
}
