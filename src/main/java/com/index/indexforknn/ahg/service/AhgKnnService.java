package com.index.indexforknn.ahg.service;

import com.index.indexforknn.ahg.common.status.AhgActiveStatus;
import com.index.indexforknn.ahg.domain.AhgCluster;
import com.index.indexforknn.ahg.domain.AhgKnn;
import com.index.indexforknn.ahg.domain.AhgVariable;
import com.index.indexforknn.ahg.service.dto.result.AhgKnnResultDTO;
import com.index.indexforknn.ahg.service.graph.AhgClusterService;
import com.index.indexforknn.base.domain.Car;
import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.service.dto.KnnDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * AhgKnnService
 * 2022/4/15 zhoutao
 */
@Service
@Slf4j
public class AhgKnnService {
    AhgKnn ahgKnn;

    @Autowired
    AhgClusterService clusterService;

    /**
     * initAhgKnn
     *
     * @param queryName:查询点
     */
    private void initAhgKnn(int queryName) {
        ahgKnn = AhgKnn.builder()
                .kCars(new PriorityQueue<>(new Car.QueryDisComprator()))
                .queryArray(new int[GlobalVariable.VERTEX_NUM])
                .current(new PriorityQueue<>(Comparator.comparingInt(o -> ahgKnn.queryArray[o])))
                .queryName(queryName)
                .nearestName(queryName)
                .nearestVertex(AhgVariable.vertices.get(queryName))
                .tempInactiveCluster(null)
                .searchLimit(Integer.MAX_VALUE)
                .build();
        Arrays.fill(ahgKnn.queryArray, -1);
        ahgKnn.queryArray[queryName] = 0;

        // add Query into virtual map
        clusterService.addQuery(ahgKnn);
    }

    public void knnSearch(int queryName) {
        initAhgKnn(queryName);
        ahgKnn.knn();
        if (ahgKnn.getTempInactiveCluster() != null) {
            clusterService.activateCluster(ahgKnn.getTempInactiveCluster());
        }
    }

    public AhgKnnResultDTO buildResult(KnnDTO knnDTO) {
        AhgKnnResultDTO resultDTO = new AhgKnnResultDTO();
        resultDTO.buildResult(ahgKnn, knnDTO);
        return resultDTO;
    }

}
