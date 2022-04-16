package com.index.indexforknn.ahg.service.graph;

import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.ahg.domain.*;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.annotation.CostTime;
import com.index.indexforknn.base.domain.enumeration.TimeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 * 2022/3/11 zhoutao
 */
@Slf4j
@Service
public class AhgClusterService {

    @CostTime(msg = "计算子图", timeType = TimeType.MilliSecond)
    public void computeClusters() {
        AhgVariable.clusters.values().forEach(this::computeCluster);
    }

    public void computeCluster(AhgCluster cluster) {
        floyd(cluster);
        saveBorderNames(cluster);
    }


    /**
     * Floyd 算法
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
     * 保存边界点信息
     */
    private void saveBorderNames(AhgCluster cluster) {
        int level = cluster.getLevel();
        Map<Integer, AhgClusterLink> clusterLinkMap = cluster.getClusterLinkMap();
        // parallelStream不保证顺序，streamp保证顺序
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
     * 更新兴趣点到边界点之间的边
     */
    public void updateHighestBorderInfo(AhgActive activeInfo, AhgCluster cluster) {
        Map<String, List<Node>> highestBorderInfo = activeInfo.getHighestBorderInfo();
        String clusterName = cluster.getName();

        // 如果不存在，则要更新
        if (!highestBorderInfo.containsKey(clusterName)) {
            int level = cluster.getLevel();
            Integer activeName = activeInfo.getName();
            AhgVertex vertex = AhgVariable.INSTANCE.getVertex(activeName);

            if (vertex.isBorder(level)) {
                highestBorderInfo.put(clusterName, Collections.emptyList());
            } else if (cluster.isLeaf() || vertex.isBorder(level - 1)) {
                // 如果兴趣点属于本层簇中结点
                List<Node> thisLayerBorders = new ArrayList<>();
                for (Integer border : cluster.getBorderNames()) {
                    int dis = cluster.getClusterDis(activeName, border);
                    if (dis != -1) {
                        thisLayerBorders.add(new Node(border, dis));
                    }
                }
                highestBorderInfo.put(clusterName, thisLayerBorders);
            } else {
                // 兴趣点不存在该簇中
                String lastLayerName = clusterName + AhgConstants.CLUSTER_NAME_SUFFIX +
                        vertex.getLayerClusterName(level + 1);
                List<Node> lastLayerBorders = highestBorderInfo.get(lastLayerName);
                if (lastLayerBorders == null) {
                    AhgVariable.INSTANCE.getCluster(lastLayerName).updateHighestBorderInfo(activeInfo);
                } else {
                    List<Node> thisLayerBorders = new ArrayList<>();

                    for (Integer borderName : cluster.getBorderNames()) {
                        int dis = Integer.MAX_VALUE;
                        for (Node lastNode : lastLayerBorders) {
                            int lastBorderName = lastNode.getName();
                            if (borderName.equals(lastBorderName)) {
                                dis = lastNode.getDis();
                                break;
                            }
                            int borderDis = cluster.getClusterDis(borderName, lastBorderName);
                            if (borderDis == -1) {
                                continue;
                            }
                            dis = Math.min(dis, borderDis + lastNode.getDis());
                        }
                        thisLayerBorders.add(new Node(borderName, dis));
                    }
                    highestBorderInfo.put(clusterName, thisLayerBorders);
                }

            }
        }
        if (cluster.getStatus() == AhgConstants.PARENT_ACTIVE) {
            updateHighestBorderInfo(activeInfo, AhgVariable.INSTANCE.getCluster(cluster.getParentName()));
        }
    }

    public void addQuery(int queryName, PriorityQueue<Integer> current, List<Integer> clusterChangedList) {
        AhgVertex queryVertex = AhgVariable.vertices.get(queryName);
        AhgCluster activeCluster = AhgVariable.clusters.get(queryVertex.getClusterName());
        while (activeCluster.getStatus() != AhgConstants.CLUSTER_ACTIVE) {
            activeCluster = AhgVariable.clusters.get(activeCluster.getParentName());
            if (activeCluster == null) {
                log.error("Cluster Active Error");
            }
        }


    }


}
