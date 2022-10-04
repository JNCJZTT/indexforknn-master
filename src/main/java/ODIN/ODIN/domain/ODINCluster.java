package ODIN.ODIN.domain;

import ODIN.ODIN.common.status.ODINActiveStatus;
import ODIN.ODIN.common.status.ODINClusterUpdateStatus;
import ODIN.base.domain.api.Cluster;
import ODIN.base.domain.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * ODINCluster
 * 2022/2/12 zhoutao
 */
@Getter
@Setter
@SuperBuilder
@Slf4j
public class ODINCluster extends Cluster<ODINClusterLink> {

    private boolean leaf;

    private int layer;

    private ODINActiveStatus status;

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
        clusterLinkMap.put(name, new ODINClusterLink());
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
        if (ODINVariable.INSTANCE.containsClusterValue(parentName)) {
            ODINVariable.INSTANCE.getCluster(parentName).addActiveName(activeName);
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
        if (ODINVariable.INSTANCE.containsClusterValue(parentName)) {
            ODINVariable.INSTANCE.getCluster(parentName).removeActive(activeName);
        }
    }

    /**
     * isSuitableCluster
     *
     * @return ODINClusterUpdateStatus
     */
    public ODINClusterUpdateStatus isSuitableCluster() {
        int activeSize = activeNames.size();

        if (parentName != null && activeSize < ODINVariable.INSTANCE.getLeastActiveNum()) {
            return ODINClusterUpdateStatus.NEED_TO_MERGE;
        }
        if (!leaf && activeSize > ODINVariable.INSTANCE.getMostActiveNum()) {
            return ODINClusterUpdateStatus.NEED_TO_SPLIT;
        }
        return ODINClusterUpdateStatus.SUITABLE;
    }

    /**
     * update cluster active status
     *
     * @param updateState updateState
     */
    public void updateActiveState(ODINActiveStatus updateState) {

        if (updateState == ODINActiveStatus.PARENT_ACTIVE && ODINVariable.INSTANCE.containsClusterValue(parentName)) {
            ODINCluster cluster = ODINVariable.INSTANCE.getCluster(parentName);
            if (cluster.status != ODINActiveStatus.SON_ACTIVE) {
                cluster.setStatus(ODINActiveStatus.SON_ACTIVE);
                cluster.updateActiveState(updateState);
            }
        } else if (updateState == ODINActiveStatus.SON_ACTIVE) {
            for (String sonName : children) {
                ODINCluster cluster = ODINVariable.INSTANCE.getCluster(sonName);
                if (cluster.status != ODINActiveStatus.PARENT_ACTIVE) {
                    cluster.setStatus(ODINActiveStatus.PARENT_ACTIVE);
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
    public void removeActiveVirtualLink(ODINVertex vertex) {
        if (vertex.isBorder(layer)) {
            return;
        }
        borderNames.parallelStream()
                .forEach(borderName -> clusterLinkMap.get(borderName).removeBorderLink(vertex.getName()));
    }


}
