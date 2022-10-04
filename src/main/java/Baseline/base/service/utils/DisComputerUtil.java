package Baseline.base.service.utils;


import Baseline.base.domain.api.Cluster;

import java.util.Set;

/**
 * computer pair dis
 * 2022/5/12 zhoutao
 */
public class DisComputerUtil {

    /**
     * floyd
     *
     * @param cluster cluster
     */
    public static void floyd(Cluster cluster) {
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
}
