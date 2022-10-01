package com.index.indexforknn.sgrid.service;

import com.index.indexforknn.base.domain.GlobalVariable;
import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IndexService;
import com.index.indexforknn.base.service.dto.IndexDTO;
import com.index.indexforknn.base.service.dto.result.ResultDTO;
import com.index.indexforknn.sgrid.domain.SGridVariable;
import com.index.indexforknn.sgrid.service.graph.SGridActiveService;
import com.index.indexforknn.sgrid.service.graph.SGridClusterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.RamUsageEstimator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SgridIndexService
 * 2022/4/27 zhoutao
 */
@Service
@Slf4j
public class SGridIndexService extends IndexService {
    @Autowired
    private SGridClusterService clusterService;

    @Autowired
    private SGridActiveService activeService;

    public SGridIndexService() {
        register();
    }

    @Override
    protected void build() {
        clusterService.computeClusters();
        activeService.buildActive();

//        log.info("the cost memory of vertices:" + RamUsageEstimator.humanSizeOf(SGridVariable.INSTANCE.getVertices()));
//        log.info("the cost memory of clusters:" + RamUsageEstimator.humanSizeOf(SGridVariable.INSTANCE.getClusters()));
    }

    @Override
    protected void update() {
        // don't need do anything
    }

    @Override
    public IndexType supportType() {
        return IndexType.SGRID;
    }
}
