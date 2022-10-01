package com.index.indexforknn.amt.service.graph;

import com.index.indexforknn.amt.common.status.AmtActiveStatus;
import com.index.indexforknn.amt.common.status.AmtClusterUpdateStatus;
import com.index.indexforknn.amt.domain.*;
import com.index.indexforknn.amt.service.build.AmtClusterBuilder;
import com.index.indexforknn.amt.domain.AmtVariable;
import com.index.indexforknn.amt.service.utils.Trie;
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
public class AmtClusterService {

    /**
     * compute clusters
     * parallelStream does not guarantee order, streamp guarantees order
     */
    public void computeClusters() {
        AmtVariable.INSTANCE.getClusters().parallelStream().forEach(this::computeCluster);
    }

    public void computeCluster(AmtCluster cluster) {
        DisComputerUtil.floyd(cluster);
        saveBorderNames(cluster);
    }

    /**
     * save border info
     *
     * @param cluster cluster
     */
    private void saveBorderNames(AmtCluster cluster) {
        int level = cluster.getLayer();
        Map<Integer, AmtClusterLink> clusterLinkMap = cluster.getClusterLinkMap();

        List<Integer> borderNames = clusterLinkMap.keySet().stream()
                .filter(vertexName -> AmtVariable.INSTANCE.getVertex(vertexName).isBorder(level))
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
    public void updateHighestBorderInfo(AmtActive activeInfo, AmtVertex activeVertex, AmtCluster cluster) {
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
                String lastLayerName = AmtVariable.INSTANCE.getLayerClusterName(activeVertex.getClusterName(), level + 1);
                List<Node> lastLayerBorders = highestBorderInfo.get(lastLayerName);
                if (lastLayerBorders == null) {
                    updateHighestBorderInfo(activeInfo, activeVertex, AmtVariable.INSTANCE.getCluster(lastLayerName));
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
        if (cluster.getStatus() == AmtActiveStatus.PARENT_ACTIVE) {
            updateHighestBorderInfo(activeInfo, activeVertex, AmtVariable.INSTANCE.getCluster(cluster.getParentName()));
        }
    }

    /**
     * add query into virtual map
     *
     * @param ahgKnn knn
     */
    public void addQuery(AmtKnn ahgKnn) {
        int queryName = ahgKnn.getNearestName();

        AmtVertex queryVertex = AmtVariable.INSTANCE.getVertex(queryName);
        AmtCluster tempCluster = AmtVariable.INSTANCE.getCluster(queryVertex.getClusterName());

        // the active cluster and the cluster which query vertex is a border
        AmtCluster activeCluster = null;
        AmtCluster borderCluster = null;

        while (tempCluster != null && (activeCluster == null)) {
            if (queryVertex.isBorder(tempCluster.getLayer())) {
                borderCluster = tempCluster;
            }
            if (tempCluster.getStatus() == AmtActiveStatus.CURRENT_ACTIVE) {
                activeCluster = tempCluster;
            }
            tempCluster = AmtVariable.INSTANCE.getCluster(tempCluster.getParentName());
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
            String parent = AmtVariable.INSTANCE.getCluster(queryInClusterName).getParentName();
            while (true) {
                for (String str : AmtVariable.INSTANCE.getCluster(parent).getChildren()) {
                    if (!Objects.equals(str, queryInClusterName)) {
                        clusterChangedList.add(str);
                    }
                }
                if (parent.equals(activeCluster.getName())) {
                    break;
                }
                queryInClusterName = parent;
                parent = AmtVariable.INSTANCE.getCluster(parent).getParentName();
            }
            clusterChangedList.parallelStream().forEach(str -> activateCluster(AmtVariable.INSTANCE.getCluster(str)));
            addQuery(ahgKnn);
        }
    }

    /**
     * add query in current seach space
     *
     * @param ahgKnn        ahgKnn
     * @param activeCluster activeCluster
     */
    private void addCurrent(AmtKnn ahgKnn, AmtCluster activeCluster) {
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
        AmtVertex queryVertex = AmtVariable.INSTANCE.getVertex(queryName);
        int activeLevel = activeCluster.getLayer();
        for (Integer activeName : activeCluster.getActiveNames()) {
            AmtVertex activeVertex = AmtVariable.INSTANCE.getVertex(activeName);
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
                AmtCluster sameParentCluster = AmtVariable.INSTANCE.getCluster(sameParentClusterName);
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
                    sameParentCluster = AmtVariable.INSTANCE.getCluster(sameParentClusterName);
                }
                String sonClusterName = activeCluster.getName() +
                        Constants.CLUSTER_NAME_SUFFIX + activeVertex.getClusterNames()[activeLevel + 1];

                Map<String, List<Node>> highestBorderInfo = activeVertex.getActiveInfo().getHighestBorderInfo();
                if (!highestBorderInfo.containsKey(sonClusterName)) {
                    updateHighestBorderInfo(activeVertex.getActiveInfo(), activeVertex, AmtVariable.INSTANCE.getCluster(sonClusterName));
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
    public void removeActive(AmtVertex vertex) {
        AmtCluster activeCluster = AmtVariable.INSTANCE.getCluster(vertex.getActiveInfo().getCurrentClusterName()),
                leafCluster = AmtVariable.INSTANCE.getCluster(vertex.getClusterName());
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
            if (trie.isProcessed(activeClusterName)) {
                continue;
            }
            AmtCluster activeCluster = AmtVariable.INSTANCE.getCluster(activeClusterName);
            AmtClusterUpdateStatus suitStatus = activeCluster.isSuitableCluster();

            List<AmtCluster> activeClusters;
            if (suitStatus == AmtClusterUpdateStatus.SUITABLE) {
                if (!isFirst) {
                    continue;
                }
                activeClusters = Collections.singletonList(activeCluster);
            } else if (suitStatus == AmtClusterUpdateStatus.NEED_TO_MERGE) {
                do {
                    activeClusterName = activeCluster.getParentName();
                    if (!AmtVariable.INSTANCE.containsClusterValue(activeClusterName)) {
                        AmtVariable.INSTANCE.addCluster(activeClusterName,
                                AmtClusterBuilder.build(activeClusterName, false));
                    }
                    activeCluster = AmtVariable.INSTANCE.getCluster(activeClusterName);
                    suitStatus = activeCluster.isSuitableCluster();
                } while (suitStatus == AmtClusterUpdateStatus.NEED_TO_MERGE);

                activeClusters = Collections.singletonList(activeCluster);

            } else {
                activeClusters = new ArrayList<>();
                Queue<AmtCluster> clusters = new LinkedList<>();
                clusters.add(activeCluster);
                while (!clusters.isEmpty()) {
                    AmtCluster cluster = clusters.poll();
                    if (cluster.isSuitableCluster() != AmtClusterUpdateStatus.NEED_TO_SPLIT) {
                        activeClusters.add(cluster);
                    } else {
                        clusters.addAll(cluster.getChildren().stream()
                                .map(AmtVariable.INSTANCE::getCluster)
                                .collect(Collectors.toList()));
                    }
                }
            }
            for (AmtCluster cluster : activeClusters) {
                activateCluster(cluster);
                trie.insert(cluster.getName());
            }
        }
    }

    public void activateCluster(AmtCluster activeCluster) {
        activeCluster.updateActiveState(activeCluster.getStatus());
        activeCluster.setStatus(AmtActiveStatus.CURRENT_ACTIVE);

        Map<Integer, AmtClusterLink> clusterLinkMap = activeCluster.getClusterLinkMap();
        String activeClusterName = activeCluster.getName();

        for (int activeName : activeCluster.getActiveNames()) {
            AmtVertex activeVertex = AmtVariable.INSTANCE.getVertex(activeName);
            AmtActive activeInfo = activeVertex.getActiveInfo();
            updateHighestBorderInfo(activeInfo, activeVertex, activeCluster);

            String currentName = activeInfo.getCurrentClusterName();
            if (currentName != null) {
                AmtCluster currentCluster = AmtVariable.INSTANCE.getCluster(currentName);
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
            AmtVariable.INSTANCE.getVertex(borderName).
                    buildVirtualMap(clusterLinkMap.get(borderName).getBorderLink(), activeClusterName);
        }

    }


}
