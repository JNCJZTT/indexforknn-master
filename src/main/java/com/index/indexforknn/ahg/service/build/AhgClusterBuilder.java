package com.index.indexforknn.ahg.service.build;

import com.index.indexforknn.ahg.common.AhgConstants;
import com.index.indexforknn.ahg.common.status.AhgActiveStatus;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgClusterLink;
import com.index.indexforknn.ahg.service.graph.AhgClusterService;
import com.index.indexforknn.ahg.service.utils.AhgVariableUtil;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.service.factory.SpringBeanFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * AhgClusterBuilder
 * 2022/4/25 zhoutao
 */
@Component
public class AhgClusterBuilder {

    /**
     * Build cluster
     *
     * @param clusterName clusterName
     * @param leaf        isleaf
     * @return cluster
     */
    public static AhgCluster build(String clusterName, boolean leaf) {
        int layer = AhgVariableUtil.getClusterLayer(clusterName);
        AhgCluster cluster = new AhgCluster()
                .setName(clusterName)
                .setClusterLinkMap(new HashMap<>())
                .setLayer(layer)
                .setParentName(AhgVariableUtil.getLayerClusterName(clusterName, layer - 1))
                .setActiveNames(new HashSet<>())
                .setLeaf(leaf);
        if (cluster.isLeaf()) {
            cluster.setStatus(AhgActiveStatus.CURRENT_ACTIVE);
        } else {
            cluster.setStatus(AhgActiveStatus.SON_ACTIVE);
            buildNonleafCluster(cluster);
        }
        return cluster;
    }

    private static void buildNonleafCluster(AhgCluster cluster) {
        List<String> children = new ArrayList<>();
        for (int i = 0; i < GlobalVariable.BRANCH; i++) {
            String childName = cluster.getName() + AhgConstants.CLUSTER_NAME_SUFFIX + i;
            if (!AhgVariableUtil.containsClusterKey(childName)) {
                continue;
            }

            if (AhgVariableUtil.getCluster(childName) == null) {
                AhgVariableUtil.addCluster(childName, AhgClusterBuilder.build(childName, false));
            }
            children.add(childName);
        }
        cluster.setChildren(children);
        children.forEach(child -> processChildrenClusters(child, cluster));
        children.forEach(child -> addClusterLinks2Parent(child, cluster));
        SpringBeanFactory.getBean(AhgClusterService.class).computeCluster(cluster);
    }

    private static void processChildrenClusters(String childName, AhgCluster cluster) {
        AhgCluster childCluster = AhgVariableUtil.getCluster(childName);
        cluster.addVertices(childCluster.getBorderNames());
        cluster.addActiveNames(childCluster.getActiveNames());
    }

    private static void addClusterLinks2Parent(String childName, AhgCluster cluster) {
        AhgCluster childCluster = AhgVariableUtil.getCluster(childName);
        Map<Integer, AhgClusterLink> clusterLinkMap = cluster.getClusterLinkMap();
        Map<Integer, AhgClusterLink> childLinkMap = childCluster.getClusterLinkMap();


        for (Integer borderName : childCluster.getBorderNames()) {
            for (Node node : childLinkMap.get(borderName).getBorderLink()) {
                int name = node.getName(), dis = node.getDis();

                if (clusterLinkMap.containsKey(name)) {
                    cluster.addClusterLink(borderName, name, dis);
                } else {
                    cluster.addBorderLink(borderName, name, dis);
                }
            }
        }
    }
}
