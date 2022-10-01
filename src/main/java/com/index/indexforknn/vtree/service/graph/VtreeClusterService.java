package com.index.indexforknn.vtree.service.graph;

import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.service.utils.DisComputerUtil;
import com.index.indexforknn.vtree.domain.VtreeCluster;
import com.index.indexforknn.vtree.domain.VtreeClusterLink;
import com.index.indexforknn.vtree.domain.VtreeVariable;
import com.index.indexforknn.vtree.service.build.VtreeClusterBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 * 2022/9/14 zhoutao
 */
@Service
public class VtreeClusterService {

    public void computeClusters() {
        VtreeVariable.INSTANCE.getClusters().parallelStream().forEach(this::computeCluster);
    }

    public void computeCluster(VtreeCluster cluster) {
        DisComputerUtil.floyd(cluster);
        saveBorderNames(cluster);
    }

    /**
     * save border info
     *
     * @param cluster cluster
     */
    private void saveBorderNames(VtreeCluster cluster) {
        int level = cluster.getLayer();
        Map<Integer, VtreeClusterLink> clusterLinkMap = cluster.getClusterLinkMap();

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
