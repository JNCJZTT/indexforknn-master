package com.index.indexforknn.sgrid.domain;

import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.Node;
import com.index.indexforknn.base.domain.api.knn.DijkstraKnn;
import com.index.indexforknn.sgrid.service.SGridKnnService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * SGridKnn
 * 2022/5/12 zhoutao
 */
@Getter
@Setter
public class SGridKnn extends DijkstraKnn<SGridVertex> {
    public SGridKnn(int queryName) {
        super(queryName,GlobalVariable.VERTEX_NUM+SGridVariable.INSTANCE.getBorderSize());
        nearestVertex = SGridVariable.INSTANCE.getVertex(queryName);
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
            for (Node node : nearestVertex.getVirtualLink()) {
                setNode(node);
            }
            updateNearestName();
        }
        queryTime = (System.nanoTime() - startKnn) / 1000.0;
    }

    @Override
    protected void updateNearestName() {
        nearestName = current.poll();
        nearestVertex = SGridVariable.INSTANCE.getVertex(nearestName);
    }
}
