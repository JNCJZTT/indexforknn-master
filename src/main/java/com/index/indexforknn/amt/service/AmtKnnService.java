package com.index.indexforknn.amt.service;

import com.index.indexforknn.amt.domain.AmtKnn;
import com.index.indexforknn.base.service.dto.result.KnnResultDTO;
import com.index.indexforknn.amt.service.graph.AmtClusterService;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IKnnService;
import com.index.indexforknn.base.service.dto.KnnDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AhgKnnService
 * 2022/4/15 zhoutao
 */
@Service
@Slf4j
public class AmtKnnService implements IKnnService {
    AmtKnn amtKnn;

    @Autowired
    AmtClusterService clusterService;

    public AmtKnnService() {
        register();
    }

    /**
     * initAhgKnn
     *
     * @param queryName:查询点
     */
    @Override
    public void initAhgKnn(int queryName) {
        amtKnn = new AmtKnn(queryName);

        // add Query into virtual map
        clusterService.addQuery(amtKnn);
    }

    @Override
    public void knnSearch(int queryName) {
        initAhgKnn(queryName);
        amtKnn.knn();
        if (amtKnn.getTempInactiveCluster() != null) {
            clusterService.activateCluster(amtKnn.getTempInactiveCluster());
        }
    }

    @Override
    public IndexType supportType() {
        return IndexType.AHG;
    }

    @Override
    public KnnResultDTO buildResult(KnnDTO knnDTO) {
        return buildResult(amtKnn, knnDTO);
    }

}
