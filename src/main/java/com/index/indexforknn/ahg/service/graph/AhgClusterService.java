package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.common.status.AhgActiveStatus;
import com.index.indexforknn.ahg.common.status.AhgClusterUpdateStatus;
import com.index.indexforknn.ahg.domain.*;
import com.index.indexforknn.ahg.service.build.AhgClusterBuilder;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.service.utils.Trie;
import com.index.indexforknn.base.common.constants.Constants;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.service.utils.DisComputerUtil;
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
//        long buildStartTime = System.nanoTime();
        AhgVariable.INSTANCE.getClusters().parallelStream().forEach(this::computeCluster);
//        long computerClustersTime = System.nanoTime() - buildStartTime;
//        System.out.println("the time of building borders : " + (float) (computerClustersTime) / 1000_000 + "ms");
    }

    public void computeCluster(AhgCluster cluster) {
        DisComputerUtil.floyd(cluster);
        saveBorderNames(cluster);
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
                .filter(vertexName -> AhgVariable.INSTANCE.getVertex(vertexName).isBorder(level))
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
            Integer activeName = activeVertex.getName();

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
                String lastLayerName = AhgVariable.INSTANCE.getLayerClusterName(activeVertex.getClusterName(), level + 1);
                List<Node> lastLayerBorders = highestBorderInfo.get(lastLayerName);
                if (lastLayerBorders == null) {
                    updateHighestBorderInfo(activeInfo, activeVertex, AhgVariable.INSTANCE.getCluster(lastLayerName));
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
            updateHighestBorderInfo(activeInfo, activeVertex, AhgVariable.INSTANCE.getCluster(cluster.getParentName()));
        }
    }

    /**
     * add query into virtual map
     *
     * @param ahgKnn knn
     */
    public void addQuery(AhgKnn ahgKnn) {
        int queryName = ahgKnn.getNearestName();

        AhgVertex queryVertex = AhgVariable.INSTANCE.getVertex(queryName);
        AhgCluster tempCluster = AhgVariable.INSTANCE.getCluster(queryVertex.getClusterName());

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
            tempCluster = AhgVariable.INSTANCE.getCluster(tempCluster.getParentName());
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
            String parent = AhgVariable.INSTANCE.getCluster(queryInClusterName).getParentName();
            while (true) {
                for (String str : AhgVariable.INSTANCE.getCluster(parent).getChildren()) {
                    if (!Objects.equals(str, queryInClusterName)) {
                        clusterChangedList.add(str);
                    }
                }
                if (parent.equals(activeCluster.getName())) {
                    break;
                }
                queryInClusterName = parent;
                parent = AhgVariable.INSTANCE.getCluster(parent).getParentName();
            }
            clusterChangedList.parallelStream().forEach(str -> activateCluster(AhgVariable.INSTANCE.getCluster(str)));
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
        AhgVertex queryVertex = AhgVariable.INSTANCE.getVertex(queryName);
        int activeLevel = activeCluster.getLayer();
        for (Integer activeName : activeCluster.getActiveNames()) {
            AhgVertex activeVertex = AhgVariable.INSTANCE.getVertex(activeName);
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
                AhgCluster sameParentCluster = AhgVariable.INSTANCE.getCluster(sameParentClusterName);
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
                    sameParentCluster = AhgVariable.INSTANCE.getCluster(sameParentClusterName);
                }
                String sonClusterName = activeCluster.getName() +
                        Constants.CLUSTER_NAME_SUFFIX + activeVertex.getClusterNames()[activeLevel + 1];

                Map<String, List<Node>> highestBorderInfo = activeVertex.getActiveInfo().getHighestBorderInfo();
                if (!highestBorderInfo.containsKey(sonClusterName)) {
                    updateHighestBorderInfo(activeVertex.getActiveInfo(), activeVertex, AhgVariable.INSTANCE.getCluster(sonClusterName));
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
        AhgCluster activeCluster = AhgVariable.INSTANCE.getCluster(vertex.getActiveInfo().getCurrentClusterName()),
                leafCluster = AhgVariable.INSTANCE.getCluster(vertex.getClusterName());
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
     * @param isFirst      is the first built
     */
    public void updateActiveClusters(Set<String> clusterNames, boolean isFirst) {
        Trie trie = new Trie();

        for (String activeClusterName : clusterNames) {
//            System.out.println("activeClusterName******************************");
            if (trie.isProcessed(activeClusterName)) {
                continue;
            }
            AhgCluster activeCluster = AhgVariable.INSTANCE.getCluster(activeClusterName);
            AhgClusterUpdateStatus suitStatus = activeCluster.isSuitableCluster();

            List<AhgCluster> activeClusters;
            if (suitStatus == AhgClusterUpdateStatus.SUITABLE) {
                if (!isFirst) {
                    continue;
                }
                activeClusters = Collections.singletonList(activeCluster);
            } else if (suitStatus == AhgClusterUpdateStatus.NEED_TO_MERGE) {
                do {
//                    System.out.println("NEED_TO_MERGE");
                    activeClusterName = activeCluster.getParentName();
                    if (!AhgVariable.INSTANCE.containsClusterValue(activeClusterName)) {
                        AhgVariable.INSTANCE.addCluster(activeClusterName,
                                AhgClusterBuilder.build(activeClusterName, false));
                    }
                    activeCluster = AhgVariable.INSTANCE.getCluster(activeClusterName);
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
                                .map(AhgVariable.INSTANCE::getCluster)
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
            AhgVertex activeVertex = AhgVariable.INSTANCE.getVertex(activeName);
            AhgActive activeInfo = activeVertex.getActiveInfo();
            updateHighestBorderInfo(activeInfo, activeVertex, activeCluster);

            String currentName = activeInfo.getCurrentClusterName();
            if (currentName != null) {
                AhgCluster currentCluster = AhgVariable.INSTANCE.getCluster(currentName);
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
            AhgVariable.INSTANCE.getVertex(borderName).
                    buildVirtualMap(clusterLinkMap.get(borderName).getBorderLink(), activeClusterName);
        }

    }


}
