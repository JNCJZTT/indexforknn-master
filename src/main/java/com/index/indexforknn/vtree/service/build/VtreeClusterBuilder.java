package com.index.indexforknn.vtree.service.build;

import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.base.common.constants.Constants;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.service.factory.SpringBeanFactory;
import com.index.indexforknn.vtree.domain.VtreeCluster;
import com.index.indexforknn.vtree.domain.VtreeClusterLink;
import com.index.indexforknn.vtree.domain.VtreeVariable;
import com.index.indexforknn.vtree.service.graph.VtreeClusterService;

import java.util.*;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
public class VtreeClusterBuilder {
    public static VtreeCluster build(String clusterName, boolean leaf) {
        int layer = VtreeVariable.getClusterLayer(clusterName);
        VtreeCluster cluster = VtreeCluster.builder()
                .name(clusterName)
                .clusterLinkMap(new HashMap<>())
                .layer(layer)
                .activeNames(new HashSet<>())
                .leaf(leaf)
                .build();
        if (layer > 0) {
            cluster.setParentName(VtreeVariable.INSTANCE.getLayerClusterName(clusterName, layer - 1));
        }
        if (!leaf) {
            buildNonleafCluster(cluster);
        }
        return cluster;
    }

    private static void buildNonleafCluster(VtreeCluster cluster) {
        List<String> children = new ArrayList<>();
        for (int i = 0; i < GlobalVariable.BRANCH; i++) {
            String childName = cluster.getName() + Constants.CLUSTER_NAME_SUFFIX + i;
            if (!VtreeVariable.INSTANCE.containsClusterKey(childName)) {
                continue;
            }

            if (VtreeVariable.INSTANCE.getCluster(childName) == null) {
                VtreeVariable.INSTANCE.addCluster(childName, VtreeClusterBuilder.build(childName, false));
            }
            children.add(childName);
        }
        cluster.setChildren(children);
        children.forEach(child -> processChildrenClusters(child, cluster));
        children.forEach(child -> addClusterLinks2Parent(child, cluster));
        SpringBeanFactory.getBean(VtreeClusterService.class).computeCluster(cluster);
    }

    private static void processChildrenClusters(String childName, VtreeCluster cluster) {
        VtreeCluster childCluster = VtreeVariable.INSTANCE.getCluster(childName);
        cluster.addVertices(childCluster.getBorderNames());
    }

    private static void addClusterLinks2Parent(String childName, VtreeCluster cluster) {
        VtreeCluster childCluster = VtreeVariable.INSTANCE.getCluster(childName);
        Map<Integer, VtreeClusterLink> clusterLinkMap = cluster.getClusterLinkMap();
        Map<Integer, VtreeClusterLink> childLinkMap = childCluster.getClusterLinkMap();


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
