package Baseline.SIMkNN;

import Baseline.SIMkNN.graph.SIMkNNActiceService;
import Baseline.SIMkNN.graph.SIMkNNClusterService;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SIMkNNIndexService extends IndexService {
    @Autowired
    private SIMkNNClusterService clusterService;

    @Autowired
    private SIMkNNActiceService activeService;



    public SIMkNNIndexService() {
        register();
    }

    @Override
    protected void build() {
        clusterService.computeClusters();
        activeService.buildActive();
    }

    @Override
    protected void update() {
        // don't need do anything
    }

    @Override
    public IndexType supportType() {
        return IndexType.SIMkNN;
    }
}
