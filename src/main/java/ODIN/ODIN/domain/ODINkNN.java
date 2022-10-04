package ODIN.ODIN.domain;

import ODIN.base.domain.GlobalVariable;
import ODIN.base.domain.api.knn.DijkstraKnn;
import ODIN.base.domain.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * ODINkNN
 * 2022/4/15 zhoutao
 */
@Getter
@Setter
@Slf4j
public class ODINkNN extends DijkstraKnn<ODINVertex> {
    private boolean isKnned = false;
    // temporary modfied clusters
    private ODINCluster tempInactiveCluster;

    public ODINkNN(int queryName) {
        super(queryName, GlobalVariable.VERTEX_NUM);
        nearestVertex = ODINVariable.INSTANCE.getVertex(queryName);

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
        nearestVertex = ODINVariable.INSTANCE.getVertex(nearestName);
    }

}
