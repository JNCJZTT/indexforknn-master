package com.index.indexforknn.tenstar.service;

import com.index.indexforknn.base.domain.enumeration.IndexType;
import com.index.indexforknn.base.service.api.IKnnService;
import com.index.indexforknn.base.service.dto.KnnDTO;
import com.index.indexforknn.base.service.dto.result.KnnResultDTO;
import com.index.indexforknn.tenstar.domain.TenStarKnn;
import com.index.indexforknn.tenstar.domain.TenStarVariable;
import com.index.indexforknn.tenstar.domain.TenStarVertex;
import com.index.indexforknn.tenstar.service.graph.TenStarVertexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/10/2 zhoutao
 */
@Service
public class TenStarKnnService implements IKnnService {
    @Autowired
    TenStarVertexService vertexService;

    TenStarKnn tenStarKnn;

    public TenStarKnnService() {
        register();
    }

    @Override
    public void initKnn(int queryName) {
        TenStarVertex vertex = TenStarVariable.INSTANCE.getVertex(queryName);
        if (!vertex.isBuiltAncestor()) {
            vertexService.buildAncestor(vertex);
        }
        tenStarKnn = new TenStarKnn(queryName);

    }

    @Override
    public void knnSearch(int queryName) {
        initKnn(queryName);
        tenStarKnn.knn();
    }

    @Override
    public KnnResultDTO buildResult(KnnDTO knnDTO) {
        return buildResult(tenStarKnn, knnDTO);
    }

    @Override
    public IndexType supportType() {
        return IndexType.TENSTAR;
    }
}
