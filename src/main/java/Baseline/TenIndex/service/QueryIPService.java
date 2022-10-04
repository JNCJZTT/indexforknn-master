package Baseline.TenIndex.service;

import Baseline.TenIndex.domain.QueryIP;
import Baseline.TenIndex.domain.TenIndexVariable;
import Baseline.TenIndex.domain.TenIndexVertex;
import Baseline.TenIndex.service.graph.TenIndexVertexService;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IKnnService;
import Baseline.base.service.dto.KnnDTO;
import Baseline.base.service.dto.result.KnnResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/10/2 zhoutao
 */
@Service
public class QueryIPService implements IKnnService {
    @Autowired
    TenIndexVertexService vertexService;

    QueryIP tenStarKnn;

    public QueryIPService() {
        register();
    }

    @Override
    public void initKnn(int queryName) {
        TenIndexVertex vertex = TenIndexVariable.INSTANCE.getVertex(queryName);
        if (!vertex.isBuiltAncestor()) {
            vertexService.buildAncestor(vertex);
        }
        tenStarKnn = new QueryIP(queryName);

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
        return IndexType.TenIndex;
    }
}
