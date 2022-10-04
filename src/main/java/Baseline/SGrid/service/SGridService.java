package Baseline.SGrid.service;

import Baseline.SGrid.domain.SGridkNN;
import Baseline.base.domain.enumeration.IndexType;
import Baseline.base.service.api.IKnnService;
import Baseline.base.service.dto.KnnDTO;
import Baseline.base.service.dto.result.KnnResultDTO;
import org.springframework.stereotype.Service;

/**
 * SGridKnnService
 * 2022/5/14 zhoutao
 */
@Service
public class SGridService implements IKnnService {

    SGridkNN sgridKnn;

    public SGridService(){
        register();
    }

    @Override
    public void initKnn(int queryName) {
        sgridKnn = new SGridkNN(queryName);
    }

    @Override
    public void knnSearch(int queryName) {
        initKnn(queryName);
        sgridKnn.knn();
    }


    public KnnResultDTO buildResult(KnnDTO knnDTO) {
        return buildResult(sgridKnn, knnDTO);
    }

    @Override
    public IndexType supportType() {
        return IndexType.SGrid;
    }
}
