package Baseline.VTree.service.graph;

import Baseline.VTree.domain.VTreeCluster;
import Baseline.VTree.domain.VTreeClusterLink;
import Baseline.VTree.domain.VtreeVariable;
import Baseline.base.domain.Node;
import Baseline.base.service.utils.DisComputerUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
@Service
public class VTreeClusterService {

    public void computeClusters() {
        VtreeVariable.INSTANCE.getClusters().parallelStream().forEach(this::computeCluster);
    }

    public void computeCluster(VTreeCluster cluster) {
        DisComputerUtil.floyd(cluster);
        saveBorderNames(cluster);
    }

    /**
     * save border info
     *
     * @param cluster cluster
     */
    private void saveBorderNames(VTreeCluster cluster) {
        int level = cluster.getLayer();
        Map<Integer, VTreeClusterLink> clusterLinkMap = cluster.getClusterLinkMap();

        List<Integer> borderNames = clusterLinkMap.keySet().stream()
                .filter(vertexName -> VtreeVariable.INSTANCE.getVertex(vertexName).isBorder(level))
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
}
