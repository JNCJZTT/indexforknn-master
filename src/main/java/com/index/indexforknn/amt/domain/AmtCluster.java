package com.index.indexforknn.amt.domain;

import com.index.indexforknn.amt.common.status.AmtActiveStatus;
import com.index.indexforknn.amt.common.status.AmtClusterUpdateStatus;
import com.index.indexforknn.base.domain.api.Cluster;
import com.index.indexforknn.base.domain.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * AhgCluster
 * 2022/2/12 zhoutao
 */
@Getter
@Setter
@SuperBuilder
@Slf4j
public class AmtCluster extends Cluster<AmtClusterLink> {

    private boolean leaf;

    private int layer;

    private AmtActiveStatus status;

    private String parentName;

    private List<String> children;

    private List<Integer> borderNames;

    private Set<Integer> activeNames;

    /**
     * add vertex into cluster
     *
     * @param name name
     */
    public void addVertex(int name) {
        clusterLinkMap.put(name, new AmtClusterLink());
    }

    /**
     * add vertices into cluster
     *
     * @param names names
     */
    public void addVertices(List<Integer> names) {
        names.forEach(this::addVertex);
    }

    /**
     * add cluster border link
     *
     * @param from from
     * @param to   to
     * @param dis  dis
     */
    public void addBorderLink(int from, int to, int dis) {
        clusterLinkMap.get(from).addBorderLink(new Node(to, dis));
    }

    /**
     * add active name
     *
     * @param activeName active name
     */
    public void addActiveName(Integer activeName) {
        if (activeNames.contains(activeName)) {
            return;
        }
        activeNames.add(activeName);

        // if the parent cluster is built,add active into parent cluster
        if (AmtVariable.INSTANCE.containsClusterValue(parentName)) {
            AmtVariable.INSTANCE.getCluster(parentName).addActiveName(activeName);
        }
    }

    /**
     * add active names
     *
     * @param activeNames activeNames
     */
    public void addActiveNames(Set<Integer> activeNames) {
        this.activeNames.addAll(activeNames);
    }

    public void removeActive(Integer activeName) {
        if (!activeNames.contains(activeName)) {
            return;
        }
        activeNames.remove(activeName);
        if (AmtVariable.INSTANCE.containsClusterValue(parentName)) {
            AmtVariable.INSTANCE.getCluster(parentName).removeActive(activeName);
        }
    }

    /**
     * isSuitableCluster
     *
     * @return AhgClusterUpdateStatus
     */
    public AmtClusterUpdateStatus isSuitableCluster() {
        int activeSize = activeNames.size();

        if (parentName != null && activeSize < AmtVariable.INSTANCE.getLeastActiveNum()) {
            return AmtClusterUpdateStatus.NEED_TO_MERGE;
        }
        if (!leaf && activeSize > AmtVariable.INSTANCE.getMostActiveNum()) {
            return AmtClusterUpdateStatus.NEED_TO_SPLIT;
        }
        return AmtClusterUpdateStatus.SUITABLE;
    }

    /**
     * update cluster active status
     *
     * @param updateState updateState
     */
    public void updateActiveState(AmtActiveStatus updateState) {

        if (updateState == AmtActiveStatus.PARENT_ACTIVE && AmtVariable.INSTANCE.containsClusterValue(parentName)) {
            AmtCluster cluster = AmtVariable.INSTANCE.getCluster(parentName);
            if (cluster.status != AmtActiveStatus.SON_ACTIVE) {
                cluster.setStatus(AmtActiveStatus.SON_ACTIVE);
                cluster.updateActiveState(updateState);
            }
        } else if (updateState == AmtActiveStatus.SON_ACTIVE) {
            for (String sonName : children) {
                AmtCluster cluster = AmtVariable.INSTANCE.getCluster(sonName);
                if (cluster.status != AmtActiveStatus.PARENT_ACTIVE) {
                    cluster.setStatus(AmtActiveStatus.PARENT_ACTIVE);
                    if (!cluster.isLeaf()) {
                        cluster.updateActiveState(updateState);
                    }
                }
            }
        }
    }

    /**
     * removeActiveVirtualLink
     *
     * @param vertex activeVertex
     */
    public void removeActiveVirtualLink(AmtVertex vertex) {
        if (vertex.isBorder(layer)) {
            return;
        }
        borderNames.parallelStream()
                .forEach(borderName -> clusterLinkMap.get(borderName).removeBorderLink(vertex.getName()));
    }


}
