package Baseline.ERkNN;

import Baseline.ERkNN.graph.ERkNNActiceService;
import Baseline.ERkNN.graph.ERkNNClusterService;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ERkNNIndexService extends IndexService {
    @Autowired
    private ERkNNClusterService clusterService;

    @Autowired
    private ERkNNActiceService activeService;



    public ERkNNIndexService() {
        register();
    }

    @Override
    protected void build() {
        activeService.buildActive();
        clusterService.computeClusters();
    }

    @Override
    protected void update() {
        // don't need do anything
    }

    @Override
    public IndexType supportType() {
        return IndexType.ERkNN;
    }
}
