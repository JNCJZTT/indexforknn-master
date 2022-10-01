package com.index.indexforknn.tenstar.service.graph;

import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IVariableService;
import com.index.indexforknn.tenstar.service.TenStarIndexService;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/30 zhoutao
 */
@Service
public class TenStarVariableService implements IVariableService {

    public TenStarVariableService() {
        register();
    }

    @Override
    public void buildVertex(int vertexName, String clusterName) {

    }

    @Override
    public void buildEdge(int vertexName, String[] edgeInfo) {

    }

    @Override
    public IndexType supportType() {
        return IndexType.TENSTAR;
    }
}
