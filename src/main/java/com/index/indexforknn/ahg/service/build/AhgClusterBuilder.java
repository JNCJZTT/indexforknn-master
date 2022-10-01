package com.index.indexforknn.ahg.service.build;

import com.index.indexforknn.ahg.common.status.AhgActiveStatus;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgClusterLink;
import com.index.indexforknn.ahg.service.graph.AhgClusterService;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.base.common.constants.Constants;
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
        int layer = AhgVariable.getClusterLayer(clusterName);

        AhgCluster cluster = AhgCluster.builder()
                .name(clusterName)
                .clusterLinkMap(new HashMap<>())
                .layer(layer)
                .activeNames(new HashSet<>())
                .leaf(leaf)
                .build();
        if (layer > 0) {
            cluster.setParentName(AhgVariable.INSTANCE.getLayerClusterName(clusterName, layer - 1));
        }
        if (leaf) {
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
            String childName = cluster.getName() + Constants.CLUSTER_NAME_SUFFIX + i;
            if (!AhgVariable.INSTANCE.containsClusterKey(childName)) {
                continue;
            }

            if (AhgVariable.INSTANCE.getCluster(childName) == null) {
                AhgVariable.INSTANCE.addCluster(childName, AhgClusterBuilder.build(childName, false));
            }
            children.add(childName);
        }
        cluster.setChildren(children);
        children.forEach(child -> processChildrenClusters(child, cluster));
        children.forEach(child -> addClusterLinks2Parent(child, cluster));
        SpringBeanFactory.getBean(AhgClusterService.class).computeCluster(cluster);
    }

    private static void processChildrenClusters(String childName, AhgCluster cluster) {
        AhgCluster childCluster = AhgVariable.INSTANCE.getCluster(childName);
        cluster.addVertices(childCluster.getBorderNames());
        cluster.addActiveNames(childCluster.getActiveNames());
    }

    private static void addClusterLinks2Parent(String childName, AhgCluster cluster) {
        AhgCluster childCluster = AhgVariable.INSTANCE.getCluster(childName);
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
