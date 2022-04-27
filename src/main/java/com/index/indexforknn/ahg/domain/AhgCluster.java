package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.ahg.common.status.AhgActiveStatus;
import com.index.indexforknn.ahg.common.status.AhgClusterUpdateStatus;
import com.index.indexforknn.ahg.service.utils.AhgVariableUtil;
import com.index.indexforknn.base.domain.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * AhgCluster
 * 2022/2/12 zhoutao
 */
@Getter
@Setter
@Accessors(chain = true)
@Slf4j
public class AhgCluster {

    private String name;

    private boolean leaf;

    private int layer;

    private AhgActiveStatus status;

    private String parentName;

    private List<String> children;

    private Map<Integer, AhgClusterLink> clusterLinkMap;

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
     * add cluster edge
     * Only saved one-way edges
     * Need to be modifed if the graph is directed
     *
     * @param from from
     * @param to   to
     * @param dis  dis
     */
    public void addClusterLink(Integer from, Integer to, Integer dis) {
        if (from.equals(to)) {
            return;
        }
        if (from < to) {
            clusterLinkMap.get(from).addClusterLink(to, dis);
        } else {
            clusterLinkMap.get(to).addClusterLink(from, dis);
        }
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
     * get cluster dis
     *
     * @param from from
     * @param to   to
     * @return the dis between from and to
     */
    public int getClusterDis(Integer from, Integer to) {
        if (from.equals(to)) {
            return 0;
        }
        if (from > to) {
            Integer temp = from;
            from = to;
            to = temp;
        }
        return clusterLinkMap.get(from).getClusterDis(to);
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
        if (AhgVariableUtil.containsClusterValue(parentName)) {
            AhgVariableUtil.getCluster(parentName).addActiveName(activeName);
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
        if (AhgVariableUtil.containsClusterValue(parentName)) {
            AhgVariableUtil.getCluster(parentName).removeActive(activeName);
        }
    }

    /**
     * isSuitableCluster
     *
     * @return AhgClusterUpdateStatus
     */
    public AhgClusterUpdateStatus isSuitableCluster() {
        int activeSize = activeNames.size();

        if (parentName != null && activeSize < AhgVariable.LEAST_ACTIVE_NUM) {
            return AhgClusterUpdateStatus.NEED_TO_MERGE;
        }
        if (!leaf && activeSize > AhgVariable.MOST_ACTIVE_NUM) {
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

        if (updateState == AhgActiveStatus.PARENT_ACTIVE && AhgVariableUtil.containsClusterValue(parentName)) {
            AhgCluster cluster = AhgVariableUtil.getCluster(parentName);
            if (cluster.status != AhgActiveStatus.SON_ACTIVE) {
                cluster.setStatus(AhgActiveStatus.SON_ACTIVE);
                cluster.updateActiveState(updateState);
            }
        } else if (updateState == AhgActiveStatus.SON_ACTIVE) {
            for (String sonName : children) {
                AhgCluster cluster = AhgVariableUtil.getCluster(sonName);
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
