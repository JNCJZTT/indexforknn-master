package ODIN.ODIN.service;

import ODIN.ODIN.service.dto.ODINkNNDTO;
import ODIN.ODIN.service.dto.result.ODINkNNEdge;
import ODIN.ODIN.service.graph.ODINClusterService;
import ODIN.ODIN.domain.ODINkNN;
import ODIN.ODIN.domain.ODINVariable;
import ODIN.ODIN.domain.ODINVertex;
import ODIN.base.service.dto.result.KnnResultDTO;
import ODIN.base.domain.enumeration.IndexType;
import ODIN.base.service.api.IKnnService;
import ODIN.base.service.dto.KnnDTO;
import ODIN.base.service.utils.DistributionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AhgKnnService
 * 2022/4/15 zhoutao
 */
@Service
@Slf4j
public class ODINService implements IKnnService {
//    ODINkNN ahgKnn;

    Map<Integer, ODINkNN> knnMap;
    private List<ODINkNNEdge> queryEdges;
    private boolean isInit = false;

    @Autowired
    ODINClusterService clusterService;

    public ODINService() {
        register();
    }

    /**
     * initAhgKnn
     */
    public void initAhgKnn(ODINkNNDTO knnDTO) {
        if (isInit && !knnDTO.isReset()) {
            return;
        }
        if (knnDTO.isReset() && knnMap != null) {
            knnMap.values().stream().filter(AhgKnn -> AhgKnn.getTempInactiveCluster() != null)
                    .forEach(AhgKnn -> clusterService.activateCluster(AhgKnn.getTempInactiveCluster()));
        }
        int querySize = knnDTO.getQuerySize();
        knnMap = new HashMap<>(querySize * 2);
        queryEdges = new ArrayList<>(querySize);
        for (int i = 0; i < querySize; i++) {
            ODINkNNEdge edge = new ODINkNNEdge(DistributionUtil.getEdge());
            knnMap.put(edge.getFrom(), new ODINkNN(edge.getFrom()));
            knnMap.put(edge.getTo(), new ODINkNN(edge.getTo()));
            queryEdges.add(edge);
        }
        isInit = true;
    }

    public void batchAhgKnnSerach() {
        for (ODINkNNEdge edge : queryEdges) {
            ODINkNNSearch(edge);
        }
    }

    public void updateKnnPosition() {
        queryEdges.parallelStream().forEach(ODINkNNEdge::update);
    }

    public void ODINkNNSearch(ODINkNNEdge edge) {
        ODINVertex vertexFrom = ODINVariable.INSTANCE.getVertex(edge.getFrom());
        ODINVertex vertexTo = ODINVariable.INSTANCE.getVertex(edge.getTo());
        if (!knnMap.containsKey(edge.getFrom())) {
            knnMap.put(edge.getFrom(), new ODINkNN(edge.getFrom()));
        }
        if (!knnMap.containsKey(edge.getTo())) {
            knnMap.put(edge.getTo(), new ODINkNN(edge.getTo()));
        }
        ODINkNN knnFrom = knnMap.get(edge.getFrom());
        ODINkNN knnTo = knnMap.get(edge.getTo());

        double tmpOffSet = 0;
        if (!knnFrom.isKnned()) {
            clusterService.addQuery(knnFrom);
            long knnFromStart = System.nanoTime();
            knnFrom.knn();
            tmpOffSet += ((double) (System.nanoTime() - knnFromStart) / 1000);
            vertexFrom.setkCars(knnFrom.getKCars());
        }
        if (!knnTo.isKnned() && edge.getDis() != 0) {
            clusterService.addQuery(knnTo);
            long knnToStart = System.nanoTime();
            knnTo.knn();
            tmpOffSet += ((double) (System.nanoTime() - knnToStart) / 1000);
            vertexTo.setkCars(knnTo.getKCars());
        }

        long knnStart = System.nanoTime();
        edge.setTopKnn();
        double setTopKnnTime = (double) (System.nanoTime() - knnStart) / 1000;
        edge.setQueryTime(tmpOffSet + setTopKnnTime);
    }


    @Override
    public void initKnn(int queryName) {

    }

    @Override
    public void knnSearch(int queryName) {
//        initAhgKnn(queryName);
//        ahgKnn.knn();
//        if (ahgKnn.getTempInactiveCluster() != null) {
//            clusterService.activateCluster(ahgKnn.getTempInactiveCluster());
//        }
    }

    public List<ODINkNNEdge> getQueryEdges() {
        return this.queryEdges;
    }


    @Override
    public IndexType supportType() {
        return IndexType.ODIN;
    }

    @Override
    public KnnResultDTO buildResult(KnnDTO knnDTO) {
        return buildResult(new ODINkNN(1), knnDTO);
    }

}
