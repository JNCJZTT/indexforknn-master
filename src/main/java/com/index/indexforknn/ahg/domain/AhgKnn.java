package com.index.indexforknn.ahg.domain;

import com.index.indexforknn.ahg.service.AhgKnnService;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.api.knn.DijkstraKnn;
import com.index.indexforknn.base.domain.Node;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.PriorityQueue;

/**
 * AhgKnn
 * 2022/4/15 zhoutao
 */
@Getter
@Setter
@Slf4j
public class AhgKnn extends DijkstraKnn<AhgVertex> {
    private boolean isKnned = false;
    // temporary modfied clusters
    private AhgCluster tempInactiveCluster;

    public AhgKnn(int queryName) {
        super(queryName, GlobalVariable.VERTEX_NUM);
        nearestVertex = AhgVariable.INSTANCE.getVertex(queryName);

    }

    /**
     * knn
     */
    @Override
    public void knn() {
        long startKnn = System.nanoTime();
        while (true) {
            if (terminateEarly()) {
                break;
            }

            // expand search space
            if (nearestVertex.isVirtualMapBorderNode()) {
                for (Node node : nearestVertex.getVirtualLink()) {
                    setNode(node);
                }
            }

            // update current nearest node
            updateNearestName();
        }
        queryTime = (System.nanoTime() - startKnn) / 1000.0;
        isKnned = true;
    }

    @Override
    protected void updateNearestName() {
        nearestName = current.poll();
        nearestVertex = AhgVariable.INSTANCE.getVertex(nearestName);
    }

}
