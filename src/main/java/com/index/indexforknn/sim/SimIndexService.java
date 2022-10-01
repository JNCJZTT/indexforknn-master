package com.index.indexforknn.sim;

import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IndexService;
import com.index.indexforknn.sim.graph.SimActiceService;
import com.index.indexforknn.sim.graph.SimClusterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SimIndexService extends IndexService {
    @Autowired
    private SimClusterService clusterService;

    @Autowired
    private SimActiceService activeService;



    public SimIndexService() {
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
        return IndexType.SIM;
    }
}
