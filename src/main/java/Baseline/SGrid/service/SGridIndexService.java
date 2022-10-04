package Baseline.SGrid.service;

import Baseline.SGrid.service.graph.SGridActiveService;
import Baseline.SGrid.service.graph.SGridClusterService;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IndexService;
import lombok.extern.slf4j.Slf4j;
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
        return IndexType.SGrid;
    }
}
