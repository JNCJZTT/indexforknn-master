package Baseline.SGrid.domain;

import Baseline.base.domain.api.Cluster;

import java.util.HashMap;

/**
 * SGridCluster
 * 2022/4/27 zhoutao
 */
public class SGridCluster extends Cluster<SGridClusterLink> {


    public SGridCluster(String name) {
        setName(name);
        clusterLinkMap = new HashMap<>();
    }

    public void addVertex(int name) {
        clusterLinkMap.put(name, new SGridClusterLink());
    }


}
