package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.ahg.common.status.AhgActiveStatus;
import com.index.indexforknn.ahg.common.status.AhgClusterUpdateStatus;
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
public class AhgCluster extends Cluster<AhgClusterLink> {

    private boolean leaf;

    private int layer;

    private AhgActiveStatus status;

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
        clusterLinkMap.put(name, new AhgClusterLink());
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
        if (AhgVariable.INSTANCE.containsClusterValue(parentName)) {
            AhgVariable.INSTANCE.getCluster(parentName).addActiveName(activeName);
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
        if (AhgVariable.INSTANCE.containsClusterValue(parentName)) {
            AhgVariable.INSTANCE.getCluster(parentName).removeActive(activeName);
        }
    }

    /**
     * isSuitableCluster
     *
     * @return AhgClusterUpdateStatus
     */
    public AhgClusterUpdateStatus isSuitableCluster() {
        int activeSize = activeNames.size();

        if (parentName != null && activeSize < AhgVariable.INSTANCE.getLeastActiveNum()) {
            return AhgClusterUpdateStatus.NEED_TO_MERGE;
        }
        if (!leaf && activeSize > AhgVariable.INSTANCE.getMostActiveNum()) {
            return AhgClusterUpdateStatus.NEED_TO_SPLIT;
        }
        return AhgClusterUpdateStatus.SUITABLE;
    }

    /**
     * update cluster active status
     *
     * @param updateState updateState
     */
    public void updateActiveState(AhgActiveStatus updateState) {

        if (updateState == AhgActiveStatus.PARENT_ACTIVE && AhgVariable.INSTANCE.containsClusterValue(parentName)) {
            AhgCluster cluster = AhgVariable.INSTANCE.getCluster(parentName);
            if (cluster.status != AhgActiveStatus.SON_ACTIVE) {
                cluster.setStatus(AhgActiveStatus.SON_ACTIVE);
                cluster.updateActiveState(updateState);
            }
        } else if (updateState == AhgActiveStatus.SON_ACTIVE) {
            for (String sonName : children) {
                AhgCluster cluster = AhgVariable.INSTANCE.getCluster(sonName);
                if (cluster.status != AhgActiveStatus.PARENT_ACTIVE) {
                    cluster.setStatus(AhgActiveStatus.PARENT_ACTIVE);
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
    public void removeActiveVirtualLink(AhgVertex vertex) {
        if (vertex.isBorder(layer)) {
            return;
        }
        borderNames.parallelStream()
                .forEach(borderName -> clusterLinkMap.get(borderName).removeBorderLink(vertex.getName()));
    }


}
