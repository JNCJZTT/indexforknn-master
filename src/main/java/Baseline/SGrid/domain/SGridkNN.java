package Baseline.SGrid.domain;

import Baseline.base.domain.GlobalVariable;
import Baseline.base.domain.Node;
import Baseline.base.domain.api.knn.DijkstraKnn;
import lombok.Getter;
import lombok.Setter;

/**
 * SGridkNN
 * 2022/5/12 zhoutao
 */
@Getter
@Setter
public class SGridkNN extends DijkstraKnn<SGridVertex> {
    public SGridkNN(int queryName) {
        super(queryName,GlobalVariable.VERTEX_NUM+ SGridVariable.INSTANCE.getBorderSize());
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
