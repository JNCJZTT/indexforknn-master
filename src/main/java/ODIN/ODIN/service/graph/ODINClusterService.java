package ODIN.ODIN.service.graph;

import ODIN.ODIN.domain.*;
import ODIN.ODIN.service.utils.Trie;
import ODIN.ODIN.common.status.ODINActiveStatus;
import ODIN.ODIN.common.status.ODINClusterUpdateStatus;

import ODIN.ODIN.domain.*;
import ODIN.ODIN.service.build.ODINClusterBuilder;
import ODIN.base.common.constants.Constants;
import ODIN.base.domain.Node;
import ODIN.base.service.utils.DisComputerUtil;
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
public class ODINClusterService {

    /**
     * compute clusters
     * parallelStream does not guarantee order, streamp guarantees order
     */
    public void computeClusters() {
//        long buildStartTime = System.nanoTime();
        ODINVariable.INSTANCE.getClusters().parallelStream().forEach(this::computeCluster);
//        long computerClustersTime = System.nanoTime() - buildStartTime;
//        System.out.println("the time of building borders : " + (float) (computerClustersTime) / 1000_000 + "ms");
    }

    public void computeCluster(ODINCluster cluster) {
        DisComputerUtil.floyd(cluster);
        saveBorderNames(cluster);
    }

    /**
     * save border info
     *
     * @param cluster cluster
     */
    private void saveBorderNames(ODINCluster cluster) {
        int level = cluster.getLayer();
        Map<Integer, ODINClusterLink> clusterLinkMap = cluster.getClusterLinkMap();

        List<Integer> borderNames = clusterLinkMap.keySet().stream()
                .filter(vertexName -> ODINVariable.INSTANCE.getVertex(vertexName).isBorder(level))
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
     * @param activeInfo   ODINActive
     * @param activeVertex active vertex
     * @param cluster      cluster
     */
    public void updateHighestBorderInfo(ODINActive activeInfo, ODINVertex activeVertex, ODINCluster cluster) {
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
                String lastLayerName = ODINVariable.INSTANCE.getLayerClusterName(activeVertex.getClusterName(), level + 1);
                List<Node> lastLayerBorders = highestBorderInfo.get(lastLayerName);
                if (lastLayerBorders == null) {
                    updateHighestBorderInfo(activeInfo, activeVertex, ODINVariable.INSTANCE.getCluster(lastLayerName));
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
        if (cluster.getStatus() == ODINActiveStatus.PARENT_ACTIVE) {
            updateHighestBorderInfo(activeInfo, activeVertex, ODINVariable.INSTANCE.getCluster(cluster.getParentName()));
        }
    }

    /**
     * add query into virtual map
     *
     * @param ahgKnn knn
     */
    public void addQuery(ODINkNN ahgKnn) {
        int queryName = ahgKnn.getNearestName();

        ODINVertex queryVertex = ODINVariable.INSTANCE.getVertex(queryName);
        ODINCluster tempCluster = ODINVariable.INSTANCE.getCluster(queryVertex.getClusterName());

        // the active cluster and the cluster which query vertex is a border
        ODINCluster activeCluster = null;
        ODINCluster borderCluster = null;

        while (tempCluster != null && (activeCluster == null)) {
            if (queryVertex.isBorder(tempCluster.getLayer())) {
                borderCluster = tempCluster;
            }
            if (tempCluster.getStatus() == ODINActiveStatus.CURRENT_ACTIVE) {
                activeCluster = tempCluster;
            }
            tempCluster = ODINVariable.INSTANCE.getCluster(tempCluster.getParentName());
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
            String parent = ODINVariable.INSTANCE.getCluster(queryInClusterName).getParentName();
            while (true) {
                for (String str : ODINVariable.INSTANCE.getCluster(parent).getChildren()) {
                    if (!Objects.equals(str, queryInClusterName)) {
                        clusterChangedList.add(str);
                    }
                }
                if (parent.equals(activeCluster.getName())) {
                    break;
                }
                queryInClusterName = parent;
                parent = ODINVariable.INSTANCE.getCluster(parent).getParentName();
            }
            clusterChangedList.parallelStream().forEach(str -> activateCluster(ODINVariable.INSTANCE.getCluster(str)));
            addQuery(ahgKnn);
        }
    }

    /**
     * add query in current seach space
     *
     * @param ahgKnn        ahgKnn
     * @param activeCluster activeCluster
     */
    private void addCurrent(ODINkNN ahgKnn, ODINCluster activeCluster) {
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
        ODINVertex queryVertex = ODINVariable.INSTANCE.getVertex(queryName);
        int activeLevel = activeCluster.getLayer();
        for (Integer activeName : activeCluster.getActiveNames()) {
            ODINVertex activeVertex = ODINVariable.INSTANCE.getVertex(activeName);
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
                ODINCluster sameParentCluster = ODINVariable.INSTANCE.getCluster(sameParentClusterName);
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
                    sameParentCluster = ODINVariable.INSTANCE.getCluster(sameParentClusterName);
                }
                String sonClusterName = activeCluster.getName() +
                        Constants.CLUSTER_NAME_SUFFIX + activeVertex.getClusterNames()[activeLevel + 1];

                Map<String, List<Node>> highestBorderInfo = activeVertex.getActiveInfo().getHighestBorderInfo();
                if (!highestBorderInfo.containsKey(sonClusterName)) {
                    updateHighestBorderInfo(activeVertex.getActiveInfo(), activeVertex, ODINVariable.INSTANCE.getCluster(sonClusterName));
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
    public void removeActive(ODINVertex vertex) {
        ODINCluster activeCluster = ODINVariable.INSTANCE.getCluster(vertex.getActiveInfo().getCurrentClusterName()),
                leafCluster = ODINVariable.INSTANCE.getCluster(vertex.getClusterName());
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
            ODINCluster activeCluster = ODINVariable.INSTANCE.getCluster(activeClusterName);
            ODINClusterUpdateStatus suitStatus = activeCluster.isSuitableCluster();

            List<ODINCluster> activeClusters;
            if (suitStatus == ODINClusterUpdateStatus.SUITABLE) {
                if (!isFirst) {
                    continue;
                }
                activeClusters = Collections.singletonList(activeCluster);
            } else if (suitStatus == ODINClusterUpdateStatus.NEED_TO_MERGE) {
                do {
//                    System.out.println("NEED_TO_MERGE");
                    activeClusterName = activeCluster.getParentName();
                    if (!ODINVariable.INSTANCE.containsClusterValue(activeClusterName)) {
                        ODINVariable.INSTANCE.addCluster(activeClusterName,
                                ODINClusterBuilder.build(activeClusterName, false));
                    }
                    activeCluster = ODINVariable.INSTANCE.getCluster(activeClusterName);
                    suitStatus = activeCluster.isSuitableCluster();
                } while (suitStatus == ODINClusterUpdateStatus.NEED_TO_MERGE);

                activeClusters = Collections.singletonList(activeCluster);

            } else {
                activeClusters = new ArrayList<>();
                Queue<ODINCluster> clusters = new LinkedList<>();
                clusters.add(activeCluster);
                while (!clusters.isEmpty()) {
                    ODINCluster cluster = clusters.poll();
                    if (cluster.isSuitableCluster() != ODINClusterUpdateStatus.NEED_TO_SPLIT) {
                        activeClusters.add(cluster);
                    } else {
                        clusters.addAll(cluster.getChildren().stream()
                                .map(ODINVariable.INSTANCE::getCluster)
                                .collect(Collectors.toList()));
                    }
                }
            }
            for (ODINCluster cluster : activeClusters) {
                activateCluster(cluster);
                trie.insert(cluster.getName());
            }
        }
    }

    public void activateCluster(ODINCluster activeCluster) {
        activeCluster.updateActiveState(activeCluster.getStatus());
        activeCluster.setStatus(ODINActiveStatus.CURRENT_ACTIVE);

        Map<Integer, ODINClusterLink> clusterLinkMap = activeCluster.getClusterLinkMap();
        String activeClusterName = activeCluster.getName();

        for (int activeName : activeCluster.getActiveNames()) {
            ODINVertex activeVertex = ODINVariable.INSTANCE.getVertex(activeName);
            ODINActive activeInfo = activeVertex.getActiveInfo();
            updateHighestBorderInfo(activeInfo, activeVertex, activeCluster);

            String currentName = activeInfo.getCurrentClusterName();
            if (currentName != null) {
                ODINCluster currentCluster = ODINVariable.INSTANCE.getCluster(currentName);
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
            ODINVariable.INSTANCE.getVertex(borderName).
                    buildVirtualMap(clusterLinkMap.get(borderName).getBorderLink(), activeClusterName);
        }

    }


}
