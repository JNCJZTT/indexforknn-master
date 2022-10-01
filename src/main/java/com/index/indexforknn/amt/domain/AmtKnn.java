package com.index.indexforknn.amt.domain;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.api.knn.DijkstraKnn;
import com.index.indexforknn.base.domain.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * AhgKnn
 * 2022/4/15 zhoutao
 */
@Getter
@Setter
@Slf4j
public class AmtKnn extends DijkstraKnn<AmtVertex> {

    // temporary modfied clusters
    private AmtCluster tempInactiveCluster;

    public AmtKnn(int queryName) {
        super(queryName, GlobalVariable.VERTEX_NUM);
        nearestVertex = AmtVariable.INSTANCE.getVertex(queryName);

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
    }

    @Override
    protected void updateNearestName() {
        nearestName = current.poll();
        nearestVertex = AmtVariable.INSTANCE.getVertex(nearestName);
    }

}
