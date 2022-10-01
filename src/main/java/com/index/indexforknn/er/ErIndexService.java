package com.index.indexforknn.er;

import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IndexService;
import com.index.indexforknn.er.graph.ErActiceService;
import com.index.indexforknn.er.graph.ErClusterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ErIndexService extends IndexService {
    @Autowired
    private ErClusterService clusterService;

    @Autowired
    private ErActiceService activeService;



    public ErIndexService() {
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
        return IndexType.ER;
    }
}
