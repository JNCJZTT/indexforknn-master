package Baseline.VTree.service;

import Baseline.VTree.domain.VTreekNN;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IKnnService;
import Baseline.base.service.dto.KnnDTO;
import Baseline.base.service.dto.result.KnnResultDTO;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 2022/9/18 zhoutao
 */
@Service
public class VTreeService implements IKnnService {
    VTreekNN knn;

    public VTreeService() {
        register();
    }

    @Override
    public void initKnn(int queryName) {
        knn = new VTreekNN(queryName);
    }

    @Override
    public void knnSearch(int queryName) {
        initKnn(queryName);
        knn.knn();
    }

    @Override
    public KnnResultDTO buildResult(KnnDTO knnDTO) {
        return buildResult(knn, knnDTO);
    }

    @Override
    public IndexType supportType() {
        return IndexType.VTree;
    }
}
