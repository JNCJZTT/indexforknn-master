package ODIN.ODIN.service.build;

import ODIN.ODIN.service.graph.ODINClusterService;
import ODIN.ODIN.common.status.ODINActiveStatus;
import ODIN.ODIN.domain.ODINCluster;
import ODIN.ODIN.domain.ODINClusterLink;
import ODIN.ODIN.domain.ODINVariable;
import ODIN.base.common.constants.Constants;
import ODIN.base.domain.GlobalVariable;
import ODIN.base.domain.Node;
import ODIN.base.service.factory.SpringBeanFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * AhgClusterBuilder
 * 2022/4/25 zhoutao
 */
@Component
public class ODINClusterBuilder {

    /**
     * Build cluster
     *
     * @param clusterName clusterName
     * @param leaf        isleaf
     * @return cluster
     */
    public static ODINCluster build(String clusterName, boolean leaf) {
        int layer = ODINVariable.getClusterLayer(clusterName);

        ODINCluster cluster = ODINCluster.builder()
                .name(clusterName)
                .clusterLinkMap(new HashMap<>())
                .layer(layer)
                .activeNames(new HashSet<>())
                .leaf(leaf)
                .build();
        if (layer > 0) {
            cluster.setParentName(ODINVariable.INSTANCE.getLayerClusterName(clusterName, layer - 1));
        }
        if (leaf) {
            cluster.setStatus(ODINActiveStatus.CURRENT_ACTIVE);
        } else {
            cluster.setStatus(ODINActiveStatus.SON_ACTIVE);
            buildNonleafCluster(cluster);
        }
        return cluster;
    }

    private static void buildNonleafCluster(ODINCluster cluster) {
        List<String> children = new ArrayList<>();
        for (int i = 0; i < GlobalVariable.BRANCH; i++) {
            String childName = cluster.getName() + Constants.CLUSTER_NAME_SUFFIX + i;
            if (!ODINVariable.INSTANCE.containsClusterKey(childName)) {
                continue;
            }

            if (ODINVariable.INSTANCE.getCluster(childName) == null) {
                ODINVariable.INSTANCE.addCluster(childName, ODINClusterBuilder.build(childName, false));
            }
            children.add(childName);
        }
        cluster.setChildren(children);
        children.forEach(child -> processChildrenClusters(child, cluster));
        children.forEach(child -> addClusterLinks2Parent(child, cluster));
        SpringBeanFactory.getBean(ODINClusterService.class).computeCluster(cluster);
    }

    private static void processChildrenClusters(String childName, ODINCluster cluster) {
        ODINCluster childCluster = ODINVariable.INSTANCE.getCluster(childName);
        cluster.addVertices(childCluster.getBorderNames());
        cluster.addActiveNames(childCluster.getActiveNames());
    }

    private static void addClusterLinks2Parent(String childName, ODINCluster cluster) {
        ODINCluster childCluster = ODINVariable.INSTANCE.getCluster(childName);
        Map<Integer, ODINClusterLink> clusterLinkMap = cluster.getClusterLinkMap();
        Map<Integer, ODINClusterLink> childLinkMap = childCluster.getClusterLinkMap();


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
