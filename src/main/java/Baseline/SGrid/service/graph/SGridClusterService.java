package Baseline.SGrid.service.graph;

import Baseline.SGrid.domain.SGridCluster;
import Baseline.SGrid.domain.SGridClusterLink;
import Baseline.SGrid.domain.SGridVariable;
import Baseline.base.service.utils.DisComputerUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SGridClusterService
 * 2022/5/12 zhoutao
 */
@Service
public class SGridClusterService {
    public void computeClusters() {
        SGridVariable.INSTANCE.getClusters().parallelStream().forEach(this::computeCluster);
    }

    public void computeCluster(SGridCluster cluster) {
        DisComputerUtil.floyd(cluster);
        saveBorderNames(cluster);
    }

    /**
     * save border info
     *
     * @param cluster cluster
     */
    private void saveBorderNames(SGridCluster cluster) {
        Map<Integer, SGridClusterLink> clusterLinkMap = cluster.getClusterLinkMap();

        List<Integer> borderNames = clusterLinkMap.keySet().stream()
                .filter(vertexName -> SGridVariable.INSTANCE.getVertex(vertexName).isBorder())
                .collect(Collectors.toList());

        for (Integer borderName : borderNames) {
            for (Integer name : clusterLinkMap.keySet()) {
                int dis = cluster.getClusterDis(borderName, name);
                if (dis != -1 && !name.equals(borderName)) {
                    SGridVariable.INSTANCE.getVertex(name).addVirtualLink(borderName, dis);
                }
            }
        }
    }
}
