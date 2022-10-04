package Baseline.VTree.service.build;

import Baseline.VTree.domain.VTreeCluster;
import Baseline.VTree.domain.VTreeClusterLink;
import Baseline.VTree.domain.VtreeVariable;
import Baseline.VTree.service.graph.VTreeClusterService;
import Baseline.base.common.constants.Constants;
import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import Baseline.base.service.factory.SpringBeanFactory;

import java.util.*;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
public class VTreeClusterBuilder {
    public static VTreeCluster build(String clusterName, boolean leaf) {
        int layer = VtreeVariable.getClusterLayer(clusterName);
        VTreeCluster cluster = VTreeCluster.builder()
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

    private static void buildNonleafCluster(VTreeCluster cluster) {
        List<String> children = new ArrayList<>();
        for (int i = 0; i < GlobalVariable.BRANCH; i++) {
            String childName = cluster.getName() + Constants.CLUSTER_NAME_SUFFIX + i;
            if (!VtreeVariable.INSTANCE.containsClusterKey(childName)) {
                continue;
            }

            if (VtreeVariable.INSTANCE.getCluster(childName) == null) {
                VtreeVariable.INSTANCE.addCluster(childName, VTreeClusterBuilder.build(childName, false));
            }
            children.add(childName);
        }
        cluster.setChildren(children);
        children.forEach(child -> processChildrenClusters(child, cluster));
        children.forEach(child -> addClusterLinks2Parent(child, cluster));
        SpringBeanFactory.getBean(VTreeClusterService.class).computeCluster(cluster);
    }

    private static void processChildrenClusters(String childName, VTreeCluster cluster) {
        VTreeCluster childCluster = VtreeVariable.INSTANCE.getCluster(childName);
        cluster.addVertices(childCluster.getBorderNames());
    }

    private static void addClusterLinks2Parent(String childName, VTreeCluster cluster) {
        VTreeCluster childCluster = VtreeVariable.INSTANCE.getCluster(childName);
        Map<Integer, VTreeClusterLink> clusterLinkMap = cluster.getClusterLinkMap();
        Map<Integer, VTreeClusterLink> childLinkMap = childCluster.getClusterLinkMap();


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
