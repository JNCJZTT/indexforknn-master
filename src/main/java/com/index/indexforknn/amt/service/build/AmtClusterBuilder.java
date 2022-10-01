package com.index.indexforknn.amt.service.build;

import com.index.indexforknn.amt.common.status.AmtActiveStatus;
import com.index.indexforknn.amt.domain.AmtCluster;
import com.index.indexforknn.amt.domain.AmtClusterLink;
import com.index.indexforknn.amt.service.graph.AmtClusterService;
import com.index.indexforknn.amt.domain.AmtVariable;
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
public class AmtClusterBuilder {

    /**
     * Build cluster
     *
     * @param clusterName clusterName
     * @param leaf        isleaf
     * @return cluster
     */
    public static AmtCluster build(String clusterName, boolean leaf) {
        int layer = AmtVariable.INSTANCE.getClusterLayer(clusterName);

        AmtCluster cluster = AmtCluster.builder()
                .name(clusterName)
                .clusterLinkMap(new HashMap<>())
                .layer(layer)
                .parentName(AmtVariable.INSTANCE.getLayerClusterName(clusterName, layer - 1))
                .activeNames(new HashSet<>())
                .leaf(leaf)
                .build();
        if (cluster.isLeaf()) {
            cluster.setStatus(AmtActiveStatus.CURRENT_ACTIVE);
        } else {
            cluster.setStatus(AmtActiveStatus.SON_ACTIVE);
            buildNonleafCluster(cluster);
        }
        return cluster;
    }

    private static void buildNonleafCluster(AmtCluster cluster) {
        List<String> children = new ArrayList<>();
        for (int i = 0; i < GlobalVariable.BRANCH; i++) {
            String childName = cluster.getName() + Constants.CLUSTER_NAME_SUFFIX + i;
            if (!AmtVariable.INSTANCE.containsClusterKey(childName)) {
                continue;
            }

            if (AmtVariable.INSTANCE.getCluster(childName) == null) {
                AmtVariable.INSTANCE.addCluster(childName, AmtClusterBuilder.build(childName, false));
            }
            children.add(childName);
        }
        cluster.setChildren(children);
        children.forEach(child -> processChildrenClusters(child, cluster));
        children.forEach(child -> addClusterLinks2Parent(child, cluster));
        SpringBeanFactory.getBean(AmtClusterService.class).computeCluster(cluster);
    }

    private static void processChildrenClusters(String childName, AmtCluster cluster) {
        AmtCluster childCluster = AmtVariable.INSTANCE.getCluster(childName);
        cluster.addVertices(childCluster.getBorderNames());
        cluster.addActiveNames(childCluster.getActiveNames());
    }

    private static void addClusterLinks2Parent(String childName, AmtCluster cluster) {
        AmtCluster childCluster = AmtVariable.INSTANCE.getCluster(childName);
        Map<Integer, AmtClusterLink> clusterLinkMap = cluster.getClusterLinkMap();
        Map<Integer, AmtClusterLink> childLinkMap = childCluster.getClusterLinkMap();


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
