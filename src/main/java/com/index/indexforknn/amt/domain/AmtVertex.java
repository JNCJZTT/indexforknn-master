package com.index.indexforknn.amt.domain;

import com.index.indexforknn.amt.common.status.AmtActiveStatus;
import com.index.indexforknn.base.common.constants.Constants;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.Vertex;
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
public class AmtVertex extends Vertex {

    private boolean[] border;

    private String[] clusterNames;

    private AmtActive activeInfo;

    // virtual link
    private Set<Node> virtualLink;

    // active node saved in cluster
    private String activeClusterName;

    public AmtVertex() {
        virtualLink = new HashSet<>();
    }

    @Override
    public void setClusterName(String clusterName) {
        super.setClusterName(clusterName);
        clusterNames = clusterName.split(Constants.CLUSTER_NAME_SUFFIX);
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
                && AmtVariable.INSTANCE.getCluster(activeClusterName).getStatus() == AmtActiveStatus.CURRENT_ACTIVE) {
            return true;
        }
        return false;
    }
}
