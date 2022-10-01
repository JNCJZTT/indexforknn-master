package com.index.indexforknn.ahg.service;

import com.index.indexforknn.ahg.domain.AhgKnn;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.domain.AhgVertex;
import com.index.indexforknn.ahg.service.dto.AhgKnnDTO;
import com.index.indexforknn.ahg.service.dto.result.AhgKnnEdge;
import com.index.indexforknn.base.service.dto.result.KnnResultDTO;
import com.index.indexforknn.ahg.service.graph.AhgClusterService;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IKnnService;
import com.index.indexforknn.base.service.dto.KnnDTO;
import com.index.indexforknn.base.service.utils.DistributionUtil;
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
public class AhgKnnService implements IKnnService {
//    AhgKnn ahgKnn;

    Map<Integer, AhgKnn> knnMap;
    private List<AhgKnnEdge> queryEdges;
    private boolean isInit = false;

    @Autowired
    AhgClusterService clusterService;

    public AhgKnnService() {
        register();
    }

    /**
     * initAhgKnn
     */
    public void initAhgKnn(AhgKnnDTO knnDTO) {
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
            AhgKnnEdge edge = new AhgKnnEdge(DistributionUtil.getEdge());
            knnMap.put(edge.getFrom(), new AhgKnn(edge.getFrom()));
            knnMap.put(edge.getTo(), new AhgKnn(edge.getTo()));
            queryEdges.add(edge);
        }
        isInit = true;
    }

    public void batchAhgKnnSerach() {
        for (AhgKnnEdge edge : queryEdges) {
            AhgKnnSearch(edge);
        }
    }

    public void updateKnnPosition() {
        queryEdges.parallelStream().forEach(AhgKnnEdge::update);
    }

    public void AhgKnnSearch(AhgKnnEdge edge) {
        AhgVertex vertexFrom = AhgVariable.INSTANCE.getVertex(edge.getFrom());
        AhgVertex vertexTo = AhgVariable.INSTANCE.getVertex(edge.getTo());
        if (!knnMap.containsKey(edge.getFrom())) {
            knnMap.put(edge.getFrom(), new AhgKnn(edge.getFrom()));
        }
        if (!knnMap.containsKey(edge.getTo())) {
            knnMap.put(edge.getTo(), new AhgKnn(edge.getTo()));
        }
        AhgKnn knnFrom = knnMap.get(edge.getFrom());
        AhgKnn knnTo = knnMap.get(edge.getTo());

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

    public List<AhgKnnEdge> getQueryEdges() {
        return this.queryEdges;
    }


    @Override
    public IndexType supportType() {
        return IndexType.AHG;
    }

    @Override
    public KnnResultDTO buildResult(KnnDTO knnDTO) {
        return buildResult(new AhgKnn(1), knnDTO);
    }

}
