package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.common.status.AhgActiveStatus;
import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.ahg.common.status.AhgClusterUpdateStatus;
import com.index.indexforknn.ahg.domain.*;
import com.index.indexforknn.ahg.service.build.AhgClusterBuilder;
import com.index.indexforknn.ahg.service.utils.AhgVariableUtil;
import com.index.indexforknn.ahg.service.utils.Trie;
import com.index.indexforknn.base.domain.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AhgClusterService
 * 2022/3/11 zhoutao
 */
@Slf4j
@Service
public class AhgClusterService {

    /**
     * compute clusters
     * parallelStream does not guarantee order, streamp guarantees order
     */
    public void computeClusters() {
        AhgVariableUtil.getClusters().parallelStream().forEach(this::computeCluster);
    }

    public void computeCluster(AhgCluster cluster) {
        floyd(cluster);
        saveBorderNames(cluster);
    }


    /**
     * Floyd
     *
     * @param cluster
     */
    private void floyd(AhgCluster cluster) {
        Set<Integer> clusterVertices = cluster.getClusterLinkMap().keySet();

        for (Integer middle : clusterVertices) {
            for (Integer from : clusterVertices) {

                if (from.equals(middle)) continue;
                int disFromMiddle = cluster.getClusterDis(middle, from);
                if (disFromMiddle == -1) continue;

                for (Integer to : clusterVertices) {
                    if (to <= from) continue;

                    int disMiddleTo = cluster.getClusterDis(middle, to);
                    if (disMiddleTo == -1) {
                        continue;
                    }

                    int dis = cluster.getClusterDis(from, to);
                    if (dis == -1 || dis > (disFromMiddle + disMiddleTo)) {
                        cluster.addClusterLink(from, to, disFromMiddle + disMiddleTo);
                    }
                }
            }
        }
    }

    /**
     * save border info
     *
     * @param cluster cluster
     */
    private void saveBorderNames(AhgCluster cluster) {
        int level = cluster.getLayer();
        Map<Integer, AhgClusterLink> clusterLinkMap = cluster.getClusterLinkMap();

        List<Integer> borderNames = clusterLinkMap.keySet().stream()
                .filter(vertexName -> AhgVariable.vertices.get(vertexName).isBorder(level))
                .collect(Collectors.toList());

        for (int i = 0; i < borderNames.size(); i++) {
            int borderName1 = borderNames.get(i);
            for (int j = i + 1; j < borderNames.size(); j++) {
                int borderName2 = borderNames.get(j);

                int dis = cluster.getClusterDis(borderName1, borderName2);
                if (dis != -1) {
                    clusterLinkMap.get(borderName1).addBorderLink(new Node(borderName2, dis));
                    clusterLinkMap.get(borderName2).addBorderLink(new Node(borderName1, dis));
                }
            }
        }
        cluster.setBorderNames(borderNames);
    }

    /**
     * update dis matrix between active and cluster
     *
     * @param activeInfo   AhgActive
     * @param activeVertex active vertex
     * @param cluster      cluster
     */
    public void updateHighestBorderInfo(AhgActive activeInfo, AhgVertex activeVertex, AhgCluster cluster) {
        Map<String, List<Node>> highestBorderInfo = activeInfo.getHighestBorderInfo();
        String clusterName = cluster.getName();


        // update active info matrix
        if (!highestBorderInfo.containsKey(clusterName)) {
            int level = cluster.getLayer();
            Integer activeName = activeInfo.getName();

            if (activeVertex.isBorder(level)) {
                highestBorderInfo.put(clusterName, Collections.emptyList());
            } else if (cluster.isLeaf() || activeVertex.isBorder(level + 1)) {
                // if the active node is cluster node
                List<Node> thisLayerBorders = new ArrayList<>();
                for (Integer border : cluster.getBorderNames()) {
                    int dis = cluster.getClusterDis(activeName, border);
                    if (dis != -1) {
                        thisLayerBorders.add(new Node(border, dis));
                    }
                }
                highestBorderInfo.put(clusterName, thisLayerBorders);
            } else {

                // if the active node is not cluster node
                String lastLayerName = AhgVariableUtil.getLayerClusterName(activeVertex.getClusterName(), level + 1);
                List<Node> lastLayerBorders = highestBorderInfo.get(lastLayerName);
                if (lastLayerBorders == null) {
                    updateHighestBorderInfo(activeInfo, activeVertex, AhgVariableUtil.getCluster(lastLayerName));
                } else {
                    List<Node> thisLayerBorders = new ArrayList<>();

                    for (Integer borderName : cluster.getBorderNames()) {
                        int dis = -1;
                        for (Node lastNode : lastLayerBorders) {
                            int borderDis = cluster.getClusterDis(borderName, lastNode.getName());
                            if (borderDis == -1) {
                                continue;
                            }
                            if (dis == -1 || (borderDis + lastNode.getDis()) < dis) {
                                dis = borderDis + lastNode.getDis();
                            }
                        }
                        if (dis != -1) {
                            thisLayerBorders.add(new Node(borderName, dis));
                        }
                    }
                    highestBorderInfo.put(clusterName, thisLayerBorders);
                }

            }
        }
        if (cluster.getStatus() == AhgActiveStatus.PARENT_ACTIVE) {
            updateHighestBorderInfo(activeInfo, activeVertex, AhgVariableUtil.getCluster(cluster.getParentName()));
        }
    }

    /**
     * add query into virtual map
     *
     * @param ahgKnn knn
     */
    public void addQuery(AhgKnn ahgKnn) {
        int queryName = ahgKnn.getNearestName();

        AhgVertex queryVertex = AhgVariable.vertices.get(queryName);
        AhgCluster tempCluster = AhgVariable.clusters.get(queryVertex.getClusterName());

        // the active cluster and the cluster which query vertex is a border
        AhgCluster activeCluster = null;
        AhgCluster borderCluster = null;

        while (tempCluster != null && (activeCluster == null)) {
            if (queryVertex.isBorder(tempCluster.getLayer())) {
                borderCluster = tempCluster;
            }
            if (tempCluster.getStatus() == AhgActiveStatus.CURRENT_ACTIVE) {
                activeCluster = tempCluster;
            }
            tempCluster = AhgVariable.clusters.get(tempCluster.getParentName());
        }

        assert activeCluster != null;
        if (borderCluster != null && activeCluster.getName().equals(borderCluster.getName())) {
            return;
        }
        if (activeCluster.isLeaf() ||
                (borderCluster != null && activeCluster.getName().equals(borderCluster.getParentName()))) {
            // active cluster contains query node
            addCurrent(ahgKnn, activeCluster);
        } else {
            List<String> clusterChangedList = new ArrayList<>();
            String queryInClusterName = borderCluster == null ? queryVertex.getClusterName() : borderCluster.getParentName();
            clusterChangedList.add(queryInClusterName);
            String parent = AhgVariableUtil.getCluster(queryInClusterName).getParentName();
            while (true) {
                for (String str : AhgVariableUtil.getCluster(parent).getChildren()) {
                    if (!Objects.equals(str, queryInClusterName)) {
                        clusterChangedList.add(str);
                    }
                }
                if (parent.equals(activeCluster.getName())) {
                    break;
                }
                queryInClusterName = parent;
                parent = AhgVariableUtil.getCluster(parent).getParentName();
            }
            clusterChangedList.parallelStream().forEach(str -> activateCluster(AhgVariableUtil.getCluster(str)));
            addQuery(ahgKnn);
        }
    }

    /**
     * add query in current seach space
     *
     * @param ahgKnn        ahgKnn
     * @param activeCluster activeCluster
     */
    private void addCurrent(AhgKnn ahgKnn, AhgCluster activeCluster) {
        Integer queryName = ahgKnn.getNearestName();
        PriorityQueue<Integer> current = ahgKnn.getCurrent();
        for (Integer borderName : activeCluster.getBorderNames()) {
            int dis = activeCluster.getClusterDis(queryName, borderName);
            if (dis != -1) {
                ahgKnn.queryArray[borderName] = dis;
                current.add(borderName);
            }
        }

        // traverse active nodes
        AhgVertex queryVertex = AhgVariable.vertices.get(queryName);
        int activeLevel = activeCluster.getLayer();
        for (Integer activeName : activeCluster.getActiveNames()) {
            AhgVertex activeVertex = AhgVariable.vertices.get(activeName);
            if (activeVertex.isBorder(activeLevel) ||
                    activeName.equals(queryName)) {
                continue;
            }

            int dis = -1;
            if (activeCluster.isLeaf() || activeVertex.isBorder(activeLevel - 1)) {
                dis = activeCluster.getClusterDis(queryName, activeName);
            } else {
                String sameParentClusterName = StringUtils.
                        getCommonPrefix(activeVertex.getClusterName(), queryVertex.getClusterName());
                // remove last ","
                sameParentClusterName = sameParentClusterName.substring(0, sameParentClusterName.length() - 1);
                AhgCluster sameParentCluster = AhgVariable.clusters.get(sameParentClusterName);
                while (sameParentCluster != null) {
                    if (sameParentCluster.getClusterLinkMap().containsKey(queryName)
                            && sameParentCluster.getClusterLinkMap().containsKey(activeName)) {
                        if (dis == -1 || dis > sameParentCluster.getClusterDis(queryName, activeName)) {
                            dis = sameParentCluster.getClusterDis(queryName, activeName);
                        }
                    } else {
                        break;
                    }
                    sameParentClusterName = sameParentCluster.getParentName();
                    sameParentCluster = AhgVariable.clusters.get(sameParentClusterName);
                }
                String sonClusterName = activeCluster.getName() +
                        AhgConstants.CLUSTER_NAME_SUFFIX + activeVertex.getClusterNames()[activeLevel + 1];

                Map<String, List<Node>> highestBorderInfo = activeVertex.getActiveInfo().getHighestBorderInfo();
                if (!highestBorderInfo.containsKey(sonClusterName)) {
                    updateHighestBorderInfo(activeVertex.getActiveInfo(), activeVertex, AhgVariable.clusters.get(sonClusterName));
                }
                for (Node node : highestBorderInfo.get(sonClusterName)) {
                    int curDis = activeCluster.getClusterDis(queryName, node.getName());
                    if (curDis != -1 && (dis == -1 || dis > (curDis + node.getDis()))) {
                        dis = curDis + node.getDis();
                    }
                }
            }
            if (dis != -1) {
                ahgKnn.queryArray[activeName] = dis;
                current.add(activeName);
            }

        }
    }

    /**
     * remove active vertex
     */
    public void removeActive(AhgVertex vertex) {
        AhgCluster activeCluster = AhgVariable.clusters.get(vertex.getActiveInfo().getCurrentClusterName()),
                leafCluster = AhgVariable.clusters.get(vertex.getClusterName());
        Integer activeName = vertex.getName();
        if (activeCluster != null) {
            activeCluster.removeActiveVirtualLink(vertex);
        }
        vertex.getActiveInfo().setCurrentClusterName(null);
        leafCluster.removeActive(activeName);
    }

    /**
     * update active clusters
     *
     * @param clusterNames influenced active cluster names
     */
    public void updateActiveClusters(Set<String> clusterNames) {
        Trie trie = new Trie();

        for (String activeClusterName : clusterNames) {
            if (trie.isProcessed(activeClusterName)) {
                continue;
            }
            AhgCluster activeCluster = AhgVariableUtil.getCluster(activeClusterName);
            AhgClusterUpdateStatus suitStatus = activeCluster.isSuitableCluster();
            if (suitStatus == AhgClusterUpdateStatus.SUITABLE) {
                continue;
            }
            List<AhgCluster> activeClusters;

            if (suitStatus == AhgClusterUpdateStatus.NEED_TO_MERGE) {
                do {
                    activeClusterName = activeCluster.getParentName();
                    if (!AhgVariableUtil.containsClusterValue(activeClusterName)) {
                        AhgVariableUtil.addCluster(activeClusterName,
                                AhgClusterBuilder.build(activeClusterName, false));
                    }
                    activeCluster = AhgVariableUtil.getCluster(activeClusterName);
                    suitStatus = activeCluster.isSuitableCluster();
                } while (suitStatus == AhgClusterUpdateStatus.NEED_TO_MERGE);

                activeClusters = Collections.singletonList(activeCluster);

            } else {
                activeClusters = new ArrayList<>();
                Queue<AhgCluster> clusters = new LinkedList<>();
                clusters.add(activeCluster);
                while (!clusters.isEmpty()) {
                    AhgCluster cluster = clusters.poll();
                    if (cluster.isSuitableCluster() != AhgClusterUpdateStatus.NEED_TO_SPLIT) {
                        activeClusters.add(cluster);
                    } else {
                        clusters.addAll(cluster.getChildren().stream()
                                .map(AhgVariableUtil::getCluster)
                                .collect(Collectors.toList()));
                    }
                }
            }
            for (AhgCluster cluster : activeClusters) {
                activateCluster(cluster);
                trie.insert(cluster.getName());
            }
        }
    }

    public void activateCluster(AhgCluster activeCluster) {
        activeCluster.updateActiveState(activeCluster.getStatus());
        activeCluster.setStatus(AhgActiveStatus.CURRENT_ACTIVE);

        Map<Integer, AhgClusterLink> clusterLinkMap = activeCluster.getClusterLinkMap();
        String activeClusterName = activeCluster.getName();

        for (int activeName : activeCluster.getActiveNames()) {
            AhgVertex activeVertex = AhgVariableUtil.getVertex(activeName);
            AhgActive activeInfo = activeVertex.getActiveInfo();
            updateHighestBorderInfo(activeInfo, activeVertex, activeCluster);

            String currentName = activeInfo.getCurrentClusterName();
            if (currentName != null) {
                AhgCluster currentCluster = AhgVariableUtil.getCluster(currentName);
                if (currentName.equals(activeClusterName)) {
                    continue;
                }
                currentCluster.removeActiveVirtualLink(activeVertex);
            }
            activeInfo.setCurrentClusterName(activeClusterName);

            if (!activeInfo.getHighestBorderInfo().containsKey(activeClusterName)) {
                updateHighestBorderInfo(activeInfo, activeVertex, activeCluster);
            }
            activeInfo.getHighestBorderInfo().get(activeClusterName).
                    forEach(node -> clusterLinkMap.get(node.getName()).
                            addActiveLink(new Node(activeName, node.getDis())));
        }

        for (int borderName : activeCluster.getBorderNames()) {
            AhgVariableUtil.getVertex(borderName).
                    buildVirtualMap(clusterLinkMap.get(borderName).getBorderLink(), activeClusterName);
        }

    }


}
