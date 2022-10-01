package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.ahg.common.status.AhgActiveStatus;
import com.index.indexforknn.base.common.constants.Constants;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.Vertex;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * AhgVertex
 * 2022/2/10 zhoutao
 */
@Getter
@Setter
@Slf4j
public class AhgVertex extends Vertex {

    private boolean[] border;

    private String[] clusterNames;

    private AhgActive activeInfo;

    // virtual link
    private Set<Node> virtualLink;

    // active node saved in cluster
    private String activeClusterName;

    private Node[] kCars;

    public AhgVertex() {
        virtualLink = new HashSet<>();
    }

    @Override
    public void setClusterName(String clusterName) {
        super.setClusterName(clusterName);
        clusterNames = clusterName.split(Constants.CLUSTER_NAME_SUFFIX);
        border = new boolean[clusterNames.length];
        Arrays.fill(border, false);
    }

    /**
     * isBorder
     *
     * @param layer layer
     */
    public boolean isBorder(int layer) {
        return border[layer];
    }

    /**
     * build virtual map(border)
     *
     * @param virtualLink       virtual edge
     * @param activeClusterName current active clusterName
     */
    public void buildVirtualMap(Set<Node> virtualLink, String activeClusterName) {
        this.activeClusterName = activeClusterName;
        this.virtualLink = virtualLink;
    }

    /**
     * isVirtualMapBorderNode
     *
     * @return return
     */
    public boolean isVirtualMapBorderNode() {
        if (activeClusterName != null
                && AhgVariable.INSTANCE.getCluster(activeClusterName).getStatus() == AhgActiveStatus.CURRENT_ACTIVE) {
            return true;
        }
        return false;
    }

    public void setkCars(PriorityQueue<Car> cars) {
        this.kCars = new Node[GlobalVariable.K];
        int i = GlobalVariable.K - 1;
        for (Car car : cars) {
            kCars[i] = new Node(car.getName(), car.getQueryDis());
            i--;
        }
    }
}
