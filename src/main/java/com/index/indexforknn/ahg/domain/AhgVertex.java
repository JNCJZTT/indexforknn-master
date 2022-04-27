package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.ahg.common.status.AhgActiveStatus;
import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.Vertex;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * AhgVertex
 * 2022/2/10 zhoutao
 */
@Getter
@Setter
@Slf4j
public class AhgVertex extends Vertex {

    private boolean[] border;

    private String[] clusterNames;

    private AhgActive activeInfo;

    // virtual link
    private Set<Node> virtualLink = new HashSet<>();

    // active node saved in cluster
    private String activeClusterName;

    @Override
    public void setClusterName(String clusterName) {
        super.setClusterName(clusterName);
        clusterNames = clusterName.split(AhgConstants.CLUSTER_NAME_SUFFIX);
        border = new boolean[clusterNames.length];
        Arrays.fill(border, false);
    }

    /**
     * isBorder
     *
     * @param layer layer
     */
    public boolean isBorder(int layer) {
        return border[layer];
    }

    /**
     * build virtual map(border)
     *
     * @param virtualLink       virtual edge
     * @param activeClusterName current active clusterName
     */
    public void buildVirtualMap(Set<Node> virtualLink, String activeClusterName) {
        this.activeClusterName = activeClusterName;
        this.virtualLink = virtualLink;
    }

    /**
     * isVirtualMapBorderNode
     *
     * @return return
     */
    public boolean isVirtualMapBorderNode() {
        if (activeClusterName != null
                && AhgVariable.clusters.get(activeClusterName).getStatus() == AhgActiveStatus.CURRENT_ACTIVE) {
            return true;
        }
        return false;
    }
}
